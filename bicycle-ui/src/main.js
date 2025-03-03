import { createApp } from 'vue';
import App from '@/App.vue';
import router from '@/router';
import axios from 'axios';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'; // 引入 Element Plus 样式

// 配置 axios 默认 URL
axios.defaults.baseURL = 'http://localhost:8080';

const app = createApp(App);
app.use(router);
app.use(ElementPlus); // 使用 Element Plus
app.mount('#app');