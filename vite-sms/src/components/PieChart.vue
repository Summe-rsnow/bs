<template>
  <div ref="chart" class="echarts">
  </div>
</template>

<script>
import * as echarts from 'echarts/core';
import {LegendComponent, TooltipComponent} from 'echarts/components';
import {PieChart} from 'echarts/charts';
import {LabelLayout} from 'echarts/features';
import {CanvasRenderer} from 'echarts/renderers';

export default {
  name: "PieChart",
  props: {
    data: {
      type: Array, // 指定 data 属性的类型为数组
      default: []
    },
    title: '',
    theme: ''
  },
  computed: {
    option() {
      // 将原先的计算属性改为接收 data 属性作为参数
      return {
        backgroundColor: '',
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
  mounted() {
    echarts.use([
      TooltipComponent,
      LegendComponent,
      PieChart,
      CanvasRenderer,
      LabelLayout
    ]);
    const myChart = this.theme ? echarts.init(this.$refs.chart, this.theme) : echarts.init(this.$refs.chart);
    myChart.setOption(this.option);
    // 监听数据变化，并更新图表
    this.$watch('data', () => {
      myChart.setOption(this.option);
    });
  }
};
</script>

<style scoped>
.echarts {
  height: 100%;
  width: 100%;
}
</style>