<template>
  <div ref="chart" class="echarts"></div>
</template>

<script>
import * as echarts from 'echarts/core';
import {GridComponent} from 'echarts/components';
import {BarChart} from 'echarts/charts';
import {CanvasRenderer} from 'echarts/renderers';

export default {
  name: "HistogramChart",
  props: {
    data: {
      type: Array, // 指定 data 属性的类型为数组
      default: []
    },
    theme: ''
  },
  computed: {
    option() {
      return {
        backgroundColor: '',
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
  },
  mounted() {
    echarts.use([GridComponent, BarChart, CanvasRenderer]);
    const myChart = this.theme ? echarts.init(this.$refs.chart, this.theme) : echarts.init(this.$refs.chart);
    // 监听数据变化，并更新图表
    this.$watch('data', () => {
      myChart.setOption(this.option);
    });
  }
}
</script>

<style scoped>
.echarts {
  height: 100%;
  width: 100%;
}
</style>