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
  get('/common/data/user/mf_ratio', (data) => {
    MFRatioData.value.data = data;
  })
}

const GrantRatioData = ref({title: '', data: []});

const GrantRatio = () => {
  GrantRatioData.value.title = '用户权限比率';
  get('/common/data/user/grant_ratio', (data) => {
    GrantRatioData.value.data = data;
  })
}

const gradeDistributionData = ref({title: '', data: []});

const gradeDistribution = () => {
  gradeDistributionData.value.title = '成绩总分布';
  get('/common/data/grade/grade_distribution', (data) => {
    gradeDistributionData.value.data = data;
  })
}

const courseCountRankingData = ref({title: '', data: []});

const courseCountRanking = () => {
  courseCountRankingData.value.title = '教师授课数排行';
  get('/common/data/course/count_ranking', (data) => {
    courseCountRankingData.value.data = data;
  })
}
</script>

<style lang="less" scoped>
@import "../assets/css/data.less";
</style>