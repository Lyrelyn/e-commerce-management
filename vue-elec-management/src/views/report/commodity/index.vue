<script setup>
import { onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { queryTop5ProductSalesApi } from '@/api/report'

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
    title: {
      text: '商品销量前 5 名统计',
      left: 'center'
    },
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

  <div class="report_container" id="product-sales-container"></div>
</template>

<style scoped>
.report_container {
  width: 1000px;
  height: 600px;
  margin: 0 auto;
}
</style>