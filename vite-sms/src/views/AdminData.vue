<template>
  <div>
    <div>
      <button v-for="(chart, index) in charts" :key="index" @click="selectChart(index)">
        {{ chart.title }}
      </button>
    </div>
    <div v-if="selectedChart !== null">
      <pie-chart :data="selectedChart.data"></pie-chart>
    </div>
  </div>
</template>

<script setup>
import PieChart from "../components/PieChart.vue";
import { ref } from "vue";

const charts = [
  {
    title: "Search Engine",
    data: [
      { value: 1048, name: "Google" },
      { value: 735, name: "Bing" },
      { value: 580, name: "Yahoo" },
      { value: 484, name: "Baidu" },
      { value: 300, name: "Others" },
    ],
  },
  {
    title: "Direct",
    data: [
      { value: 335, name: "Direct" },
      { value: 310, name: "Email" },
      { value: 234, name: "Affiliate" },
      { value: 135, name: "Video Ads" },
      { value: 1548, name: "Search Engine" },
    ],
  },
  {
    title: "Email",
    data: [
      { value: 310, name: "Hotmail" },
      { value: 234, name: "Gmail" },
      { value: 135, name: "Yahoo Mail" },
      { value: 1548, name: "Others" },
    ],
  },
];

const selectedChart = ref(null);

function selectChart(index) {
  selectedChart.value = charts[index];
}
</script>

<style scoped>
button {
  margin: 10px;
}
</style>