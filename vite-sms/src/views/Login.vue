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
        <div class="remember">
          <input v-model="loginForm.rememberMe" type="checkbox">
          <div>记住我</div>
        </div>
      </div>
    </div>
    <div v-else class="forget-password-form">
      <h2>找回密码</h2>
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
import {onBeforeMount, onMounted, reactive, ref} from "vue";
import router from "../router/index.js";

const userStore = useUserStore();
const showForgetFormFlag = ref(false);
const message = ref("请输入账号，密码以及验证码，后点击登录按钮进行登录操作");
const username = ref('');
const loginForm = reactive({
  username: '',
  password: '',
  verificationCode: '',
  rememberMe: false
})
const forgetForm = reactive({
  username: '',
  verificationCode: '',
  newPwd: '',
  confirmPwd: ''
})

onBeforeMount(() => {
  rememberMe();
})

onMounted(() => {
  refreshVerificationCode();
})

// 发送登录请求到后端
const login = () => {
  post('/user/login', loginForm,
      (data) => {
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
  get(`/user/phone_code/${forgetForm.username}`, () => {

  })
}

const rememberMe = () => {
  const cookies = document.cookie.split(';');
  let rememberMeValue = '';

  for (let i = 0; i < cookies.length; i++) {
    const cookie = cookies[i].trim();
    if (cookie.startsWith('rememberMe=')) {
      rememberMeValue = cookie.substring('rememberMe='.length);
      break;
    }
  }

  if (rememberMeValue) {
    userStore.token = rememberMeValue;
    get('/user/self/info', (data) => {
      userStore.user = data;
      router.push('/home');
    })
  }
}
</script>

<style lang="less" scoped>
@import "../assets/css/login.less";
</style>