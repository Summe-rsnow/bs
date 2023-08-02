<template>
  <div class="container">
    <h1>数据可视化</h1>
    <div class="charts">
      <div class="chart">
        <select @change="handlePie">
          <option value="-1"></option>
          <option value="0">男女比率图</option>
          <option value="1">账号权限比率图</option>
        </select>
        <pie-chart :data="pieChartData.data" :theme="theme" :title="pieChartData.title"
                   style="width: 500px;height: 500px;"/>
      </div>
      <div class="chart">
        <select @change="handleHistogram">
          <option value="-1"></option>
          <option value="0">成绩分布图</option>
          <option value="1">授课数量排行</option>
        </select>
        <histogram-chart :data="histogramChartData.data" :theme="theme"
                         style="width: 700px;height: 500px;"/>
      </div>
    </div>
  </div>
</template>

<script setup>
import PieChart from "../components/PieChart.vue";
import HistogramChart from "../components/HistogramChart.vue";
import {get} from "../net/index.js";
import {ref} from "vue";

const theme = ref('dark');
const pieChartData = ref({
  title: '',
  data: []
});

const handlePie = (event) => {
  const selectedOption = event.target.value;
  switch (selectedOption) {
    case '-1':
      pieChartData.value.title = '';
      pieChartData.value.data = [];
      break;
    case '0':
      MFRatio();
      break;
    case '1':
      GrantRatio();
      break;
  }
};

const MFRatio = () => {
  pieChartData.value.title = '男女比率';
  get('/common/data/user/mf_ratio', (data, msg) => {
    pieChartData.value.data = data;
  })
}

const GrantRatio = () => {
  pieChartData.value.title = '用户权限比率';
  get('/common/data/user/grant_ratio', (data, msg) => {
    pieChartData.value.data = data;
  })
}

const histogramChartData = ref({data: []});

const handleHistogram = (event) => {
  const selectedOption = event.target.value;
  switch (selectedOption) {
    case '-1':
      histogramChartData.value.data = [];
      break;
    case '0':
      gradeDistribution();
      break;
    case '1':
      courseCountRanking();
      break;
  }
};

const gradeDistribution = () => {
  get('/common/data/grade/grade_distribution', (data, msg) => {
    histogramChartData.value.data = data;
  })
}

const courseCountRanking = () => {
  get('/common/data/course/count_ranking', (data, msg) => {
    histogramChartData.value.data = data;
  })
}
</script>

<style lang="less" scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  h1 {
    font-size: 60px;
    margin: 0 0 20px;
    padding: 20px 660px 10px;
    border-bottom: 2px solid #ccc;
  }

  .charts {
    width: 100%;
    display: flex;
    justify-content: space-around;
    gap: 40px;

    .chart {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      select {
        appearance: none;
        padding: 8px 12px;
        backdrop-filter: blur(8px);
        font-size: 30px;
        font-weight: 600;
        border: 1px solid #ccc;
        border-radius: 8px;
        margin: 10px 0;

        &:focus {
          outline: none;
        }
      }
    }
  }
}
</style>