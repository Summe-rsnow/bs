<template>
  <div class="echarts">
    <e-charts :option="option"></e-charts>
  </div>
</template>

<script>
export default {
  name: "PieChart",
  props: {
    data: {
      type: Array, // 指定 data 属性的类型为数组
    },
    title: ''
  },
  computed: {
    option() {
      // 将原先的计算属性改为接收 data 属性作为参数
      return {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: this.title,
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 40,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.data // 将原先的数据改为从接收的 data 属性中获取
          }
        ]
      }
    }
  },
};
</script>

<style scoped>
.echarts {
  height: 400px;
  width: 400px;
}
</style>