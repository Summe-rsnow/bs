<template>
  <div class="container">
    <div class="title">
      <h1>学生成绩管理系统</h1>
    </div>
    <div v-if="!showForgetFormFlag" class="login-form">
      <h2>请登录账号以使用系统</h2>
      <input v-model="loginForm.username" placeholder="账号" type="text">
      <input v-model="loginForm.password" placeholder="密码" type="password">
      <div class="code">
        <input v-model="loginForm.verificationCode" placeholder="请输入验证码" type="text">
        <img alt="" src="" @click="refreshVerificationCode">
      </div>
      <div class="msg">
        <div>{{ message }}</div>
      </div>
      <div class="function">
        <div @click="forget">忘记密码？</div>
        <button @click="login">登录</button>
        <div class="remember"><input type="checkbox">
          <div>记住我</div>
        </div>
      </div>
    </div>
    <div v-else class="forget-password-form">
      <h2>输入需要找回密码的账号</h2>
      <input v-model="forgetForm.username" placeholder="账号" type="text">
      <input v-model="forgetForm.newPwd" placeholder="新密码" type="password">
      <input v-model="forgetForm.confirmPwd" placeholder="确定密码" type="password">
      <div class="verificationCode">
        <input v-model="forgetForm.verificationCode" placeholder="验证码" type="text">
        <button @click="getVerificationCode">获取验证码</button>
      </div>
      <div class="function">
        <button class="l" @click="forgetPwd">确定</button>
        <button class="r" @click="showForgetFormFlag = false">返回</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {get, post} from '../net/index';
import {useUserStore} from "../stores/index.js";
import {onMounted, reactive, ref} from "vue";
import router from "../router/index.js";

const userStore = useUserStore();
const showForgetFormFlag = ref(false);
const message = ref("请输入账号，密码以及验证码，后点击登录按钮进行登录操作");
const username = ref('');
const loginForm = reactive({
  username: '',
  password: '',
  verificationCode: ''
})
const forgetForm = reactive({
  username: '',
  verificationCode: '',
  newPwd: '',
  confirmPwd: ''
})
onMounted(() => {
  refreshVerificationCode();
})

// 发送登录请求到后端
const login = () => {
  post('/user/login', loginForm,
      (data, msg) => {
        userStore.user = data;
        userStore.token = data.token;
        router.push('/home');
      }
  );
}

const forget = () => {
  showForgetFormFlag.value = true;
}

const forgetPwd = () => {
  if (forgetForm.newPwd !== forgetForm.confirmPwd) {
    alert('确认密码于新密码不一致！');
  } else {
    // 提交表单数据
    post('/user/pwd/forget', forgetForm,
        (data, msg) => {
          alert(msg);
          showForgetFormFlag.value = false;
        },
        (msg) => {
          alert(msg);
        }
    );
  }
}

// 刷新验证码
const refreshVerificationCode = () => {
  document.querySelector('.code img').src = `/api/common/vcode?${Date.now()}`;
}

const getVerificationCode = () => {
  get(`/user/phoneCode/${forgetForm.username}`, () => {

  })
}
</script>

<style lang="less" scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;

  .title {
    margin-top: 130px;
    color: #fff;
    font-size: 28px;
    text-shadow: -1px -1px 0 #000000, 1px -1px 0 #000000, -1px 1px 0 #000000, 1px 1px 0 #000000;
  }

  .login-form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
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
        justify-content: space-between;
        align-items: center;
        text-align: center;

        input {
          margin: 5px;
          width: auto;
        }
      }
    }
  }

  .forget-password-form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
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

    .verificationCode {
      width: 382px;
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: space-between;

      input {
        width: 65%;
        margin-bottom: 20px;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 8px;
      }

      button {
        margin-bottom: 20px;
        padding: 10px;
        background-color: #63e6be;
        color: white;
        border-style: none;
        border-radius: 8px;
        cursor: pointer;

        &:hover {
          background-color: #4fcfa9;
        }
      }
    }

    .function {
      display: flex;
      justify-content: space-around;
      align-items: center;
      text-align: center;
      width: 90%;

      button {
        margin: 20px 10px 10px;
        padding: 10px 24px;
        color: white;
        font-weight: 900;
        font-size: 20px;
        border-style: none;
        border-radius: 8px;
        cursor: pointer;
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
}
</style>