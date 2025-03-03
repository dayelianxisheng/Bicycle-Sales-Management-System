<template>
  <div class="login-page">
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
      <div class="circles">
        <div class="circle"></div>
        <div class="circle"></div>
        <div class="circle"></div>
        <div class="circle"></div>
        <div class="circle"></div>
      </div>
    </div>
    
    <div class="login-container">
      <div class="login-card">
        <div class="brand">
          <div class="logo">🚲</div>
          <h1>自行车销售管理系统</h1>
          <p>智慧销售，高效管理</p>
        </div>
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-item">
            <label for="username">用户名</label>
            <div class="input-wrapper">
              <i class="icon">👤</i>
              <input 
                id="username"
                name="username"
                type="text" 
                v-model="username" 
                required
                placeholder="请输入用户名"
                autocomplete="username"
              >
            </div>
          </div>
          <div class="form-item">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <i class="icon">🔒</i>
              <input 
                id="password"
                name="password"
                :type="showPassword ? 'text' : 'password'" 
                v-model="password" 
                required
                placeholder="请输入密码"
                autocomplete="current-password"
              >
              <button 
                type="button"
                class="toggle-password"
                @click="showPassword = !showPassword"
                aria-label="切换密码显示"
              >
                {{ showPassword ? '👁️' : '👁️‍🗨️' }}
              </button>
            </div>
          </div>
          <div class="form-options">
            <label class="remember">
              <input 
                type="checkbox" 
                id="remember"
                name="remember"
                v-model="rememberPassword"
              >
              <span>记住密码</span>
            </label>
            <a href="#" class="forgot" tabindex="0">忘记密码？</a>
          </div>
          <button 
            type="submit" 
            :disabled="isLoading"
            class="submit-button"
          >
            {{ isLoading ? '登录中...' : '登 录' }}
          </button>
        </form>
      </div>
      <div class="footer">
        <p>© 2024 自行车销售管理系统 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const username = ref('');
const password = ref('');
const rememberPassword = ref(false);
const showPassword = ref(false);
const isLoading = ref(false);
const router = useRouter();

// 自动填充默认账号密码
onMounted(() => {
  username.value = 'admin';
  password.value = '123456';
});

const handleLogin = async () => {
  if (isLoading.value) return;
  
  try {
    isLoading.value = true;
    const response = await axios.post('/api/users/login', {
      username: username.value,
      password: password.value
    });

    if (response.data.code === 200) {
      const token = response.data.data;
      localStorage.setItem('token', token);
      localStorage.setItem('username', username.value);
      ElMessage.success('登录成功');
      router.push('/layout/home');
    } else {
      ElMessage.error(response.data.message || '登录失败');
    }
  } catch (error) {
    console.error('登录错误:', error);
    ElMessage.error('登录失败');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.shape {
  position: absolute;
  filter: blur(90px);
  opacity: 0.3;
}

.shape:nth-child(1) {
  background: #3b82f6;
  width: 500px;
  height: 500px;
  top: -250px;
  right: -100px;
  border-radius: 32% 68% 65% 35% / 59% 59% 41% 41%;
  animation: float-1 15s ease-in-out infinite;
}

.shape:nth-child(2) {
  background: #10b981;
  width: 400px;
  height: 400px;
  bottom: -200px;
  left: -100px;
  border-radius: 41% 59% 47% 53% / 41% 44% 56% 59%;
  animation: float-2 12s ease-in-out infinite;
}

.circles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.circle {
  position: absolute;
  border: 2px solid rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.circle:nth-child(1) {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 30%;
  animation: float-circle 8s linear infinite;
}

.circle:nth-child(2) {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 25%;
  animation: float-circle 12s linear infinite reverse;
}

.circle:nth-child(3) {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 25%;
  animation: float-circle 6s linear infinite;
}

.circle:nth-child(4) {
  width: 80px;
  height: 80px;
  top: 30%;
  right: 15%;
  animation: float-circle 10s linear infinite reverse;
}

.circle:nth-child(5) {
  width: 120px;
  height: 120px;
  bottom: 30%;
  left: 15%;
  animation: float-circle 15s linear infinite;
}

@keyframes float-1 {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(50px, 50px) rotate(180deg);
  }
}

@keyframes float-2 {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(-30px, -50px) rotate(-180deg);
  }
}

@keyframes float-circle {
  0% {
    transform: rotate(0deg) translate(20px) rotate(0deg);
  }
  100% {
    transform: rotate(360deg) translate(20px) rotate(-360deg);
  }
}

.login-container {
  width: 100%;
  max-width: 460px;
  text-align: center;
  position: relative;
  z-index: 1;
}

.login-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.brand {
  text-align: center;
  margin-bottom: 40px;
}

.brand .logo {
  font-size: 48px;
  margin-bottom: 16px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.brand h1 {
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 8px;
}

.brand p {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-item {
  text-align: left;
}

.form-item label {
  display: block;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 8px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper .icon {
  position: absolute;
  left: 16px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
}

.input-wrapper input {
  width: 100%;
  height: 48px;
  padding: 0 16px 0 44px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.input-wrapper input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.input-wrapper input:focus {
  border-color: rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
  outline: none;
}

.toggle-password {
  position: absolute;
  right: 16px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  padding: 0;
  font-size: 16px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.remember {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.7);
}

.remember input[type="checkbox"] {
  width: 16px;
  height: 16px;
  margin: 0;
}

.forgot {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
}

.forgot:hover {
  color: #ffffff;
  transition: color 0.3s ease;
}

.submit-button {
  width: 100%;
  height: 48px;
  background: rgba(59, 130, 246, 0.3);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-button:not(:disabled):hover {
  background: rgba(59, 130, 246, 0.5);
  transform: translateY(-1px);
}

.submit-button:not(:disabled):active {
  transform: translateY(0);
}

.submit-button:disabled {
  background: rgba(59, 130, 246, 0.2);
  cursor: not-allowed;
}

.footer {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.footer:hover {
  color: rgba(255, 255, 255, 0.8);
  transition: color 0.3s ease;
}

@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
  }
  
  .login-container {
    max-width: 420px;
  }
  
  .brand h1 {
    font-size: 20px;
  }
}

input:focus,
button:focus,
a:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

.submit-button:focus:not(:disabled) {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.5);
}
</style>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>