<template>
  <div class="container">
    <div class="tablecontainer">
      <h1 class="title">修改成绩</h1>
      <div class="table">
        <div class="function">
          <button class="add" @click="add"><img alt="" src="../assets/icons/add.svg">新增</button>
          <button class="select" @click="select"><img alt="" src="../assets/icons/search.svg">查询</button>
          <button class="select" @click="getTemplate"><img alt="" src="../assets/icons/file.svg">模版获取</button>
          <button class="csv" @click="onCSV">
            <img alt="" src="../assets/icons/add_all.svg">
            <input v-show="false" id="csv" accept=".csv" name="select" type="file" v-on:change="onFileChange">
            批量添加
          </button>
        </div>
        <table>
          <thead>
          <tr>
            <th v-for="h in header" :key="header">{{ h }}</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in gradePage.records" :key="row">
            <td v-for="k in gradePage.keys" :key="k">{{ row[k] }}</td>
            <td>
              <div style="display: flex;justify-content: space-around;">
                <button class="l" @click="edit(row)"><img alt="" src="../assets/icons/edit.svg">修改</button>
                <button class="r" @click="delUser(row.id)"><img alt="" src="../assets/icons/trash.svg">删除</button>
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
          <button v-show="gradePage.total/pagesize>page" @click="nextPage">
            下一页
          </button>
          <div class="total">共{{ Math.ceil(gradePage.total / pagesize) }}页</div>
          <input v-model="targetPage" :max="gradePage.total/pagesize" min="1" type="number">
          <button @click="gotoPage">跳转</button>
        </div>
      </div>
    </div>
    <div v-if="showAddFormFlag" class="form">
      <h2>查询学生</h2>
      <form>
        <label for="studentId">学生ID：</label>
        <!-- 将表单数据绑定到 formData.name -->
        <input id="studentId" v-model="formData.studentId" type="text"><br>

        <label for="courseId">课程ID：</label>
        <!-- 将表单数据绑定到 formData.email -->
        <input id="courseId" v-model="formData.courseId" type="text"><br>

        <label for="score">分数：</label>
        <!-- 将表单数据绑定到 formData.email -->
        <input id="score" v-model="formData.score" type="text"><br>

        <div class="button">
          <button class="l" @click="submitAddForm">确认</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <div v-if="showSelectFormFlag" class="form">
      <h2>条件查询</h2>
      <form>
        <label for="studentName">学生姓名：</label>
        <input id="studentName" v-model="selectData.studentName" type="text"><br>
        <label for="courseName">课程名称：</label>
        <input id="courseName" v-model="selectData.courseName" type="text"><br>
        <div class="button">
          <button class="l" @click="submitSelectForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <div v-if="showEditFormFlag" class="form">
      <h2>查询学生</h2>
      <form>
        <label for="score">分数：</label>
        <input id="score" v-model="formData.score" type="text"><br>
        <div class="button">
          <button class="l" @click="submitEditForm">确认</button>
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
import {onBeforeMount, reactive, ref} from "vue";
import {del, post} from "../net/index.js";
import {useUserStore} from "../stores/index.js";

const userStore = useUserStore();
const page = ref(1);
const pagesize = ref(10);
const targetPage = ref();
const header = ref(['ID', '学生ID', '学生姓名', '课程名称', '分数']);
const gradePage = reactive({
  total: null, records: [],
  keys: ['id', 'studentId', 'studentName', 'courseName', 'score']
});
const blur = ref(false);
const showAddFormFlag = ref(false);
const showSelectFormFlag = ref(false);
const showEditFormFlag = ref(false);
const showDelFormFlag = ref(false);
const delId = ref();

const formData = reactive({
  id: null,
  studentId: null,
  courseId: null,
  teacherId: null,
  score: null,
});

const selectData = reactive({
  studentName: '',
  courseName: '',
});

const flash = () => {
  post(`/grade/change/${page.value}/${pagesize.value}`, selectData,
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
  formData.score = row.score;
  blur.value = true;
  showEditFormFlag.value = true;
}

const delUser = (id) => {
  blur.value = true;
  showDelFormFlag.value = true;
  delId.value = id;
}

const submitAddForm = () => {
  formData.teacherId = userStore.user.id;
  // 提交表单数据
  post('/grade/add', formData, () => {
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
  post('/grade/edit', formData, () => {
    flash();
  })
  resetFormData();
  cancelForm();
}

const confirmDel = () => {
  del(`grade/del/${delId.value}`, () => {
    flash();
  })
  cancelForm();
}

const cancelForm = () => {
  showAddFormFlag.value = false;
  showSelectFormFlag.value = false;
  showEditFormFlag.value = false;
  showDelFormFlag.value = false;
  blur.value = false;
}

const resetFormData = () => {
  formData.id = null;
  formData.studentId = null;
  formData.courseId = null;
  formData.teacherId = null;
  formData.score = null;
}

const onCSV = () => {
  document.getElementById('csv').click();
}

let file;

const onFileChange = (event) => {
  file = event.target.files[0];
  const formData = new FormData();
  formData.append('file', file);
  post('/grade/csv/add', formData, (data, msg) => {
    alert(msg);
    flash();
  })
}

const getTemplate = () => {
  fetch('/GradeTemplate.csv')
      .then(response => response.blob())
      .then(blob => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'GradeTemplate.csv'; // 设置文件名
        link.click();
      })
      .catch(error => {
        console.error('获取文件失败:', error);
      });
}
</script>
<style lang="less" scoped>
@import "../assets/css/manager";

table td:nth-child(1),
table td:nth-child(2) {
  width: 24%;
}

table td:nth-child(3),
table td:nth-child(5) {
  width: 11%;
}

table td:nth-child(4) {
  width: 16%;
}

table td:nth-child(6) {
  width: 14%;
}
</style>