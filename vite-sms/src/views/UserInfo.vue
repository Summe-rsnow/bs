<template>
  <div class="container">
    <div class="info">
      <div class="ul">
        <h1 style="margin: 0; padding:0;">个人信息</h1>
        <ul>
          <!-- 显示 userStore 中的数据 -->
          <li><strong>ID:</strong> {{ userStore.user.id }}</li>
          <li><strong>用户名:</strong> {{ userStore.user.username }}</li>
          <li><strong>姓名:</strong> {{ userStore.user.name }}</li>
          <li><strong>邮箱:</strong> {{ userStore.user.email }}</li>
          <li><strong>性别:</strong> {{ userStore.user.gender }}</li>
          <li><strong>年龄:</strong> {{ userStore.user.age }}</li>
          <li><strong>手机号码:</strong> {{ userStore.user.phone }}</li>
          <li><strong>身份证号码:</strong> {{ userStore.user.idNumber }}</li>
          <li><strong>身份:</strong> {{ userGrantLevel[userStore.user.userGrant] }}</li>
        </ul>
        <div class="button">
          <button @click="showForm">修改信息</button>
          <button @click="showPwd">修改密码</button>
        </div>
      </div>
      <div class="avatar">
        <h1 style="margin: 10px; padding:0;">个人头像</h1>
        <img :src="imagePath" alt="">
        <div class="file">
          <div class="select">
            <label for="select">选择头像</label>
            <input id="select" accept="image/*" name="select" type="file" v-on:change="onFileChange">
          </div>
          <button class="upload" @click="uploadFile">上传头像</button>
        </div>
      </div>
    </div>
    <div v-if="showFormFlag" class="form">
      <h2>修改信息</h2>
      <form>
        <label for="name">姓名：</label>
        <input id="name" v-model="formData.name" type="text"><br>

        <label for="email">邮箱：</label>
        <input id="email" v-model="formData.email" type="text"><br>

        <label for="gender">性别：</label>
        <select id="gender" v-model="formData.gender">
          <option value="男">男</option>
          <option value="女">女</option>
        </select><br>

        <label for="age">年龄：</label>
        <input id="age" v-model="formData.age" type="number"><br>

        <label for="phone">手机号码：</label>
        <input id="phone" v-model="formData.phone" type="text"><br>

        <label for="idNumber">身份证号码：</label>
        <input id="idNumber" v-model="formData.idNumber" type="text"><br>

        <div class="formbutton">
          <button class="l" @click="submitForm">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <div v-if="showPwdFlag" class="form">
      <h2>修改密码</h2>
      <form>
        <label for="oldPwd">原密码：</label>
        <input id="oldPwd" v-model="pwd.oldPwd" type="password"><br>

        <label for="newPwd">新密码：</label>
        <input id="newPwd" v-model="pwd.newPwd" type="password"><br>

        <label for="confirmPwd">确认密码：</label>
        <input id="confirmPwd" v-model="pwd.confirmPwd" type="password"><br>
        <div class="formbutton">
          <button class="l" @click="submitPwd">保存</button>
          <button class="r" @click="cancelForm">取消</button>
        </div>
      </form>
    </div>
    <Cropper
        v-if="cropperVisible"
        :imagePath="imagePath"
        fileType="blob"
        imageType="image/png"
        mode="scale"
        @cancel="onCancel"
        @save="onSave"
    />
    <!-- 背景模糊层 -->
    <div :class="{ 'blur-background': blur === true }"></div>
  </div>
</template>

<script setup>
import Cropper from "vue3-cropper";
import 'vue3-cropper/lib/vue3-cropper.css';
import {reactive, ref} from "vue";
import {useUserStore} from "../stores/index.js";
import {avatar, post} from '../net/index.js';
import router from "../router/index.js";

const userStore = useUserStore();
const userGrantLevel = reactive(['管理员', '教师', '学生']);
const blur = ref(false);
const showFormFlag = ref(false);
const showPwdFlag = ref(false);
const pwd = reactive({
  oldPwd: '',
  newPwd: '',
  confirmPwd: ''
});
const cropperVisible = ref(false);
const imagePath = ref(userStore.avatarSrc);
let file;

// 创建新的响应式对象来保存表单数据
const formData = reactive({
  id: userStore.user.id,
  name: userStore.user.name,
  email: userStore.user.email,
  gender: userStore.user.gender,
  age: userStore.user.age,
  phone: userStore.user.phone,
  idNumber: userStore.user.idNumber,
});

const onFileChange = (event) => {
  file = event.target.files[0];
  imagePath.value = URL.createObjectURL(file);
  const input = document.getElementById('select');
  input.value = ''; // 清空文件选择
  cropperVisible.value = true;
}

