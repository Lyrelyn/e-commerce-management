
<script setup>
  import { ref, watch, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { queryPageApi, addApi, queryInfoApi, updateApi, deleteApi } from '@/api/delivery'

  // 示例数据
  const deliveryList = ref([])

  const searchDelivery = ref({
    orderId: '', // 订单ID
    courierName: '', // 快递员姓名
    date: [],
    begin: '', // 开始时间
    end: '' // 结束时间
  })

  //侦听searchDelivery中的date属性
  watch(
    () => searchDelivery.value.date,
    (newValue, oldValue) => {
      if(newValue.length == 2){
        searchDelivery.value.begin = newValue[0]
        searchDelivery.value.end = newValue[1]
      }else {
        searchDelivery.value.begin = ''
        searchDelivery.value.end = ''
      }
    }
  )

  onMounted(async () => {
    search()
  })

  //查询
  const search= async () => {
    console.log('Search:', searchDelivery.value)
  
    const result = await queryPageApi(
      searchDelivery.value.orderId,
      searchDelivery.value.courierName,
      searchDelivery.value.begin,
      searchDelivery.value.end,
      currentPage.value,
      pageSize.value
    );
    if(result.code){
      deliveryList.value = result.data.rows
      total.value = result.data.total
    }
  } 

  const clear = () => {
    // 清空表单
    searchDelivery.value = {
      orderId: '', // 订单ID
      courierName: '', // 快递员姓名
      date: [],
      begin: '', // 开始时间
      end: '' // 结束时间
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
    const addDelivery = () => {
      dialogVisible.value = true
      dialogTitle.value = '新增配送'
  }

    // 验证规则
    // 自定义验证函数，检查到达日期是否在发货日期之后
    const validateArrivalTime = (rule, value, callback) => {
      const deliveryTime = delivery.value.deliveryTime;
      if (deliveryTime && value) {
        const deliveryDate = new Date(deliveryTime);
        const arrivalDate = new Date(value);
        if (arrivalDate < deliveryDate) {
          callback(new Error('预计到达日期必须在发货日期之后'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    
    const rules = ref({
      orderId: [
        { required: true, message: '请输入订单号', trigger: 'blur' },
      ],
      courierName: [
        { required: true, message: '请输入快递员姓名', trigger: 'blur' }, 
      ],
      courierPhone: [
        { required: true, message: '请输入快递员电话', trigger: 'blur' }, 
      ],
      arrivalTime: [
        { required: true, message: '请输入预计到达日期', trigger: 'blur' },
        // 添加自定义验证规则
        { validator: validateArrivalTime, trigger: 'change' } 
      ]
    });

  //新增/修改表单
  const deliveryFormRef = ref(null)
  const delivery = ref({
    orderId: '', // 订单ID
    courierName: '', // 快递员姓名
    courierPhone: '', // 快递员电话
    courierCompany: '', // 快递公司
    location: '', // 当前位置
    deliveryAddress: '', // 收货地址
    status: '', // 状态（0：未发货，1：已发货，2：已送达）
    deliveryTime: '', // 发货日期
    arrivalTime: '' // 预计到达日期
  })

  delivery.value = {
    orderId: '', // 订单ID
    courierName: '', // 快递员姓名
    courierPhone: '', // 快递员电话
    courierCompany: '', // 快递公司
    location: '', // 当前位置
    deliveryAddress: '', // 收货地址
    status: '', // 状态（0：未发货，1：已发货，2：已送达）
    deliveryTime: '', // 发货日期
    arrivalTime: '' // 预计到达日期
  }
  if(deliveryFormRef.value){
    deliveryFormRef.value.resetFields()
  }

  // 控制弹窗
  const dialogVisible = ref(false)
  const dialogTitle = ref('新增订单')

  const edit = async (id) => {
    const result = await queryInfoApi(id);
    if(result.code){ 
      dialogVisible.value = true;
      dialogTitle.value = '修改配送';
      delivery.value = result.data;
    }
  }

  const save = async () => {
    deliveryFormRef.value.validate(async valid => {
      if (valid) {
        let result;
        if(delivery.value.id){ //修改
          result = await updateApi(delivery.value);
        }else { //新增
          result = await addApi(delivery.value);
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

  //删除
  const deleteById = (id) => {
    //弹出确认框
    ElMessageBox.confirm('您确认删除该配送吗?','提示',
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

  //记录勾选的配送的id
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
  <h1>配送管理</h1> <br>
  <el-form :inline="true" :model="searchDelivery">
    <el-form-item label="订单号" >
      <el-input v-model="searchDelivery.orderId" placeholder="请输入订单号" style="width: 150px;"></el-input>
    </el-form-item>
    <el-form-item label="快递员姓名" >
      <el-input v-model="searchDelivery.courierName" placeholder="请输入快递员姓名" style="width: 150px;"></el-input>
    </el-form-item>

    <el-form-item label="发货日期">
      <el-date-picker
        v-model="searchDelivery.date"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
      ></el-date-picker>
    </el-form-item>
    
    <el-form-item>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="clear">清空</el-button>
    </el-form-item>
  </el-form>

  <el-button type="primary" @click="addDelivery"> + 新增配送</el-button>
  <el-button type="danger" @click="deleteByIds"> - 批量删除</el-button>
  <br><br>

  <!-- 表格 -->
  <el-table :data="deliveryList" border style="width: 100%" @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" align="center"></el-table-column>
    <el-table-column prop="id" label="序号" width="55" align="center"></el-table-column>
    <el-table-column prop="orderId" label="订单号" width="70" align="center"></el-table-column>
    <el-table-column prop="courierName" label="快递员姓名" width="100" align="center"></el-table-column>
    <el-table-column prop="courierPhone" label="快递员电话" width="120" align="center"></el-table-column>
    <el-table-column prop="courierCompany" label="快递公司" width="110" align="center"></el-table-column>
    <el-table-column prop="location" label="当前位置" width="150" align="center"></el-table-column>
    <el-table-column prop="deliveryAddress" label="收货地址" width="150" align="center"></el-table-column>
    <el-table-column label="状态" width="100" align="center">
      <template #default="scope">
          <span v-if="scope.row.status == 0">未发货</span>
          <span v-else-if="scope.row.status == 1">已发货</span>
          <span v-else-if="scope.row.status == 2">已送达</span>
          <span v-else>其他</span>
        </template>
    </el-table-column>
    <el-table-column prop="deliveryTime" label="发货日期" width="170" align="center"></el-table-column>
    <el-table-column prop="arrivalTime" label="预计到达日期" width="170" align="center"></el-table-column>
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
      <el-form ref="deliveryFormRef" :model="delivery" :rules="rules" label-width="80px">
        <!-- 基本信息 -->
        <!-- 第一行 -->
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="订单号" prop="orderId">
              <el-input v-model="delivery.orderId" placeholder="请输入订单号"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="10">
            <el-form-item label="快递员名" prop="courierName">
              <el-input v-model="delivery.courierName" placeholder="请输入快递员名称，2-20个字"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行 -->
        <el-row :gutter="20">
            <el-col :span="10">
              <el-form-item label="快递电话" prop="courierPhone">
                <el-input v-model="delivery.courierPhone" placeholder="请输入快递员电话"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="快递公司" prop="courierCompany">
                <el-input v-model="delivery.courierCompany" placeholder="请输入快递公司"></el-input>
              </el-form-item>
            </el-col>   
        </el-row>
       
        <!-- 第三行 -->
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="当前位置" prop="location">
              <el-input v-model="delivery.location" placeholder="请输入当前位置"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="收货地址" prop="deliveryAddress">
              <el-input v-model="delivery.deliveryAddress" placeholder="请输入收货地址"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第四行 -->
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="发货日期" prop="deliveryTime">
              <el-date-picker v-model="delivery.deliveryTime" type="date" placeholder="选择发货日期" value-format="YYYY-MM-DD"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="到达时间" prop="arrivalTime">
              <el-date-picker v-model="delivery.arrivalTime" type="date" placeholder="选择预计到达日期" value-format="YYYY-MM-DD"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="delivery.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="待发货" value="0"></el-option>
                <el-option label="已发货" value="1"></el-option>
                <el-option label="已送达" value="2"></el-option>
              </el-select>
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