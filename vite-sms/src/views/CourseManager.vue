<template>
  <div class="container">
    <div class="tablecontainer">
      <h1 class="title">课程管理</h1>
      <div class="table">
        <div class="function">
          <button class="add" @click="add"><img alt="" src="../assets/icons/add.svg">新增</button>
          <button class="select" @click="select"><img alt="" src="../assets/icons/search.svg">查询</button>
        </div>
        <table>
          <thead>
          <tr>
            <th v-for="h in header" :key="header">{{ h }}</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in coursePage.records" :key="row">
            <td v-for="k in coursePage.keys" :key="k">{{ row[k] }}</td>
            <td>
              <div style="display: flex;justify-content: space-around;">
                <button class="l" @click="edit(row)"><img alt="" src="../assets/icons/edit.svg">修改</button>
                <button class="r" @click="delCourse(row.id)"><img alt="" src="../assets/icons/trash.svg">删除</button>
              </div>
            </td>
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
    <div v-if="showAddFormFlag" class="form">
      <h2>添加课程</h2>
      <form>
        <label for="name">课程名称：</label>
        <!-- 将表单数据绑定到 formData.name -->
        <input id="name" v-model="formData.name" required="required" type="text"><br>

        <label for="email">授课老师ID：</label>
        <!-- 将表单数据绑定到 formData.email -->
        <input id="email" v-model="formData.teacherId" required="required" type="text"><br>

        <div class="button">
          <button class="l" @click="submitAddForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <div v-if="showSelectFormFlag" class="form">
      <h2>条件查询</h2>
      <form>
        <label for="name">课程名称：</label>
        <input id="name" v-model="selectData.name" type="text"><br>
        <label for="teacherName">授课教师姓名：</label>
        <input id="teacherName" v-model="selectData.teacherName" type="text"><br>
        <div class="button">
          <button class="l" @click="submitSelectForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <div v-if="showEditFormFlag" class="form">
      <h2>添加用户</h2>
      <form>
        <label for="name">课程名称：</label>
        <!-- 将表单数据绑定到 formData.name -->
        <input id="name" v-model="formData.name" required="required" type="text"><br>

        <label for="email">授课老师ID：</label>
        <!-- 将表单数据绑定到 formData.email -->
        <input id="email" v-model="formData.teacherId" required="required" type="text"><br>

        <div class="button">
          <button class="l" @click="submitEditForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <div v-if="showDelFormFlag" class="form">
      <h2>确定要删除该用户吗？</h2>
      <div class="button">
        <button class="l" @click="confirmDel">确定</button>
        <button class="r" @click="cancelForm">取消</button>
      </div>
    </div>
    <!-- 背景模糊层 -->
    <div :class="{ 'blur-background': blur === true }"></div>
  </div>
</template>

<script setup>
import {del, post} from "../net/index.js";
import {onBeforeMount, reactive, ref} from "vue";

const page = ref(1);
const pagesize = ref(10);
const header = ref(['ID', '课程名称', '授课教师ID', '授课教师姓名']);
const coursePage = reactive({
  total: null, records: [],
  keys: ['id', 'name', 'teacherId', 'teacherName']
});
const blur = ref(false);
const showAddFormFlag = ref(false);
const showSelectFormFlag = ref(false);
const showEditFormFlag = ref(false);
const showDelFormFlag = ref(false);
const delId = ref();

// 创建新的响应式对象来保存表单数据
const formData = reactive({
  id: null,
  name: null,
  teacherId: null
});

const selectData = reactive({
  name: '',
  teacherName: '',
});

const flash = () => {
  post(`/course/${page.value}/${pagesize.value}`, selectData,
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

const add = () => {
  resetFormData();
  blur.value = true;
  showAddFormFlag.value = true;
}

const select = () => {
  blur.value = true;
  showSelectFormFlag.value = true;
}

const edit = (row) => {
  formData.id = row.id;
  formData.name = row.name;
  formData.teacherId = row.teacherId;
  blur.value = true;
  showEditFormFlag.value = true;
}

const delCourse = (id) => {
  blur.value = true;
  showDelFormFlag.value = true;
  delId.value = id;
}

const submitAddForm = () => {
  // 提交表单数据
  post('/course/add', formData, () => {
    flash();
  })
  resetFormData();
  cancelForm();
}

const submitSelectForm = () => {
  page.value = 1;
  flash();
  cancelForm();
}

const submitEditForm = () => {
  // 提交表单数据
  post('/course/edit', formData, () => {
    flash();
  })
  resetFormData();
  cancelForm();
}

const confirmDel = () => {
  del(`/course/del/${delId.value}`, () => {
    flash();
  })
  cancelForm();
}

const cancelForm = () => {
  showAddFormFlag.value = false;
  showEditFormFlag.value = false;
  showDelFormFlag.value = false;
  showSelectFormFlag.value = false;
  blur.value = false;
}

const resetFormData = () => {
  formData.id = null;
  formData.name = null;
  formData.teacherId = null;
}
</script>

<style lang="less" scoped>
@import "../assets/css/manager";

table td:nth-child(1) {
  width: 28%;
}

table td:nth-child(2) {
  width: 19%;
}

table td:nth-child(3) {
  width: 28%;
}

table td:nth-child(4) {
  width: 12%;
}

table td:nth-child(5) {
  width: 13%;
}
</style>