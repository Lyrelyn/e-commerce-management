<script setup>
import { onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { queryTop5ProductSalesApi } from '@/api/report'
import { ElCard, ElSkeleton, ElSkeletonItem } from 'element-plus'

// 钩子函数 - 加载商品销量统计报表
onMounted(() => {
  nextTick(() => {
    loadProductSalesChart()
  })
})

// 获取商品销量统计报表
const loadProductSalesChart = async () => {
 
  try {
    const result = await queryTop5ProductSalesApi()
    if (result.data) {
      console.log(result.data)
      initProductSalesChart(result.data)
    } else {
      console.error('获取商品销量数据失败:', result)
    }
  } catch (error) {
    console.error('请求商品销量数据出错:', error)
  }
}

// 初始化商品销量统计图表
function initProductSalesChart(salesDataList) {
  const myChart = echarts.init(document.getElementById('product-sales-container'))

  // 提取商品名称和销量数据
  const productNames = salesDataList.map(item => item.name)
  const salesVolumes = salesDataList.map(item => item.sales)

  const option = {
    // title: {
    //   text: '商品销量前 5 名统计',
    //   left: 'center'
    // },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: productNames
    },
    yAxis: {
      type: 'value',
      name: '销量'
    },
    series: [
      {
        name: '销量',
        type: 'line',
        data: salesVolumes,
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }

  myChart.setOption(option)

  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}
</script>

<template>

  <!-- <div class="report_container" id="product-sales-container"></div> -->
  <el-card shadow="hover">
    <template #header>
      <div class="card-header">
        <span>商品销量前 5 名统计</span>
      </div>
    </template>
    <!-- <el-skeleton v-if="isLoading" animated>
      <el-skeleton-item variant="rect" width="100%" height="600px" />
    </el-skeleton> -->
    <div class="report_container" id="product-sales-container"></div>
  </el-card>
</template>

<style scoped>
.report_container {
  width: 1000px;
  height: 600px;
  margin: 0 auto;
}
.el-card {
  margin: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
} 
</style>

<!-- <template>
  <div class="report-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>客户级别统计</span>
        </div>
      </template>
      <el-skeleton v-if="isLoading" animated>
        <el-skeleton-item variant="rect" width="100%" height="600px" />
      </el-skeleton>
      <div v-else class="chart-container" ref="levelChartRef"></div>
    </el-card>

    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>客户性别统计</span>
        </div>
      </template>
      <el-skeleton v-if="isLoading" animated>
        <el-skeleton-item variant="rect" width="100%" height="600px" />
      </el-skeleton>
      <div v-else class="chart-container" ref="genderChartRef"></div>
    </el-card>
  </div>
</template>

<style scoped>
.report-container {
  display: flex;
  gap: 20px;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
}

.chart-container {
  width: 100%;
  height: 600px;
}

.el-card {
  flex: 1;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style> -->