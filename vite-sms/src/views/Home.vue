<template>
  <div class="container">
    <div class="header">
      <div class="user">
        <img :src="userStore.avatarUrl" alt=""
             style="width: 50px;height: 50px;border-radius: 25px;margin: 0 15px">
        <h3>
          {{ userStore.user.name }}
        </h3>
      </div>
      <h1>您好，欢迎使用学生成绩管理系统</h1>
      <div class="date-time">当前时间是：{{ currentDate }}</div>
      <div class="logout" @click="logout">
        <button>退出登录</button>
      </div>
    </div>
    <div class="body">
      <div class="left-menu">
        <ul>
          <template v-for="item in menuItems" :key="item.id">
            <li v-if="item.grand.includes(userStore.user.userGrant)" :class="{ 'selected': selectedItem === item.id }"
                @click="selectMenuItem(item)">
              {{ item.label }}
            </li>
          </template>
        </ul>
      </div>
      <div class="right-content">
        <router-view></router-view>
      </div>
    </div>
    <div v-if="showNoticeFlag" class="notice">
      <h1>通知</h1>
      <h2>{{ notice.title }}</h2>
      <div class="text">
        <div>{{ notice.text }}</div>
      </div>
      <button :disabled="countdown > 0" @click="showNoticeFlag=false">
        确定{{ countdown > 0 ? ' (' + countdown + ')' : '' }}
      </button>
    </div>
    <div v-if="showNoticeFlag" class="blur-background"></div>
  </div>
</template>

<script setup>
import {onBeforeMount, onMounted, reactive, ref} from "vue";
import {useUserStore} from "../stores/index.js";
import {downloadAvatar, get} from "../net/index.js";
import router from "../router/index.js";

const userStore = useUserStore();
const selectedItem = ref('1');


const menuItems = reactive([
  {id: '1', label: '个人信息', router: '/info', grand: [0, 1, 2]},
  {id: '2', label: '查询成绩', router: '/select_grade', grand: [2]},
  {id: '3', label: '修改成绩', router: '/grade_manager', grand: [1]},
  {id: '4', label: '成员管理', router: '/user_manager', grand: [0]},
  {id: '5', label: '课程管理', router: '/course_manager', grand: [0]},
  {id: '6', label: '查询学生', router: '/select_student', grand: [1]},
  {id: '7', label: '查询课程', router: '/select_course', grand: [1]},
  {id: '8', label: '数据可视化', router: '/admin_data', grand: [0]},
])

const selectMenuItem = (item) => {
  selectedItem.value = item.id;
  router.push(item.router);
}

const logout = () => {
  get('user/logout',
      (data, msg) => {
        userStore.user = null;
        userStore.token = '';
        //去掉cookie
        document.cookie = "rememberMe=; Max-Age=0; path=/";
        router.push('/');
      })
}

/*当前日期时间*/
const currentDate = ref('');

onBeforeMount(() => {
  downloadAvatar();
  // 获取当前日期、星期和时间  每秒执行一次
  setInterval(() => {
    const now = new Date();
    const options = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      second: 'numeric'
    };
    currentDate.value = now.toLocaleDateString('zh-CN', options);
  }, 1000)
})

onMounted(() => {
  router.push("/info");
  getNotice();
  countdownInterval();
})

const countdown = ref(5);

const countdownInterval = () => {
  setInterval(() => {
    countdown.value--;
  }, 1000);
}

const showNoticeFlag = ref(false);

const notice = reactive({
  title: '',
  text: ''
})

const getNotice = () => {
  get('notice/notice/get', (data, msg) => {
        showNoticeFlag.value = true;
        msg = msg.slice(1, -1);
        const split = msg.split(', ');
        notice.title = split[2].split('=')[1];
        notice.text = split[3].split('=')[1];
      }, (msg) => {

        console.log(msg);
      }
  )
}
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
}

.header {
  backdrop-filter: blur(10px);
  height: 70px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.user {
  display: flex;
  flex-direction: row;
  text-align: center;
  align-items: center;
}

.logout button {
  margin-right: 20px;
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background-color: #ffa8a8;
}

.logout button:hover {
  background-color: #dd8181;
}

.body {
  flex-grow: 1;
  display: flex;
  flex-direction: row;
}

.left-menu {
  min-width: 200px;
  backdrop-filter: blur(10px);
}

ul {
  margin: 0;
  padding: 0;
  list-style-type: none;
  text-align: center;

}

li {
  text-align: center;
  height: 80px;
  line-height: 80px;
  font-size: 18px;
  font-weight: 700;
}

.selected {
  background-color: #868e96;
}

.right-content {
  box-shadow: 4px 3px 6px #343a40 inset;
  border-left: 1px solid #ccc;
  border-top: 1px solid #ccc;
  flex-grow: 1;
  background-color: #868e96;
}

.notice {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 800px;
  height: 830px;
  padding: 24px;
  background-color: #414157;
  text-align: center;
  z-index: 2;
  border-radius: 8px;
}

.notice h1 {
  font-size: 38px;
  margin: 10px 0;
}

.notice h2 {
  font-size: 24px;
  margin: 10px 0;
}

.notice .text {
  height: 650px;
  margin: 14px 0;
  background-color: #4d4d72;
  display: flex;
  align-content: center;
  justify-content: flex-start;
  padding: 10px;
  color: #ffffff;
  font-size: 18px;
  line-height: 1.5;
}

.notice button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  background-color: #63e6be;
  color: white;
  cursor: pointer;
}

.notice button:hover {
  background-color: #4fcfa9;
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
</style>