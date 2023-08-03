<template>
  <div class="container">
    <div class="tablecontainer">
      <h1 class="title">查询课程</h1>
      <div class="table">
        <button class="select" @click="select"><img alt="" src="../assets/icons/search.svg">查询</button>
        <table>
          <thead>
          <tr>
            <th v-for="h in header" :key="header">{{ h }}</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in coursePage.records" :key="row">
            <td v-for="k in coursePage.keys" :key="k">{{ row[k] }}</td>
          </tr>
          </tbody>
        </table>
        <div class="pagination">
          <button v-show="page>1" @click="prevPage">
            上一页
          </button>
          <span>第{{ page }}页</span>
          <button v-show="coursePage.total/pagesize>page" @click="nextPage">
            下一页
          </button>
        </div>
      </div>
    </div>
    <div v-if="showSelectFormFlag" class="form">
      <h2>条件查询</h2>
      <form>
        <label for="name">课程名称：</label>
        <input id="name" v-model="selectData.name" type="text"><br>
        <div class="button">
          <button class="l" @click="submitSelectForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <!-- 背景模糊层 -->
    <div :class="{ 'blur-background': blur === true }"></div>
  </div>
</template>

<script setup>
import {post} from "../net/index.js";
import {onBeforeMount, reactive, ref} from "vue";

const blur = ref(false);
const showSelectFormFlag = ref(false);
const page = ref(1);
const pagesize = ref(10);
const header = ref(['ID', '课程名称', '授课教师ID', '授课教师名称']);
const coursePage = reactive({
  total: null, records: [],
  keys: ['id', 'name', 'teacherId', 'teacherName']
});

//创建新的响应式对象来保存条件查询数据
const selectData = reactive({
  name: '',
});

const flash = () => {
  post(`/grade/course/${page.value}/${pagesize.value}`, selectData,
      (data, msg) => {
        coursePage.records = data.records;
        coursePage.total = data.total;
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

const select = () => {
  blur.value = true;
  showSelectFormFlag.value = true;
}

const submitSelectForm = () => {
  page.value = 1;
  flash();
  cancelForm();
}

const cancelForm = () => {
  showSelectFormFlag.value = false;
  blur.value = false;
}
</script>

<style lang="less" scoped>
@import "../assets/css/select";

table td:nth-child(1) {
  width: 30%;
}

table td:nth-child(2) {
  width: 30%;
}

table td:nth-child(3) {
  width: 25%;
}

table td:nth-child(4) {
  width: 15%;
}
</style>