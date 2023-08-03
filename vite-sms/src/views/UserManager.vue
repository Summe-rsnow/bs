<template>
  <div class="container">
    <div class="tablecontainer">
      <h1 class="title">用户管理</h1>
      <div class="table">
        <div class="function">
          <button class="add" @click="add"><img alt="" src="../assets/icons/add.svg">新增</button>
          <button class="select" @click="select"><img alt="" src="../assets/icons/search.svg">查询</button>
          <button class="csv" @click="onCSV">
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
          <tr v-for="row in userPage.records" :key="row">
            <td v-for="k in userPage.keys" :key="k">{{ row[k] }}</td>
            <td>{{ grantName[row['userGrant']] }}</td>
            <td>
              <div style="display: flex;justify-content: space-around;">
                <button class="l" @click="edit(row)"><img alt="" src="../assets/icons/edit.svg">修改</button>
                <button v-if="row.status ===1" class="n" @click="banUser(row.id,0)"><img alt=""
                                                                                         src="../assets/icons/no.svg">禁用
                </button>
                <button v-else class="y" @click="banUser(row.id,1)"><img alt="" src="../assets/icons/yes.svg">启用
                </button>
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
          <button v-show="userPage.total/pagesize>page" @click="nextPage">
            下一页
          </button>
        </div>
      </div>
    </div>
    <div v-if="showAddFormFlag" class="form">
      <h2>添加用户</h2>
      <form>
        <label for="name">账号：</label>
        <!-- 将表单数据绑定到 formData.username -->
        <input id="username" v-model="formData.username" required="required" type="text"><br>

        <label for="name">姓名：</label>
        <!-- 将表单数据绑定到 formData.name -->
        <input id="name" v-model="formData.name" type="text"><br>

        <label for="email">邮箱：</label>
        <!-- 将表单数据绑定到 formData.email -->
        <input id="email" v-model="formData.email" type="text"><br>

        <label for="gender">性别：</label>
        <!-- 将表单数据绑定到 formData.gender -->
        <select id="gender" v-model="formData.gender">
          <option value="男">男</option>
          <option value="女">女</option>
        </select><br>

        <label for="age">年龄：</label>
        <!-- 将表单数据绑定到 formData.age -->
        <input id="age" v-model="formData.age" type="number"><br>

        <label for="phone">手机号码：</label>
        <!-- 将表单数据绑定到 formData.phone -->
        <input id="phone" v-model="formData.phone" type="text"><br>

        <label for="idNumber">身份证号码：</label>
        <!-- 将表单数据绑定到 formData.idNumber -->
        <input id="idNumber" v-model="formData.idNumber" type="text"><br>

        <label for="userGrant">权限：</label>
        <!-- 将表单数据绑定到 formData.userGrant -->
        <select id="userGrant" v-model="formData.userGrant">
          <option value="1">教师</option>
          <option value="2">学生</option>
          <option value="0">管理员</option>
        </select><br>

        <div class="button">
          <button class="l" @click="submitAddForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
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
        <label for="userGrant">权限：</label>
        <select id="userGrant" v-model="selectData.userGrant">
          <option value="-1"></option>
          <option value="1">教师</option>
          <option value="2">学生</option>
          <option value="0">管理员</option>
        </select><br>

        <div class="button">
          <button class="l" @click="submitSelectForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <div v-if="showEditFormFlag" class="form">
      <h2>添加用户</h2>
      <form>
        <label for="name">姓名：</label>
        <!-- 将表单数据绑定到 formData.name -->
        <input id="name" v-model="formData.name" type="text"><br>

        <label for="email">邮箱：</label>
        <!-- 将表单数据绑定到 formData.email -->
        <input id="email" v-model="formData.email" type="text"><br>

        <label for="gender">性别：</label>
        <!-- 将表单数据绑定到 formData.gender -->
        <select id="gender" v-model="formData.gender">
          <option value="男">男</option>
          <option value="女">女</option>
        </select><br>

        <label for="age">年龄：</label>
        <!-- 将表单数据绑定到 formData.age -->
        <input id="age" v-model="formData.age" type="number"><br>

        <label for="phone">手机号码：</label>
        <!-- 将表单数据绑定到 formData.phone -->
        <input id="phone" v-model="formData.phone" type="text"><br>

        <label for="idNumber">身份证号码：</label>
        <!-- 将表单数据绑定到 formData.idNumber -->
        <input id="idNumber" v-model="formData.idNumber" type="text"><br>

        <div class="button">
          <button class="l" @click="submitEditForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <div v-if="showBanFormFlag" class="form">
      <h2>确定要进行该操作吗？</h2>
      <div class="button">
        <button class="l" @click="confirmBan">确定</button>
        <button class="r" @click="cancelForm">取消</button>
      </div>
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
import {del, get, post} from "../net/index.js";
import {onBeforeMount, reactive, ref} from "vue";

