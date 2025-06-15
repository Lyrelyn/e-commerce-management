<script setup>
  import { ref, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { queryPageApi, addApi, queryInfoApi, deleteApi, updateApi } from '@/api/customer'

  //客户列表数据
  const customerList = ref([])

  const searchCustomer = ref({
    username: '',
    gender: '',
  })

  //查询员工
  const search= async () => {
    console.log('Search:', searchCustomer.value)
    const result = await queryPageApi(searchCustomer.value.username, searchCustomer.value.gender, currentPage.value, pageSize.value);
    if(result.code){
      console.log('查询成功', result.data)
      customerList.value = result.data.rows
      total.value = result.data.total
    }
  }

  const clear = () => {
    // 清空表单
    searchCustomer.value = {
      username: '',
      gender: '',
    }
    search()
  }

  //声明token
  const token = ref('')

  onMounted(async () => {
    search()
    getToken()
  })

  //获取token
  const getToken = () => {
    const loginUser = JSON.parse(localStorage.getItem('loginUser'));
    if(loginUser && loginUser.token){
      token.value = loginUser.token;
    }
  }

  // 分页配置
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)

  // 分页处理
  const handleSizeChange = (val) => {
    search()
  }
  const handleCurrentChange = (val) => {
    search()
  }

  //新增
  const addCustomer = () => {
    dialogVisible.value = true
    dialogTitle.value = '新增客户'
  }


  //新增/修改表单
  const customerFormRef = ref(null)
  const customer = ref({
    username: '',
    gender: '',
    image: '',
    phone: '',
    email: '',
    updatedTime: '',
    level: ''
  })

  customer.value = {
    username: '',
    gender: '',
    image: '',
    phone: '',
    email: '',
    updatedTime: '',
    level: ''
  }
  if(customerFormRef.value){
    customerFormRef.value.resetFields()
  }

  // 验证规则
  const rules = ref({
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 2, max: 20, message: '用户名长度应在2到20个字符之间', trigger: 'blur' }
    ],
    
    gender: [
      { required: true, message: '请选择性别', trigger: 'change' }
    ],
    phone: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      /**
       * 正则表达式: / ..... / ;  ^ : 以...开始 ;  $ : 以 ... 结束
       * [3-9] : 范围 3-9 之间
       * \d : 数字, [0-9]
       * {9} : 量词
       */
      { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
    ]
  });
  
  // 控制弹窗
  const dialogVisible = ref(false)
  const dialogTitle = ref('新增客户')

  //文件上传
  // 图片上传成功后触发
  const handleAvatarSuccess = (response,uploadFile) => {
    console.log('图片上传成功', response.data)
    customer.value.image = response.data
  }
  // 文件上传之前触发
  const beforeAvatarUpload = (rawFile) => {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
      ElMessage.error('只支持上传图片')
      return false
    } else if (rawFile.size / 1024 / 1024 > 10) {
      ElMessage.error('只能上传10M以内图片')
      return false
    }
    return true
  }

  const save = async () => {
    customerFormRef.value.validate(async valid => {
      if (valid) {
        let result;
        if(customer.value.id){ //修改
          result = await updateApi(customer.value);
        }else { //新增
          result = await addApi(customer.value);
        }
        if(result.code){
          ElMessage.success('保存成功')
          dialogVisible.value = false
          search()
        }else {
          ElMessage.error(result.msg)
        };
      } else { //不通过
        ElMessage.error('表单校验不通过');
      }
    })
  }

  const edit = async (id) => {
    const result = await queryInfoApi(id);
    if(result.code){
      dialogVisible.value = true;
      dialogTitle.value = '修改客户';
      customer.value = result.data;
    }
  }

  //删除
  const deleteById = (id) => {
    //弹出确认框
    ElMessageBox.confirm('您确认删除该客户吗?','提示',
      { confirmButtonText: '确认',cancelButtonText: '取消',type: 'warning'}
    ).then(async () => { //确认
      const result = await deleteApi(id);
      if(result.code){
        ElMessage.success('删除成功');
        search();
      }else{
        ElMessage.error(result.msg);
      }
    }).catch(() => { //取消
      ElMessage.info('您已取消删除');
    })
  }

  //记录勾选的员工的id
  const selectedIds = ref([]);
  //复选框勾选发生变化时触发 - selection: 当前选中的记录 (数组)
  const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map( item => item.id);
  }

  //批量删除
  const deleteByIds = () => {
    //弹出确认框
    ElMessageBox.confirm('您确认删除该客户吗?','提示',
      { confirmButtonText: '确认',cancelButtonText: '取消',type: 'warning'}
    ).then(async () => { //确认
      if(selectedIds.value && selectedIds.value.length > 0){
        const result = await deleteApi(selectedIds.value);
        if(result.code){
          ElMessage.success('删除成功');
          search();
        }else{
          ElMessage.error(result.msg);
        }
      }else {
        ElMessage.info('您没有选择任何要删除的数据');
      }
    }).catch(() => { //取消
      ElMessage.info('您已取消删除');
    })
  }
  

