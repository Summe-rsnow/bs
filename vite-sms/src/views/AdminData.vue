<template>
  <div class="container">
    <h1>数据可视化</h1>
    <div class="charts">
      <div class="pie">
        <div class="chart">
          <h2>{{ MFRatioData.title }}</h2>
          <pie-chart :data="MFRatioData.data" :theme="theme" :title="MFRatioData.title"
                     style="width: 500px;height: 500px;"/>
        </div>
        <div class="chart">
          <h2>{{ GrantRatioData.title }}</h2>
          <pie-chart :data="GrantRatioData.data" :theme="theme" :title="GrantRatioData.title"
                     style="width: 500px;height: 500px;"/>
        </div>
      </div>
      <div class="histogram">
        <div class="chart">
          <h2>{{ gradeDistributionData.title }}</h2>
          <histogram-chart :data="gradeDistributionData.data" :theme="theme"
                           style="width: 700px;height: 500px;"/>
        </div>
        <div class="chart">
          <h2>{{ courseCountRankingData.title }}</h2>
          <histogram-chart :data="courseCountRankingData.data" :theme="theme"
                           style="width: 700px;height: 500px;"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import PieChart from "../components/PieChart.vue";
import HistogramChart from "../components/HistogramChart.vue";
import {get} from "../net/index.js";
import {onBeforeMount, ref} from "vue";

onBeforeMount(() => {
  MFRatio();
  GrantRatio();
  gradeDistribution();
  courseCountRanking();
})

const theme = ref('light');

const MFRatioData = ref({title: '', data: []});

const MFRatio = () => {
  MFRatioData.value.title = '男女比率';
  get('/common/data/user/mf_ratio', (data, msg) => {
    MFRatioData.value.data = data;
  })
}

const GrantRatioData = ref({title: '', data: []});

const GrantRatio = () => {
  GrantRatioData.value.title = '用户权限比率';
  get('/common/data/user/grant_ratio', (data, msg) => {
    GrantRatioData.value.data = data;
  })
}

const gradeDistributionData = ref({title: '', data: []});

const gradeDistribution = () => {
  gradeDistributionData.value.title = '成绩总分布';
  get('/common/data/grade/grade_distribution', (data, msg) => {
    gradeDistributionData.value.data = data;
  })
}

const courseCountRankingData = ref({title: '', data: []});

const courseCountRanking = () => {
  courseCountRankingData.value.title = '教师授课数排行';
  get('/common/data/course/count_ranking', (data, msg) => {
    courseCountRankingData.value.data = data;
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
      margin: 40px 0;
      padding: 10px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border-radius: 20px;
      background-color: #4fcfa9;
      box-shadow: 14px 14px 40px #343a40;

      h2 {
        font-size: 30px;
        font-weight: 600;
        color: #333333;
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2); /* 添加阴影效果 */
      }
    }
  }
}
</style>