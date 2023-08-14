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
          <div class="total">共{{ Math.ceil(gradePage.total / pagesize) }}页</div>
          <input v-model="targetPage" :max="gradePage.total/pagesize" min="1" type="number">
          <button @click="gotoPage">跳转</button>
        </div>
      </div>
    </div>
    <div v-if="showSelectFormFlag" class="form">
      <h2>条件查询</h2>
      <form>
        <label for="teacherName">授课教师姓名：</label>
        <input id="teacherName" v-model="selectData.teacherName" type="text"><br>
        <label for="courseName">课程名称：</label>
        <input id="courseName" v-model="selectData.courseName" type="text"><br>
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
const pagesize = ref(15);
const targetPage = ref();
const header = ref(['ID', '授课老师ID', '授课老师姓名', '课程名称', '分数']);
const gradePage = reactive({
  total: null, records: [],
  keys: ['id', 'teacherId', 'teacherName', 'courseName', 'score']
});

const selectData = reactive({
  teacherName: '',
  courseName: '',
});

const flash = () => {
  post(`/grade/${page.value}/${pagesize.value}`, selectData,
      (data) => {
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

const gotoPage = () => {
  if (targetPage.value >= 1 && targetPage.value <= Math.ceil(gradePage.total / pagesize.value)) {
    page.value = targetPage.value;
    flash();
  } else {
    alert('请输入正确的页码');
  }
}

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