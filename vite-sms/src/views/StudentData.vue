<template>
  <div class="container">
    <h1>数据可视化</h1>
    <div class="charts">
      <!--      <div class="pie">-->
      <!--        <div class="chart">-->
      <!--          <h2>{{ MFRatioData.title }}</h2>-->
      <!--          <pie-chart :data="MFRatioData.data" :theme="theme" :title="MFRatioData.title"-->
      <!--                     style="width: 500px;height: 500px;"/>-->
      <!--        </div>-->
      <!--        <div class="chart">-->
      <!--          <h2>{{ GrantRatioData.title }}</h2>-->
      <!--          <pie-chart :data="GrantRatioData.data" :theme="theme" :title="GrantRatioData.title"-->
      <!--                     style="width: 500px;height: 500px;"/>-->
      <!--        </div>-->
      <!--      </div>-->
      <div class="histogram">
        <div class="chart">
          <h2>{{ gradeSelfRankingData.title }}</h2>
          <histogram-chart :data="gradeSelfRankingData.data" :theme="theme"
                           style="width: 700px;height: 500px;"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import HistogramChart from "../components/HistogramChart.vue";
import {onBeforeMount, ref} from "vue";
import {get} from "../net/index.js";

onBeforeMount(() => {
  gradeSelfRanking();
})

const theme = ref('light');

const gradeSelfRankingData = ref({title: '', data: []});

const gradeSelfRanking = () => {
  gradeSelfRankingData.value.title = '个人成绩排行';
  get('/common/data/grade/self_ranking', (data) => {
    gradeSelfRankingData.value.data = data;
  })
}
</script>

<style lang="less" scoped>
@import "../assets/css/data.less";
</style>