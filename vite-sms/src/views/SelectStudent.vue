<template>
  <div class="container">
    <div class="tablecontainer">
      <h1 class="title">查询学生</h1>
      <div class="table">
        <table>
          <thead>
          <tr>
            <th v-for="h in header" :key="header">{{ h }}</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in studentPage.records" :key="row">
            <td v-for="k in studentPage.keys" :key="k">{{ row[k] }}</td>
          </tr>
          </tbody>
        </table>
        <div class="pagination">
          <button v-show="page>1" @click="prevPage">
            上一页
          </button>
          <span>第{{ page }}页</span>
          <button v-show="studentPage.total/pagesize>page" @click="nextPage">
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
const pagesize = ref(10);
const header = ref(['ID', '用户名', '姓名', '邮箱', '性别', '年龄', '手机号码']);
const studentPage = reactive({
  total: null, records: [],
  keys: ['id', 'username', 'name', 'email', 'gender', 'age', 'phone']
});

const flash = () => {
  get(`/grade/student/${page.value}/${pagesize.value}`,
      (data, msg) => {
        studentPage.records = data.records;
        studentPage.total = data.total;
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
table td:nth-child(2),
table td:nth-child(4),
table td:nth-child(7) {
  width: 19%;
}

table td:nth-child(3) {
  width: 10%;
}

table td:nth-child(5),
table td:nth-child(6) {
  width: 7%;
}
</style>