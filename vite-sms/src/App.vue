<script setup>
import {useUserStore} from "./stores/index.js";
import {useRouter} from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const flash = () => {
  if (userStore.user == null) {
    router.push('/login');
  }
}
</script>

<template>
  <router-view></router-view>
</template>

<style scoped>
div {
  height: 100vh;
  background-image: url("./assets/bg.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
</style>
