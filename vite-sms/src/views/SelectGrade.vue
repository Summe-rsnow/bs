<template>
  <div class="container">
    <div class="tablecontainer">
      <h1 class="title">查询课程</h1>
      <div class="table">
        <table>
          <thead>
          <tr>
            <th v-for="h in header" :key="header">{{ h }}</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in gradePage.records" :key="row">
            <td v-for="k in gradePage.keys" :key="k">{{ row[k] }}</td>
          </tr>
          </tbody>
        </table>
        <div class="pagination">
          <button v-show="page>1" @click="prevPage">
            上一页
          </button>
          <span>第{{ page }}页</span>
          <button v-show="gradePage.total/pagesize>page" @click="nextPage">
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {get} from "../net/index.js";
import {onBeforeMount, reactive, ref} from "vue";

const page = ref(1);
const pagesize = ref(15);
const header = ref(['ID', '授课老师ID', '授课老师', '课程名', '分数']);
const gradePage = reactive({
  total: null, records: [],
  keys: ['id', 'teacherId', 'teacherName', 'courseName', 'score']
});

const flash = () => {
  get(`/grade/${page.value}/${pagesize.value}`,
      (data, msg) => {
        gradePage.records = data.records;
        gradePage.total = data.total;
      }
  )
}

onBeforeMount(() => {
  flash();
});

//下一页
const nextPage = () => {
  page.value++;
  flash();
};

//上一页
const prevPage = () => {
  page.value--;
  flash();
};
</script>

<style lang="less" scoped>
@import "../assets/css/select";
table td:nth-child(1),
table td:nth-child(2) {
  width: 27%;
}

table td:nth-child(3),
table td:nth-child(4) {
  width: 18%;
}

table td:nth-child(5) {
  width: 10%;
}
</style>