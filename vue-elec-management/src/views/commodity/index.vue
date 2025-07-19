<script setup>
  import { ref, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { queryPageApi, addApi, queryInfoApi, updateApi, deleteApi } from '@/api/commodity'
  import { queryAllApi as queryAllBrandApi } from '@/api/brand'

  //品牌列表数据
  const brandList = ref([])

  // 示例数据
  const commodityList = ref([])

  const searchCommodity = ref({
    name: '',
    minPrice: '', // 新增最低价格字段
    maxPrice: '', // 新增最高价格字段
  })

  // // 侦听 searchCommodity 中的价格属性
  // watch(
  //   [() => searchCommodity.value.minPrice, () => searchCommodity.value.maxPrice],
  //   ([newMinPrice, newMaxPrice], [oldMinPrice, oldMaxPrice]) => {
  //     // 检查价格是否合法，确保最低价格不大于最高价格
  //     if (newMinPrice && newMaxPrice && Number(newMinPrice) > Number(newMaxPrice)) {
  //       console.warn('最低价格不能大于最高价格')
  //       // 可以选择重置最高价格或者最低价格，这里重置最高价格
  //       searchCommodity.value.maxPrice = newMinPrice
  //     }
  //   },
  //   { deep: true }
  // )
  const token = ref('')

  onMounted(async () => {
    search()

    //加载所有品牌数据
    try {
      const result = await queryAllBrandApi('','',1,30);
      console.log('品牌数据接口响应:', result);
      if (result.code && result.data && Array.isArray(result.data.rows)) {
        // 从 result.data.rows 中获取品牌数据
        brandList.value = result.data.rows;
        console.log('brandList:', brandList.value);
        brandList.value.forEach(brand => {
          if (brand.id === undefined || brand.brandName === undefined) {
            console.warn('品牌数据格式错误:', brand);
          }
        });
      } else {
        console.error('未获取到有效的品牌数据数组:', result.data);
      }
    } catch (error) {
      console.error('加载品牌数据失败:', error);
      //ElMessage.error('加载品牌数据失败');
    }

    getToken()
  })

  //获取token
  const getToken = () => {
    const loginUser = JSON.parse(localStorage.getItem('loginUser'));
    if(loginUser && loginUser.token){
      token.value = loginUser.token;
    }
  }

  //查询
  const search= async () => {
    console.log('Search:', searchCommodity.value)
    const { minPrice, maxPrice } = searchCommodity.value;
    // 将 currentPage.value 和 pageSize.value 转换为 number 类型
    if (minPrice & maxPrice & minPrice!= '' && maxPrice != '' && Number(minPrice) > Number(maxPrice)) {
      ElMessage.error('最低价格不能大于最高价格')
      return
    }
    const result = await queryPageApi(
      searchCommodity.value.name,
      searchCommodity.value.minPrice,
      searchCommodity.value.maxPrice,
      Number(currentPage.value),
      Number(pageSize.value)
    );
    if(result.code){
      commodityList.value = result.data.rows
      total.value = result.data.total
    }
  }

  const clear = () => {
    // 清空表单
    searchCommodity.value = {
      name: '',
      minPrice: '', // 重置最低价格
      maxPrice: '', // 重置最高价格
    }
    search()
  }



  // 分页配置
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

  //新增商品
  const addCommodity = () => {
    dialogVisible.value = true
    dialogTitle.value = '新增商品'
  }


  //新增/修改表单
  const commodityFormRef = ref(null)
  const commodity = ref({
    name: '',
    image: '',
    price: '',
    description: '',
    brandId: '',
    sales: '',
    stock: '',
    status: ''
  })

  commodity.value = {
    name: '',
    image: '',
    price: '',
    description: '',
    brandId: '',
    sales: '',
    stock: '',
    status: ''
  }
  if(commodityFormRef.value){
    commodityFormRef.value.resetFields()
  }

  // 验证规则
  const rules = ref({
    name: [
      { required: true, message: '请输入商品名', trigger: 'blur' },
      { min: 2, max: 20, message: '用户名长度应在2到20个字符之间', trigger: 'blur' }
    ],
    
    status: [
      { required: true, message: '请选择状态', trigger: 'change' }
    ]
  });

  // 控制弹窗
  const dialogVisible = ref(false)
  const dialogTitle = ref('新增商品')

  //文件上传
  // 图片上传成功后触发
  const handleAvatarSuccess = (response,uploadFile) => {
    commodity.value.image = response.data
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
    commodityFormRef.value.validate(async valid => {
      if (valid) {
        let result;
        if(commodity.value.id){ //修改
          result = await updateApi(commodity.value);
        }else { //新增
          result = await addApi(commodity.value);
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

  //编辑
  const edit = async (id) => {
    const result = await queryInfoApi(id);
    if(result.code){
      dialogVisible.value = true;
      dialogTitle.value = '修改商品';
      commodity.value = result.data;
    }
  }

  //删除
  const deleteById = (id) => {
    //弹出确认框
    ElMessageBox.confirm('您确认删除该商品吗?','提示',
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

  const selectedIds = ref([]);
  //复选框勾选发生变化时触发 - selection: 当前选中的记录 (数组)
  const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map( item => item.id);
  }

  //批量删除
  const deleteByIds = () => {
    //弹出确认框
    ElMessageBox.confirm('您确认删除该商品吗?','提示',
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
  <h1>商品管理</h1> <br>
  <el-form :inline="true" :model="searchCommodity">
    <el-form-item label="名称" >
      <el-input v-model="searchCommodity.name" placeholder="请输入商品名称" style="width: 150px;"></el-input>
    </el-form-item>
    <!-- 价格范围输入框 -->
    <el-form-item label="价格范围">
      <el-input v-model="searchCommodity.minPrice" placeholder="最低价格" style="width: 100px;"></el-input>
      <span style="margin: 0 5px;">-</span>
      <el-input v-model="searchCommodity.maxPrice" placeholder="最高价格" style="width: 100px;"></el-input>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="clear">清空</el-button>
    </el-form-item>
  </el-form>

  <el-button type="primary" @click="addCommodity"> + 新增商品</el-button>
  <el-button type="danger" @click="deleteByIds"> - 批量删除</el-button>
  <br><br>

<!-- 表格 -->
  <el-table :data="commodityList" border style="width: 100%" @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" align="center"></el-table-column>
    <el-table-column prop="name" label="名称" width="170" align="center"></el-table-column>
    <el-table-column label="图片" width="170" align="center">
      <template #default="scope" >
        <img :src="'http://127.0.0.1:8081/images/'+scope.row.image" alt="Avatar" class="avatar" />
      </template>
    </el-table-column>
    <el-table-column prop="price" label="价格" width="120" align="center"></el-table-column>
    <el-table-column prop="description" label="描述" width="170" align="center"></el-table-column>
    <el-table-column prop="brandId" label="品牌ID" width="100" align="center"></el-table-column>
    <el-table-column prop="sales" label="销量" width="120" align="center"></el-table-column>
    <el-table-column prop="stock" label="库存" width="120" align="center"></el-table-column>
    <el-table-column label="状态" width="120" align="center">
      <template #default="scope">
          <span v-if="scope.row.status == 1">上架</span>
          <span v-else-if="scope.row.status == 2">下架</span>
          <span v-else>其他</span>
        </template>
    </el-table-column>
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

  <!-- 新增/修改员工的对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form ref="commodityFormRef" :model="commodity" :rules="rules" label-width="80px">
        <!-- 基本信息 -->
        <!-- 第一行 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="commodity.name" placeholder="请输入商品名称，2-20个字"></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="价格">
              <el-input v-model="commodity.price" placeholder="请输入商品价格"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="commodity.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="上架" value="1"></el-option>
                <el-option label="下架" value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="库存">
              <el-input v-model="commodity.stock" placeholder="请输入库存数量"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第三行 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌">
               <el-select
                  v-model="commodity.brandId"
                  placeholder="请选择品牌"
                  style="width: 100%;"
                  filterable
                  allow-create
                  default-first-option
                >
                <el-option 
                  v-for="brand in brandList" 
                  :key="brand.id" 
                  :label="brand.brandName" 
                  :value="brand.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="销量">
              <el-input v-model="commodity.sales" placeholder="请输入销量"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第四行 -->
        <el-row :gutter="20">          
          <el-col :span="12">
            <el-form-item label="描述">
              <el-input v-model="commodity.description" placeholder="请输入描述"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第五行 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="图片">
              <el-upload
                class="avatar-uploader"
                action="/api/upload"
                :headers="{'token': token}"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                >
                <img v-if="commodity.image" :src="'http://127.0.0.1:8081/images/'+commodity.image" class="avatar" />
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