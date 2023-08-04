<template>
  <div class="container">
    <h1>公告管理</h1>
    <div class="notice">
      <div class="title">
        <label for="title">标题：</label>
        <input id="title" type="text" v-model="noticeFormData.title"/>
      </div>
      <div class="text">
        <label for="content">内容：</label>
        <textarea id="content" v-model="noticeFormData.text"></textarea>
      </div>
      <div class="function">
        <div class="grant">
          <label>发送给：</label>
          <label>
            <input type="checkbox" v-model="noticeFormData.grants" value="0"/>管理员
          </label>
          <label>
            <input type="checkbox" v-model="noticeFormData.grants" value="1"/>教师
          </label>
          <label>
            <input type="checkbox" v-model="noticeFormData.grants" value="2"/>学生
          </label>
        </div>
        <div class="hour">
          <label>公告时长：</label>
          <input v-model="noticeFormData.hours" type="number" min="1" max="100">
          <select v-model="timeFlag">
            <option value="1">小时</option>
            <option value="24">天</option>
            <option value="168">星期</option>
            <option value="720">月（30天）</option>
          </select>
        </div>
      </div>
      <button @click="showNoticeFlag=true">提交</button>
    </div>
    <div v-if="showNoticeFlag" class="confirm">
      <h3>确认提交</h3>
      <div class="button">
        <button @click="submitNoticeForm" class="l">确定</button>
        <button @click="showNoticeFlag=false" class="r">取消</button>
      </div>
    </div>
    <div v-if="showNoticeFlag" class="blur-background"></div>
  </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import {post} from "../net/index.js";

const showNoticeFlag = ref(false);
const timeFlag = ref(1);

const noticeFormData = reactive({
  title: '',
  text: '',
  grants: [],
  hours: 0,
});

const submitNoticeForm = () => {
  if (!noticeFormData.title) {
    alert('请输入公告标题')
    return
  }
  if (!noticeFormData.text) {
    alert('请输入公告内容')
    return
  }
  if (!noticeFormData.grants.length) {
    alert('请选择要发送公告的对象')
    return
  }
  if (noticeFormData.hours < 1 || noticeFormData.hours > 100) {
    alert('请输入正确的时间数值(1-100)')
    return
  }
  noticeFormData.hours = Math.floor(noticeFormData.hours);
  noticeFormData.hours *= timeFlag.value;
  post('notice/set', noticeFormData, (data, msg) => {
    alert(msg);
  })
  formDataRest();
  showNoticeFlag.value = false;
}

const formDataRest = () => {
  noticeFormData.title = '';
  noticeFormData.text = '';
  noticeFormData.grants = [];
  noticeFormData.hours = 0;
}
</script>

<style lang="less" scoped>
.container {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;

  h1 {
    margin: 0 0 20px;
    padding: 20px 660px 10px;
    border-bottom: 2px solid #ccc;
  }

  .notice {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 80%;
    height: 95%;
    padding: 0;
    margin: 20px;
    border: 1px solid #ccc;
    border-radius: 20px;
    background-color: #242424;
    box-shadow: 14px 14px 40px #343a40;

    .title,
    .text {

      label {
        font-size: 30px;
        font-weight: 560;
        margin: 20px 0;
      }

      input, textarea {
        width: 90%;
        padding: 10px;
        margin: 10px 5px;
        border-radius: 8px;
        border: 1px solid #ccc;
        font-family: "Helvetica Neue", Arial, sans-serif;
        font-size: 20px;
        color: #555;
        background-color: #f5f5f5;
        /* 添加盒模型的样式，确保宽度包括 padding 和 border */
        box-sizing: border-box;
        /* 添加垂直对齐的样式 */
        vertical-align: middle;
      }
    }

    .title {
      width: 28%;
    }

    .text {
      width: 80%;

      textarea {
        height: 430px;
      }
    }

    .function {
      .grant {
      }

      .hour {

      }

      input, select {
        border: none;
        border-radius: 8px;
        margin: 10px;
        padding: 8px;
      }
    }
  }

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

  .confirm {
    width: 400px;
    border-radius: 8px;
    background-color: rgb(255, 255, 255);
    color: #343a40;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 20px;
    z-index: 2;

    .button {
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
        box-shadow: 4px 3px 6px #343a40;

        &:hover {
          box-shadow: 4px 3px 6px #343a40 inset;
        }
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
