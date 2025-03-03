// 获取用户列表
const fetchList = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/users')
    if (response.data.code === 200) {
      tableData.value = response.data.data
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
} 