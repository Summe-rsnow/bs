import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import {createPinia} from "pinia";
import router from './router/index'
import ECharts from "vue-echarts";
import 'echarts';

const app = createApp(App);
const pinia = createPinia();

app.use(router);
app.use(pinia);
app.component('ECharts', ECharts);

app.mount('#app');

