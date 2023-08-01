<template>
  <div class="echarts">
    <e-charts :option="option"></e-charts>
  </div>
</template>

<script>
export default {
  name: "HistogramChart",
  props: {
    data: {
      type: Array, // 指定 data 属性的类型为数组
    }
  },
  computed: {
    option() {
      return {
        xAxis: {
          type: 'category',
          data: this.data.map(i => i.name)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: this.data.map(i => i.value),
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
              color: 'rgba(180, 180, 180, 0.2)'
            }
          }
        ]
      };
    }
  }
}
</script>

<style scoped>
.echarts {
  height: 400px;
  width: 600px;
}
</style>