const page = ref(1);
const pagesize = ref(10);
const header = ref(['ID', '用户名', '姓名', '邮箱', '性别', '年龄', '手机号码', '身份']);
const grantName = ref(['管理员', '教师', '学生']);
const userPage = reactive({
  total: null, records: [],
  keys: ['id', 'username', 'name', 'email', 'gender', 'age', 'phone']
});
const blur = ref(false);
const showAddFormFlag = ref(false);
const showSelectFormFlag = ref(false);
const showEditFormFlag = ref(false);
const showBanFormFlag = ref(false);
const showDelFormFlag = ref(false);
const ban = ref();
const banId = ref();
const delId = ref();

// 创建新的响应式对象来保存表单数据
const formData = reactive({
  id: null,
  username: null,
  name: null,
  email: null,
  gender: null,
  age: null,
  phone: null,
  idNumber: null,
  userGrant: 2,
});

//创建新的响应式对象来保存条件查询数据
const selectData = reactive({
  name: '',
  gender: '',
  userGrant: -1,
});

const flash = () => {
  post(`/user/${page.value}/${pagesize.value}`, selectData,
      (data, msg) => {
        userPage.records = data.records;
        userPage.total = data.total;
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
  formData.username = row.username;
  formData.name = row.name;
  formData.email = row.email;
  formData.gender = row.gender;
  formData.age = row.age;
  formData.phone = row.phone;
  formData.idNumber = row.idNumber;
  formData.userGrant = row.userGrant;
  blur.value = true;
  showEditFormFlag.value = true;
}

const banUser = (id, flag) => {
  blur.value = true;
  showBanFormFlag.value = true;
  banId.value = id;
  ban.value = flag;
}

const delUser = (id) => {
  blur.value = true;
  showDelFormFlag.value = true;
  delId.value = id;
}

const submitAddForm = () => {
  const username = formData.username;
  const email = formData.email;
  const phone = formData.phone;
  const idNumber = formData.idNumber;

  // 账号格式验证
  const usernameRegex = /^[a-zA-Z][a-zA-Z0-9]{4,15}$/;
  if (!usernameRegex.test(username)) {
    alert('请输入5-16位账号，且账号开头为字母，只能包含字母和数字');
    cancelForm();
    return;
  }

  // 邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (email && !emailRegex.test(email)) {
    alert('请输入正确的邮箱格式');
    cancelForm();
    return;
  }
  // 手机号码验证
  const phoneRegex = /^\d{11}$/;
  if (phone && !phoneRegex.test(phone)) {
    alert('请输入11位手机号码');
    cancelForm();
    return;
  }
  // 身份证号码验证
  const idNumberRegex = /^\d{18}$/;
  if (idNumber && !idNumberRegex.test(idNumber)) {
    alert('请输入18位身份证号码');
    cancelForm();
    return;
  }

  // 提交表单数据
  post('/user/add', formData, () => {
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
  const email = formData.email;
  const phone = formData.phone;
  const idNumber = formData.idNumber;

  // 邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (email && !emailRegex.test(email)) {
    alert('请输入正确的邮箱格式');
    cancelForm();
    return;
  }
  // 手机号码验证
  const phoneRegex = /^\d{11}$/;
  if (phone && !phoneRegex.test(phone)) {
    alert('请输入11位手机号码');
    cancelForm();
    return;
  }
  // 身份证号码验证
  const idNumberRegex = /^\d{18}$/;
  if (idNumber && !idNumberRegex.test(idNumber)) {
    alert('请输入18位身份证号码');
    cancelForm();
    return;
  }

  // 提交表单数据
  post('/user/edit', formData, () => {
    flash();
  })
  resetFormData();
  cancelForm();
}

const confirmBan = () => {
  get(`user/ban/${ban.value}/${banId.value}`, () => {
    flash();
  })
  cancelForm();
}

const confirmDel = () => {
  del(`user/del/${delId.value}`, () => {
    flash();
  })
  cancelForm();
}

const cancelForm = () => {
  showAddFormFlag.value = false;
  showEditFormFlag.value = false;
  showBanFormFlag.value = false;
  showDelFormFlag.value = false;
  showSelectFormFlag.value = false;
  blur.value = false;
}

const resetFormData = () => {
  formData.id = null;
  formData.username = null;
  formData.name = null;
  formData.email = null;
  formData.gender = null;
  formData.age = null;
  formData.phone = null;
  formData.idNumber = null;
  formData.userGrant = 2;
}

const onCSV = () => {
  document.getElementById('csv').click();
}

let file;

const onFileChange = (event) => {
  file = event.target.files[0];
  const formData = new FormData();
  formData.append('file', file);
  post('/user/csv/add', formData, (data, msg) => {
    alert(msg);
  })
}
</script>

<style lang="less" scoped>
@import "../assets/css/manager";

table td:nth-child(1) {
  width: 15%;
}

table td:nth-child(2) {
  width: 14%;
}

table td:nth-child(4) {
  width: 23%;
}

table td:nth-child(3),
table td:nth-child(5),
table td:nth-child(6) {
  width: 5%;
}

table td:nth-child(7) {
  width: 10%;
}

table td:nth-child(8) {
  width: 7%;
}

table td:nth-child(9) {
  width: 16%;
}
</style>