<script setup>
  import { ref, onMounted, watch } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { queryPageApi, addApi, queryInfoApi, deleteApi, updateApi } from '@/api/order'

  //客户列表数据
  const orderList = ref([])

  const searchOrder = ref({
    orderId: '',
    customerName: ''
  })

  //查询员工
  const search= async () => {
    console.log('Search:', searchOrder.value)
    const result = await queryPageApi(searchOrder.value.orderId, searchOrder.value.customerName, currentPage.value, pageSize.value);
    if(result.code){
      orderList.value = result.data.rows
      total.value = result.data.total
    }
  }

  const clear = () => {
    // 清空表单
    searchOrder.value = {
      orderId: '',
      customerName: ''
    }
    search()
  }

  onMounted(async () => {
    search()
  })

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
  const addOrder = () => {
    dialogVisible.value = true
    dialogTitle.value = '新增订单'
  }

  //表单校验规则

  // 自定义校验规则
  const validateOrderItemList = (rule, value, callback) => {
    if (value.length === 0) {
      callback(new Error('请至少添加一个订单项目'))
    } else {
      callback()
    }
  }
  // 验证规则
  const rules = ref({
    orderId: [
      { required: true, message: '请输入订单号', trigger: 'blur' },
    ],
    customerName: [
      { required: true, message: '请输入客户名', trigger: 'blur' },
      { min: 2, max: 20, message: '用户名长度应在2到20个字符之间', trigger: 'blur' }
    ],
    status: [
      { required: true, message: '请选择状态', trigger: 'change' }
    ],
    customerPhone: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1\d{10}$/g, message: '请输入有效的手机号', trigger: 'blur' }
    ],
    orderItemList: [
      { validator: validateOrderItemList, trigger:  ['change', 'submit'] }
    ]
  });

    //新增/修改表单
  const orderFormRef = ref(null)
  const order = ref({
    orderId: '',
    customerName: '',
    customerPhone: '',
    totalPrice: 0, // 初始化为 0
    status: '',
    orderItemList: []
  })

  // 计算总金额的函数
  const calculateTotalPrice = () => {
    const total = order.value.orderItemList.reduce((sum, item) => {
      const price = parseFloat(item.price) || 0
      const number = parseInt(item.number) || 0
      return sum + price * number
    }, 0)
    order.value.totalPrice = total
  }

  // 监听订单项目列表的变化，重新计算总金额
  watch(() => order.value.orderItemList, () => {
    calculateTotalPrice()
  }, { deep: true })

  // 订单项目
  // 动态添加 .
  const addOrderItem = () => {
    order.value.orderItemList.push({commodityName: '', number: '', price: ''})
  }

  // 动态删除订单项目 .
  const delOrderItem = (index) => {
    order.value.orderItemList.splice(index, 1)
    calculateTotalPrice() // 删除后重新计算总金额
  }

  order.value = {
    orderId: '',
    customerName: '',
    customerPhone: '',
    totalPrice: '',
    status: '',
    orderItemList: []
  }
  if(orderFormRef.value){
    orderFormRef.value.resetFields()
  }

  // 控制弹窗
  const dialogVisible = ref(false)
  const dialogTitle = ref('新增订单')

  const save = async () => {
    orderFormRef.value.validate(async valid => {
      if (valid) {
        let result;
        if(order.value.id){ //修改
          result = await updateApi(order.value);
        }else { //新增
          result = await addApi(order.value);
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
      dialogTitle.value = '修改订单';
      order.value = result.data;
      calculateTotalPrice() // 编辑时重新计算总金额
    }
  }

  //删除
  const deleteById = (id) => {
    //弹出确认框
    ElMessageBox.confirm('您确认删除该订单吗?','提示',
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

  //记录勾选的订单的id
  const selectedIds = ref([]);
  //复选框勾选发生变化时触发 - selection: 当前选中的记录 (数组)
  const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map( item => item.id);
  }

  //批量删除
  const deleteByIds = () => {
    //弹出确认框
    ElMessageBox.confirm('您确认删除该订单吗?','提示',
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
  <h1>订单管理</h1> <br>
  <el-form :inline="true" :model="searchOrder">
    <el-form-item label="订单号">
      <el-input v-model="searchOrder.orderId" placeholder="请输入订单号" style="width: 150px;"></el-input>
    </el-form-item>
    <el-form-item label="客户名称" >
      <el-input v-model="searchOrder.customerName" placeholder="请输入客户名" style="width: 150px;"></el-input>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="clear">清空</el-button>
    </el-form-item>
  </el-form>

  <el-button type="primary" @click="addOrder"> + 新增订单</el-button>
  <el-button type="danger" @click="deleteByIds"> - 批量删除</el-button>
  <br><br>

  <!-- 表格 -->
  <el-table :data="orderList" border style="width: 100%" @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" align="center"></el-table-column>
    <el-table-column prop="id" label="序号" width="100" align="center"></el-table-column>
    <el-table-column prop="orderId" label="订单号" width="170" align="center"></el-table-column>
    <el-table-column prop="customerName" label="客户名称" width="170" align="center"></el-table-column>
    <el-table-column prop="customerPhone" label="客户电话" width="170" align="center"></el-table-column>
    <el-table-column prop="totalPrice" label="总金额" width="170" align="center"></el-table-column>
    <el-table-column label="状态" width="120" align="center">
      <template #default="scope">
          <span v-if="scope.row.status == 0">待支付</span>
          <span v-else-if="scope.row.status == 1">已支付</span>
          <span v-else-if="scope.row.status == 2">已取消</span>
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
      <el-form ref="orderFormRef" :model="order" :rules="rules" label-width="80px">
        <!-- 基本信息 -->
        <!-- 第一行 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单号" prop="orderId">
              <el-input v-model="order.orderId" placeholder="请输入订单号"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="order.customerName" placeholder="请输入客户名称，2-20个字"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="order.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="待支付" value="0"></el-option>
                <el-option label="已支付" value="1"></el-option>
                <el-option label="已取消" value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="电话" prop="customerPhone">
              <el-input v-model="order.customerPhone" placeholder="请输入客户电话号码"></el-input>
            </el-form-item>
          </el-col>
          
        </el-row>

        <!-- 第三行 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-col :span="12">
              <el-form-item label="总金额" prop="totalPrice">
                <!-- 设置输入框为只读 -->
                <el-input v-model="order.totalPrice" placeholder="请输入总金额" readonly></el-input>
              </el-form-item>
            </el-col>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="order.remark" placeholder="请输入备注信息" type="textarea" :autosize="{ minRows: 2, maxRows: 4 }"></el-input>
            </el-form-item>
          </el-col>   
        </el-row>

        <!-- 订单项目 -->
        <!-- 第六行 -->
        <el-row :gutter="10">
          <el-col :span="24">
            <el-form-item label="订单项目">
              <el-button type="success" size="small" @click="addOrderItem">+ 添加订单项目</el-button>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 第七行 ...  订单项目 -->
        <el-row :gutter="3" v-for="(oi, index) in order.orderItemList">

          <el-col :span="6">
            <el-form-item size="small" label="商品名称" label-width="60px">
              <el-input placeholder="请输入商品名称" v-model="oi.commodityName"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item size="small" label="数量" label-width="60px">
              <el-input placeholder="请输入数量" v-model="oi.number"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item size="small" label="金额" label-width="60px">
              <el-input placeholder="请输入商品金额" v-model="oi.price"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="2">
            <el-form-item size="small" label-width="0px">
              <el-button type="danger" @click="delOrderItem(index)">- 删除</el-button>
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

</style>