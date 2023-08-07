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
          <h2>{{ gradePassRankingDescData.title }}</h2>
          <histogram-chart :data="gradePassRankingDescData.data" :theme="theme"
                           style="width: 700px;height: 500px;"/>
        </div>
        <div class="chart">
          <h2>{{ gradePassRankingAscData.title }}</h2>
          <histogram-chart :data="gradePassRankingAscData.data" :theme="theme"
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
  gradePassRankingDesc();
  gradePassRankingAsc();
})

const theme = ref('light');

const gradePassRankingDescData = ref({title: '', data: []});

const gradePassRankingDesc = () => {
  gradePassRankingDescData.value.title = '各科及格率排行(降序)';
  get('/common/data/grade/pass_ranking/0', (data, msg) => {
    gradePassRankingDescData.value.data = data;
  })
}

const gradePassRankingAscData = ref({title: '', data: []});

const gradePassRankingAsc = () => {
  gradePassRankingAscData.value.title = '各科及格率排行(升序)';
  get('/common/data/grade/pass_ranking/1', (data, msg) => {
    gradePassRankingAscData.value.data = data;
  })
}
</script>

<style lang="less" scoped>
@import "../assets/css/data.less";
</style>