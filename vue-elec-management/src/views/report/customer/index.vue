<script setup>
  import { onMounted } from 'vue'
  import * as echarts from 'echarts'
  import { queryCustomerGenderDataApi, queryCustomerLevelDataApi } from '@/api/report'

  //钩子函数 - 加载报表
  onMounted(() => {
    loadGenderChart() //加载性别统计报表
    loadLevelChart() //加载客户级别统计报表
  })

  //获取性别统计报表
  const loadGenderChart = async () => {
    let result = await queryCustomerGenderDataApi();
    initGenderChart(result.data)
  }

  //获取客户级别统计报表
  const loadLevelChart = async () => {
    let result = await queryCustomerLevelDataApi();
    console.log(result)
    const dataList = result.data.map(item => item.num);
    initLevelChart(levelList ,dataList)
  }

  const levelList = ['普通客户', 'VIP客户', '至尊VIP客户'];

  function initLevelChart(levelList, dataList) {
  // 基于准备好的dom，初始化echarts实例
  var myChart = echarts.init(document.getElementById('container1'));
  // 绘制图表
  myChart.setOption({
    title: {
      text: '客户级别统计',
      subText: '',
      textStyle: {
        fontSize: 20
      },
      left: 'center'
    },
    grid:{
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel:true
    },
    tooltip: {},
    xAxis: {
      data: levelList
    },
    yAxis: {},
    series: [
      {
        name: '人数',
        type: 'bar',
        data: dataList,
        itemStyle:{
          // 设置柱状渐变色
          color:new echarts.graphic.LinearGradient(0, 0, 1, 1, [
            {
              offset: 0,
              color: '#ffbf61'
            },
            {
              offset: 1,
              color: '#dd5f85'
            }
          ])
        }
      }
    ]
  });
}

  function initGenderChart(genderDataList) {
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('container2'));
    let option = {
      title: {
        text: '客户性别统计',
        subText: '',
        textStyle: {
          fontSize: 20
        },
        left: 'center'
      },
      grid:{
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel:true
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        top: '10%',
        left: 'center'
      },
      series: [
        {
          name: '性别',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          top: '5%',
          itemStyle: {
            borderRadius: 5,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold'
            }
          },
          data: genderDataList
        }
      ]
    };
    // 绘制图表
    myChart.setOption(option);
  }

  

</script>

<template>


  <div class="report_container" id="container1">
    
  </div>

  <div class="report_container" id="container2">
    
  </div>
   
</template>


<style scoped>
  .report_container {
    width: 600px;
    height: 600px;
    float: left;
    margin-left: 5px;
  }

  #container1 {
    border-right: 1px dashed #ccc;
  }
</style>