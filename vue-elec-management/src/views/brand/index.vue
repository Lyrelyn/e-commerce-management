<script setup>
  import { ref, onMounted } from 'vue'
  import { ElMessage, ElMessageBox, imageEmits } from 'element-plus'
  import {queryAllApi, addBrandApi, queryInfoApi, updateBrandApi, deleteBrandApi} from '@/api/brand'

  //声明列表展示数据
  let brandList= ref([])

  const searchBrand = ref({
    brandName: '',
    companyName: ''
  })


  //查询
  const search= async () => {
    console.log('Search:', searchBrand.value)
    const result = await queryAllApi(searchBrand.value.brandName, searchBrand.value.companyName, currentPage.value, pageSize.value);
    if(result.code){
      brandList.value = result.data.rows
      total.value = result.data.total
    }
  }

  const clear = () => {
    // 清空表单
    searchBrand.value = {
      brandName: '',
      companyName: ''
    }
    search()
  }

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

  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  // 分页处理
  const handleSizeChange = (val) => {
    pageSize.value = val;
    console.log('Page Size changed to:', val); // 输出更新后的每页数量
    search();
  };
  const handleCurrentChange = (val) => {
    currentPage.value = val;
    console.log('Current Page changed to:', val); // 输出更新后的页码
    search();
  };

  // 编辑品牌 - 根据ID查询回显数据
  const handleEdit = async (id) => {
    console.log(`Edit item with ID ${id}`);
    formTitle.value = '修改品牌'
    showDialog.value = true
    brandForm.value = {brandName: '', companyName: '', description: ''}

    const result = await queryInfoApi(id)
    if(result.code){
      brandForm.value = result.data
    }
  };

  // 删除品牌 - 根据ID删除
  const handleDelete = (ids) => {
    console.log(`Delete item with ID ${ids}`);
    // 在这里实现删除功能
    //删除时, 需要弹出一个确认框, 如果是确认, 则删除
    ElMessageBox.confirm('此操作将永久删除该品牌, 是否继续?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      // 删除
      const result = await deleteBrandApi(ids)
      if(result.code){
        ElMessage.success('删除成功')
        search()
      }
    })  
  };

  const selectedIds = ref([]);
  //复选框勾选发生变化时触发 - selection: 当前选中的记录 (数组)
  const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map( item => item.id);
  }

  //批量删除
  const deleteByIds = () => {
    //弹出确认框
    ElMessageBox.confirm('您确认删除该品牌吗?','提示',
      { confirmButtonText: '确认',cancelButtonText: '取消',type: 'warning'}
    ).then(async () => { //确认
      if(selectedIds.value && selectedIds.value.length > 0){
        const result = await deleteBrandApi(selectedIds.value);
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
      console.log('取消删除原因:', error);
      ElMessage.info('您已取消删除');
    })
  }

  const formTitle = ref('')
  //新增
  const add = () => {
    formTitle.value = '新增品牌'
    showDialog.value = true
    brandForm.value = {brandName: '', companyName: '', description: '', image: ''}
  }

  // 新增对话框的状态
  const showDialog = ref(false)
  // 表单数据
  const brandForm = ref({name: '', companyName: '',description: '', image: ''})
  // 表单验证规则
  const formRules = ref({
    name: [
      { required: true, message: '请输入品牌名称', trigger: 'blur' },
      { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
    ]
  })

  // 表单标签宽度
  const formLabelWidth = ref('120px')
  // 表单引用
  const brandFormRef = ref(null)

  // 重置表单
  const resetForm = () => {
    brandFormRef.value.resetFields()
  }

  // 提交表单
  const save = async () => {
    await brandFormRef.value.validate(async valid => {
      if (!valid) return
      // 提交表单
      let result = null;
      if(brandForm.value.id){
        result = await updateBrandApi(brandForm.value) // 修改
      }else {
        result = await addBrandApi(brandForm.value) // 新增
      } 
      if(result.code){
        ElMessage.success('操作成功')
        // 关闭对话框
        showDialog.value = false
        // 重置表单
        resetForm()
        // 重新加载数据
        search()
      }else {
        ElMessage.error(result.msg)
      }
    })
  }

  // 图片上传成功后触发
  const handleAvatarSuccess = (response,uploadFile) => {
    brandForm.value.image = response.data
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

</script>

<template>
  <h1>品牌管理</h1> <br>
  
  <el-form :inline="true" :model="searchBrand">
    <el-form-item label="品牌名称">
      <el-input v-model="searchBrand.brandName" placeholder="请输入品牌名称" style="width: 150px;"></el-input>
    </el-form-item>
    <el-form-item label="公司名称" >
      <el-input v-model="searchBrand.companyName" placeholder="请输入公司名称" style="width: 150px;"></el-input>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="clear">清空</el-button>
    </el-form-item>
  </el-form>

  <el-button type="primary" @click="add"> + 新增品牌</el-button>
  <el-button type="danger" @click="deleteByIds"> - 批量删除</el-button>
  <br><br>
  
  <el-table :data="brandList" border style="width: 100%" @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" align="center"></el-table-column>
    <el-table-column type="index" label="序号" width="100" align="center"/>
    <el-table-column label="图片" width="170" align="center">
      <template #default="scope" >
        <img :src="'http://127.0.0.1:8081/images/'+scope.row.image" alt="Avatar" class="avatar" />
      </template>
    </el-table-column>
    <el-table-column prop="brandName" label="品牌名称" width="300" align="center"/>
    <el-table-column prop="companyName" label="公司名称" width="300" align="center"/>
    <el-table-column prop="description" label="描述" width="300" align="center">
    </el-table-column>   
    <el-table-column fixed="right" label="操作" align="center">
      <template #default="scope">
        <el-button size="small" @click="handleEdit(scope.row.id)">修改</el-button>
        <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-dialog v-model="showDialog" :title="formTitle" width="30%" @close="resetForm">
    <el-form :model="brandForm" :rules="formRules" ref="brandFormRef">
      <el-form-item label="品牌名称" prop="brandName" label-width="80px">
        <el-input v-model="brandForm.brandName" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="公司名称" prop="companyName" label-width="80px">
        <el-input v-model="brandForm.companyName" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description" label-width="80px">
        <el-input v-model="brandForm.description" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="图片" prop="image" label-width="80px">
        <el-upload
          class="avatar-uploader"
          action="/api/upload"
          :show-file-list="false"
          :headers="{'token': token}"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
          >
              <img v-if="brandForm.image" :src="'http://127.0.0.1:8081/images/'+brandForm.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </span>
    </template>
  </el-dialog>


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