const onSave = (res) => {
  if (typeof res === "string") {
    imagePath.value = res;
  } else {
    imagePath.value = URL.createObjectURL(res);
  }
  file = new File([res], "t.png", {type: "image/png"});
  cropperVisible.value = false;
};

const uploadFile = () => {
  if (!file) {
    alert('请先选择头像再上传！')
  }
  const formData = new FormData();
  formData.append('file', file);
  avatar(formData,
      (data, msg) => {
        userStore.user.avatar = msg;
        userStore.avatarSrc = `api/common/avatar/download?${Date.now()}`;
      })
}

const showForm = () => {
  blur.value = true;
  showFormFlag.value = true;
}

const showPwd = () => {
  blur.value = true;
  showPwdFlag.value = true;
}

const submitForm = () => {
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
  post('/user/self/edit', formData,
      (data) => {
        // 更新 userStore 中的数据
        userStore.user = data;
      }
  );
  cancelForm();
}

const submitPwd = () => {
  if (!pwd.oldPwd) {
    alert('请输入原密码！');
  } else if (pwd.newPwd !== pwd.confirmPwd) {
    alert('确认密码于新密码不一致！');
  } else {
    // 提交表单数据
    post('/user/pwd', pwd,
        () => {
          // 更新 userStore 中的数据
          router.push('/');
        },
        (msg) => {
          alert(msg);
        }
    );
  }
  cancelForm();
}

const cancelForm = () => {
  showFormFlag.value = false;
  showPwdFlag.value = false;
  blur.value = false;
}

const onCancel = () => {
  file = undefined;
  imagePath.value = userStore.avatarSrc;
  cropperVisible.value = false;
}
</script>

<style lang="less" scoped>
.container {
  display: flex;
  flex-direction: row;
  justify-content: center;
  width: 100%;
  height: 100%;

  .info {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    width: 80%;
    height: 95%;
    padding: 0;
    margin: 20px;
    border: 1px solid #ccc;
    border-radius: 20px;
    background-color: #242424;
    box-shadow: 14px 14px 40px #343a40;

    .ul {
      width: 20%;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px 40px;
      margin: 10px;
      border-radius: 20px;

      ul {
        margin: 0;
        padding: 0;
        list-style-type: none;
        text-align: center;

        li {
          text-align: left;
          height: 74px;
          line-height: 80px;
        }
      }

      .button {
        display: flex;
        width: 100%;
        flex-direction: row;
        justify-content: space-between;


        button {
          padding: 10px 20px;
          border: none;
          border-radius: 8px;
          font-size: 16px;
          background-color: #4d4d72;
          color: white;
          cursor: pointer;

          &:hover {
            background-color: #414157;
          }
        }
      }
    }

    .avatar {
      height: 400px;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px 40px;
      margin: 30px 200px 140px;
      border-radius: 20px;

      img {
        width: 250px;
        height: 250px;
        border: 1px solid #343a40;
        border-radius: 8px;
        box-shadow: 25px 25px 25px #000;
      }

      .file {
        margin-top: 30px;
        width: 100%;
        display: flex;
        justify-content: space-around;

        .select {
          padding: 4px 8px;
          color: #343a40;
          font-size: 17px;
          border-radius: 8px;
          box-shadow: 10px 10px 25px #000;
          backdrop-filter: blur(14px);
          background-color: rgba(255, 255, 255, 0.58);
          cursor: pointer;

          input[type="file"] {
            display: none;
          }
        }

        .upload {
          border: none;
          padding: 4px 8px;
          color: #343a40;
          font-size: 17px;
          border-radius: 8px;
          box-shadow: 10px 10px 25px #000;
          backdrop-filter: blur(14px);
          background-color: rgba(255, 255, 255, 0.58);
          cursor: pointer;
        }
      }
    }
  }


  .form {
    width: 400px;
    border-radius: 8px;
    background-color: rgb(255, 255, 255);
    color: #000;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 20px;
    z-index: 2;

    label {
      display: block;
      font-size: 17px;
      font-weight: bold;
      margin: 5px 0;
    }

    input {
      width: 90%;
      padding: 10px;
      margin: 0 5px;
      font-size: 14px;
      border-radius: 8px;
      border: 1px solid #ccc;
    }

    select {
      width: 16%;
      padding: 10px;
      margin: 0 5px;
      font-size: 14px;
      border-radius: 8px;
      border: 1px solid #ccc;
    }

    .formbutton {
      display: flex;
      flex-direction: row;
      justify-content: space-between;

      button {
        padding: 10px 20px;
        font-size: 16px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        margin-top: 15px;
      }

      .l {
        background-color: #63e6be;

        &:hover {
          background-color: #4fcfa9;
        }
      }

      .r {
        background-color: #ffa8a8;

        &:hover {
          background-color: #dd8181;
        }
      }
    }
  }

  .blur-background {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(10px);
    z-index: 1;
  }
}
</style>