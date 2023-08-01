<template>
  <div class="container">
    <h1>数据可视化</h1>
    <div class="charts">
      <div class="chart">
        <select @change="handleSelectChange">
          <option value="-1"></option>
          <option value="0">男女比率图</option>
          <option value="1">账号权限比率图</option>
        </select>
        <pie-chart :data="pieChartData.data" :title="pieChartData.title"></pie-chart>
      </div>
      <div class="chart">
        <select>
          <option value="-1">成绩分布图</option>
        </select>
        <histogram-chart :data="histogramChartData.data"></histogram-chart>
      </div>

    </div>
  </div>
</template>

<script setup>
import PieChart from "../components/PieChart.vue";
import HistogramChart from "../components/HistogramChart.vue";
import {get} from "../net/index.js";
import {onMounted, ref} from "vue";

onMounted(() => {
  gradeDistribution();
})

const pieChartData = ref({
  title: '',
  data: []
});

const handleSelectChange = (event) => {
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

const gradeDistribution = () => {
  get('/common/data/grade/grade_distribution', (data, msg) => {
    console.log(data);
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

    .chart {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border: 1px solid #ccc;
      border-radius: 20px;
      background-color: #eed9d9;
      box-shadow: 14px 14px 40px #343a40;

      select {
        padding: 8px 12px;
        font-size: 20px;
        border: 1px solid #ccc;
        border-radius: 4px;
        margin-bottom: 20px;
      }
    }
  }
}
</style>