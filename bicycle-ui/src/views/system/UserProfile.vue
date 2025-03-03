<template>
  <div class="app-container">
    <div class="profile-container">
      <!-- 左侧个人信息卡片 -->
      <el-card class="info-card">
        <div class="avatar-container">
          <div class="avatar-wrapper" @click="handleClickAvatar">
            <el-avatar 
              :size="100" 
              :src="avatarUrl" 
              @error="handleAvatarError"
            >
              <el-icon :size="50"><User /></el-icon>
            </el-avatar>
          </div>
          <div class="username">{{ userInfo.nickname || userInfo.username }}</div>
        </div>
        
        <div class="info-list">
          <div class="info-item">
            <el-icon><User /></el-icon>
            <span class="label">用户名称</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <el-icon><Phone /></el-icon>
            <span class="label">手机号码</span>
            <span class="value">{{ userInfo.mobile || '未设置' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Message /></el-icon>
            <span class="label">用户邮箱</span>
            <span class="value">{{ userInfo.email || '未设置' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Office /></el-icon>
            <span class="label">所属部门</span>
            <span class="value">{{ userInfo.deptId ? '部门' + userInfo.deptId : '未设置' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Position /></el-icon>
            <span class="label">所属岗位</span>
            <span class="value">{{ userInfo.postIds || '未设置' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Timer /></el-icon>
            <span class="label">创建时间</span>
            <span class="value">{{ formatDate(userInfo.createTime) }}</span>
          </div>
          <div class="info-item">
            <el-icon><Timer /></el-icon>
            <span class="label">最后登录</span>
            <span class="value">{{ formatDate(userInfo.loginDate) }}</span>
          </div>
        </div>
      </el-card>

      <!-- 右侧基本信息表单 -->
      <el-card class="form-card">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本资料" name="basic">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
              <el-form-item label="用户昵称" prop="nickname">
                <el-input v-model="form.nickname" placeholder="请输入用户昵称" />
              </el-form-item>
              <el-form-item label="手机号码" prop="mobile">
                <el-input v-model="form.mobile" placeholder="请输入手机号码" />
              </el-form-item>
              <el-form-item label="用户邮箱" prop="email">
                <el-input v-model="form.email" placeholder="请输入用户邮箱" />
              </el-form-item>
              <el-form-item label="性别" prop="sex">
                <el-radio-group v-model="form.sex">
                  <el-radio :value="1">男</el-radio>
                  <el-radio :value="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSave">保存</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="password">
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword">保存</el-button>
                <el-button @click="handleResetPassword">重置</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="修改头像"
      width="500px"
      :close-on-click-modal="false"
      append-to-body
    >
      <div class="avatar-edit-container">
        <el-upload
          class="avatar-edit-uploader"
          action="#"
          :show-file-list="false"
          :on-change="handleAvatarChange"
          :before-upload="handleBeforeUpload"
          :auto-upload="false"
          name="file"
          accept=".jpg,.jpeg,.png"
          drag
          ref="uploadRef"
        >
          <img v-if="previewUrl" :src="previewUrl" class="avatar-preview" />
          <template v-else>
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
          </template>
          <template #tip>
            <div class="el-upload__tip">
              只能上传 jpg/png/jpeg 文件，且不超过 2MB
            </div>
          </template>
        </el-upload>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取 消</el-button>
          <el-button type="primary" @click="submitUpload">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { 
  User, 
  Iphone as Phone, 
  Message, 
  HomeFilled as Office,
  Location as Position,
  Timer,
  Plus,
  UploadFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const activeTab = ref('basic')
const formRef = ref(null)
const passwordFormRef = ref(null)

const userInfo = reactive({
  username: '',
  nickname: '',
  avatar: '',
  mobile: '',
  email: '',
  deptId: '',
  postIds: '',
  sex: 1,
  status: 0,
  createTime: null,
  updateTime: null,
  loginDate: null,
  loginIp: ''
})

const form = reactive({
  nickname: '',
  mobile: '',
  email: '',
  sex: 1
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const formatDate = (date) => {
  if (!date) return '未记录'
  return new Date(date).toLocaleString()
}

// 获取用户信息
const getUserInfo = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      return
    }

    const response = await axios.get('/api/users/profile', {
      headers: { 
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })

    console.log('获取用户信息响应:', response.data)

    if (response.data.code === 200) {
      const data = response.data.data
      // 确保头像路径正确更新
      Object.assign(userInfo, {
        ...data,
        avatar: data.avatar || ''  // 如果avatar为null，设置为空字符串
      })
      console.log('更新后的用户信息:', userInfo)
      console.log('头像URL:', avatarUrl.value)
      
      Object.assign(form, {
        nickname: data.nickname,
        mobile: data.mobile,
        email: data.email,
        sex: data.sex || 1
      })
    } else {
      ElMessage.error(response.data.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    if (error.response?.status === 403) {
      ElMessage.error('没有权限或 token 已过期，请重新登录')
    } else {
      ElMessage.error('获取用户信息失败')
    }
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    const token = localStorage.getItem('token')
    const response = await axios.put('/api/users/profile', form, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('保存成功')
      getUserInfo()
    } else {
      ElMessage.error(response.data.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    nickname: userInfo.nickname,
    mobile: userInfo.mobile,
    email: userInfo.email,
    sex: userInfo.sex
  })
  ElMessage.info('已重置')
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    const token = localStorage.getItem('token')
    const response = await axios.put('/api/users/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('密码修改成功')
      handleResetPassword()
    } else {
      ElMessage.error(response.data.msg || '密码修改失败')
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    ElMessage.error('密码修改失败')
  }
}

const handleResetPassword = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

// 上传相关
const uploadRef = ref(null)
const previewUrl = ref('')
const uploadFile = ref(null)  // 新增：保存上传文件的引用

// 处理文件选择前的验证
const handleBeforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理文件改变事件
const handleAvatarChange = (file) => {
  if (!file) return
  
  // 如果文件验证通过
  if (handleBeforeUpload(file.raw)) {
    // 创建预览URL
    previewUrl.value = URL.createObjectURL(file.raw)
    // 保存文件引用
    uploadFile.value = file.raw
  }
}

// 计算头像完整URL
const avatarUrl = computed(() => {
  if (!userInfo.avatar) return ''
  console.log('原始头像路径:', userInfo.avatar)
  // 如果已经是完整URL则直接返回
  if (userInfo.avatar.startsWith('http')) {
    console.log('使用完整URL:', userInfo.avatar)
    return userInfo.avatar
  }
  // 使用后端返回的路径
  const url = `http://localhost:8080${userInfo.avatar}`
  console.log('完整的头像URL:', url)
  return url
})

// 提交上传
const submitUpload = async () => {
  if (!uploadFile.value) {
    ElMessage.warning('请选择要上传的图片')
    return
  }

  try {
    const formData = new FormData()
    formData.append('file', uploadFile.value)

    console.log('开始上传头像...')
    const response = await axios.put('/api/users/avatar', formData, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'multipart/form-data'
      }
    })
    console.log('上传响应:', response.data)

    if (response.data.code === 200) {
      // 更新头像URL
      userInfo.avatar = response.data.data
      console.log('新的头像路径:', userInfo.avatar)
      ElMessage.success('头像上传成功')
      dialogVisible.value = false
      // 刷新用户信息
      await getUserInfo()
    } else {
      ElMessage.error(response.data.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    if (error.response?.status === 400) {
      ElMessage.error(error.response.data?.message || '请求参数错误')
    } else {
      ElMessage.error('头像上传失败，请重试')
    }
  } finally {
    // 清除上传文件和预览
    uploadFile.value = null
    previewUrl.value = ''
    if (uploadRef.value) {
      uploadRef.value.clearFiles()
    }
  }
}

// 关闭对话框时清理
const handleClose = () => {
  previewUrl.value = ''
  uploadFile.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
  dialogVisible.value = false
}

// 头像上传对话框
const dialogVisible = ref(false)

// 点击头像
const handleClickAvatar = () => {
  dialogVisible.value = true
}

// 处理头像加载错误
const handleAvatarError = (e) => {
  console.warn('头像加载失败:', e)
  console.warn('失败的URL:', e.target.src)
  userInfo.avatar = ''
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.app-container {
  padding: 20px;  /* 恢复原来的内边距 */
}

.profile-container {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 20px;
}

.avatar-container {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-wrapper:hover {
  opacity: 0.8;
}

.avatar-edit-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.avatar-edit-uploader {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 200px;
}

:deep(.el-upload__tip) {
  margin-top: 10px;
  text-align: center;
  color: var(--el-text-color-secondary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

:deep(.el-upload-dragger:hover) {
  border-color: var(--el-color-primary);
}

:deep(.el-icon--upload) {
  font-size: 48px;
  color: var(--el-text-color-secondary);
  margin-bottom: 10px;
}

:deep(.el-upload__text) {
  color: var(--el-text-color-regular);
  font-size: 14px;
  text-align: center;
}

:deep(.el-upload__text em) {
  color: var(--el-color-primary);
  font-style: normal;
}

.username {
  margin-top: 10px;
  font-size: 20px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.info-list {
  padding: 20px 0;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
}

.info-item .el-icon {
  margin-right: 8px;
  font-size: 18px;
  color: var(--el-text-color-secondary);
}

.info-item .label {
  color: var(--el-text-color-secondary);
  width: 80px;
}

.info-item .value {
  color: var(--el-text-color-primary);
  flex: 1;
}

.el-form {
  max-width: 460px;
  margin-top: 20px;
}

:deep(.el-tabs__nav) {
  margin-bottom: 20px;
}

.avatar-preview {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

/* 移除顶部导航栏相关样式 */
.top-nav,
.nav-right,
.username-text {
  display: none;
}
</style> 