</script>

<template>
  <h1>客户管理</h1> <br>
  <el-form :inline="true" :model="searchCustomer">
    <el-form-item label="名称" >
      <el-input v-model="searchCustomer.username" placeholder="请输入客户名" style="width: 150px;"></el-input>
    </el-form-item>

    <el-form-item label="性别">
      <el-select v-model="searchCustomer.gender" placeholder="请选择">
        <el-option label="男" value="0"></el-option>
        <el-option label="女" value="1"></el-option>
      </el-select>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="clear">清空</el-button>
    </el-form-item>
  </el-form>

  <el-button type="primary" @click="addCustomer"> + 新增客户</el-button>
  <el-button type="danger" @click="deleteByIds"> - 批量删除</el-button>
  <br><br>

<!-- 表格 -->
  <el-table :data="customerList" border style="width: 100%" @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" align="center"></el-table-column>
    <el-table-column prop="id" label="ID" width="100" align="center"></el-table-column>
    <el-table-column prop="username" label="用户名" width="150" align="center"></el-table-column>
    <el-table-column label="性别" width="100" align="center">
      <template #default="scope">
          <span v-if="scope.row.gender == 0">男</span>
          <span v-else-if="scope.row.gender == 1">女</span>
          <span v-else>其他</span>
        </template>
    </el-table-column>
    <el-table-column label="头像" width="170" align="center">
      <template #default="scope" >
        <img :src=" 'http://127.0.0.1:8081/images/' + scope.row.image" alt="Avatar" class="avatar" />
      </template>
    </el-table-column>
    <el-table-column prop="level" label="级别" width="100" align="center">
      <template #default="scope">
            <span v-if="scope.row.level == 0">普通</span>
            <span v-else-if="scope.row.level == 1">VIP</span>
            <span v-else-if="scope.row.level == 2">SVIP</span>
            <span v-else>其他</span>
      </template>
    </el-table-column>
    <el-table-column prop="phone" label="电话" width="170" align="center"></el-table-column>
    <el-table-column prop="email" label="邮箱" width="170" align="center"></el-table-column>
    <el-table-column prop="updatedTime" label="最后操作时间" width="170" align="center"></el-table-column>

    <el-table-column label="操作" fixed="right" align="center">
      <template #default="scope">
        <el-button size="small" type="primary" @click="edit(scope.row.id)">编辑</el-button>
        <el-button size="small" type="danger" @click="deleteById(scope.row.id)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <br>

  <!-- 分页 -->
  <el-pagination
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :page-sizes="[10, 20, 30, 40]"
    layout="total, sizes, prev, pager, next, jumper"
    :total="total"
  >
  </el-pagination>

  <!-- 新增/修改的对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form ref="customerFormRef" :model="customer" :rules="rules" label-width="80px">
        <!-- 基本信息 -->
        <!-- 第一行 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="username">
              <el-input v-model="customer.username" placeholder="请输入客户名称，2-20个字"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="customer.phone" placeholder="请输入电话号码"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="customer.gender" placeholder="请选择性别" style="width: 100%;">
                <el-option label="男" value="0"></el-option>
                <el-option label="女" value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="customer.email" placeholder="请输入邮箱号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 第三行 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="住址" prop="address">
              <el-input v-model="customer.address" placeholder="请输入地址信息"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="会员等级" prop="remark">
              <el-select v-model="customer.level" placeholder="请输入会员等级">
                <el-option label="普通" value="0"></el-option>
                <el-option label="VIP" value="1"></el-option>
                <el-option label="SVIP" value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
              
        </el-row>

        <!-- 第行 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                action="/api/upload"
                :headers="{'token': token}"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                >
                <img v-if="'http://127.0.0.1:8081/images/' + customer.image" :src="'http://127.0.0.1:8081/images/'+customer.image" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row> 
      </el-form>
      
        <!-- 底部按钮 -->
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="save">保存</el-button>
          </span>
        </template>

    </el-dialog>

</template>

<style scoped>
  .avatar {
    height: 40px;
  }

  .avatar-uploader .avatar {
    width: 78px;
    height: 78px;
    display: block;
  }
  .avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  .avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
  }

  .el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 78px;
    height: 78px;
    text-align: center;
    /* 添加灰色的虚线边框 */
    border: 1px dashed var(--el-border-color);
  }
</style>