<template>
  <div class="container">
    <div class="tablecontainer">
      <h1 class="title">查询学生</h1>
      <div class="table">
        <button class="select" @click="select"><img alt="" src="../assets/icons/search.svg">查询</button>
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
          <div class="total">共{{ Math.ceil(studentPage.total / pagesize) }}页</div>
          <input v-model="targetPage" :max="studentPage.total/pagesize" min="1" type="number">
          <button @click="gotoPage">跳转</button>
        </div>
      </div>
    </div>
    <div v-if="showSelectFormFlag" class="form">
      <h2>条件查询</h2>
      <form>
        <label for="name">姓名：</label>
        <input id="name" v-model="selectData.name" type="text"><br>
        <label for="gender">性别：</label>
        <select id="gender" v-model="selectData.gender">
          <option value=""></option>
          <option value="男">男</option>
          <option value="女">女</option>
        </select><br>

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
import {onBeforeMount, reactive, ref} from "vue";
import {post} from "../net/index.js";

const blur = ref(false);
const showSelectFormFlag = ref(false);
const page = ref(1);
const pagesize = ref(10);
const targetPage = ref();
const header = ref(['ID', '用户名', '姓名', '邮箱', '性别', '年龄', '手机号码']);
const studentPage = reactive({
  total: null, records: [],
  keys: ['id', 'username', 'name', 'email', 'gender', 'age', 'phone']
});

//创建新的响应式对象来保存条件查询数据
const selectData = reactive({
  name: '',
  gender: '',
});

const flash = () => {
  post(`/grade/student/${page.value}/${pagesize.value}`, selectData,
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

const gotoPage = () => {
  if (targetPage.value >= 1 && targetPage.value <= Math.ceil(studentPage.total / pagesize.value)) {
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
table td:nth-child(2),
table td:nth-child(7) {
  width: 17%;
}

table td:nth-child(4) {
  width: 25%;
}

table td:nth-child(3) {
  width: 10%;
}

table td:nth-child(5),
table td:nth-child(6) {
  width: 7%;
}
</style>