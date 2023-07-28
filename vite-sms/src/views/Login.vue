<template>
  <div class="container">
    <div class="title">
      <h1>学生成绩管理系统</h1>
    </div>
    <div class="login-form">
      <h2>请登录账号以使用系统</h2>
      <input v-model="username" placeholder="账号" type="text">
      <input v-model="password" placeholder="密码" type="password">
      <div class="code">
        <input v-model="verificationCode" placeholder="请输入验证码" type="text">
        <img alt="" src="" @click="refreshVerificationCode">
      </div>
      <div class="msg">
        <div>{{ message }}</div>
      </div>
      <div class="function">
        <div>忘记密码？</div>
        <button @click="login">登录</button>
        <div class="remember"><input type="checkbox">
          <div>记住我</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {post} from '../net/index';
import {useUserStore} from "../stores/index.js";
import {onMounted, ref} from "vue";
import router from "../router/index.js";

const userStore = useUserStore();
const username = ref();
const password = ref();
const verificationCode = ref();
const message = ref("请输入账号，密码以及验证码，后点击登录按钮进行登录操作");
onMounted(() => {
  refreshVerificationCode();
})

// 发送登录请求到后端
const login = () => {
  // if (verificationCode.value!==sessionStorage.getItem("vcode")){
  //   return
  // }
  post('/user/login', {username: username.value, password: password.value, verificationCode: verificationCode.value},
      (data, msg) => {
        userStore.user = data;
        router.push('/home');
      },
      (msg) => {
        message.value = msg;
      }
  );
}
// 刷新验证码
const refreshVerificationCode = () => {
  document.querySelector('.code img').src = `/api/common/vcode?${Date.now()}`;
}
</script>

<style lang="less" scoped>
.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .title {
    margin-bottom: 150px;
    color: #fff;
    font-size: 28px;
    text-shadow: -1px -1px 0 #000000, 1px -1px 0 #000000, -1px 1px 0 #000000, 1px 1px 0 #000000;
  }

  .login-form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-bottom: 300px;
    width: 400px;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    backdrop-filter: blur(8px);


    h2 {
      font-size: 30px;
    }

    input {
      width: 90%;
      margin-bottom: 20px;
      padding: 10px;
      border: 1px solid #ccc;
      border-radius: 8px;
    }

    .msg {
      height: 50px;
      margin-top: 20px;
      padding: 4px 8px;
      border-radius: 8px;
      backdrop-filter: blur(14px);
      background-color: rgba(255, 255, 255, 0.58);
      width: 90%;

      div {
        display: flex;
        align-items: center;
        justify-content: center;
        color: #343a40;
        font-weight: 900;
        font-size: 17px;

      }
    }

    .code {
      width: 382px;
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-items: center;
      justify-content: space-between;

      input {
        width: 182px;
        margin: 0;
      }

      img {
        height: 37px;
        width: 140px;
        border: 1px solid #ccc;
        border-radius: 8px;
      }
    }

    .function {
      display: flex;
      justify-content: space-between;
      align-items: center;
      text-align: center;
      width: 90%;

      button {
        margin: 20px 10px 10px;
        padding: 10px 24px;
        background-color: #63e6be;
        color: white;
        font-weight: 900;
        font-size: 20px;
        border-style: none;
        border-radius: 8px;
        cursor: pointer;

        &:hover {
          background-color: #4fcfa9;
        }
      }

      .remember {
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;

        input {
          margin: 5px;
          width: auto;
        }
      }
    }
  }
}
</style>