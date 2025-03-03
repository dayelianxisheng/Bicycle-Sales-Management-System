<template>
  <div class="customer-list">
    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryForm" class="query-form">
      <el-form-item label="客户姓名">
        <el-input v-model="queryForm.name" placeholder="请输入客户姓名" clearable />
      </el-form-item>
      <el-form-item label="手机号码">
        <el-input v-model="queryForm.phone" placeholder="请输入手机号码" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="handleAdd">新增客户</el-button>
      </el-form-item>
    </el-form>

    <!-- 客户列表 -->
    <el-table :data="customerList" border stripe style="width: 100%">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="name" label="客户姓名" width="120" />
      <el-table-column prop="phone" label="手机号码" width="140" />
      <el-table-column prop="email" label="电子邮箱" width="180" />
      <el-table-column prop="address" label="联系地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="注册时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="handleOrders(row)">订单</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑客户对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="customer-form"
      >
        <el-form-item label="客户姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入电子邮箱" />
        </el-form-item>
        <el-form-item label="联系地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入联系地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 查询表单数据
const queryForm = reactive({
  name: '',
  phone: ''
})

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const customerList = ref([])

// 对话框相关数据
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  phone: '',
  email: '',
  address: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入客户姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 获取客户列表数据
const getCustomerList = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      name: queryForm.name,
      phone: queryForm.phone
    }
    const res = await axios.get('/api/customers', { params })
    if (res.data.code === 200) {
      customerList.value = res.data.data.records
      total.value = res.data.data.total || res.data.data.records.length
    } else {
      ElMessage.error(res.data.message || '获取客户列表失败')
    }
  } catch (error) {
    console.error('获取客户列表出错：', error)
    ElMessage.error('获取客户列表失败')
  }
}

// 查询按钮点击事件
const handleSearch = () => {
  currentPage.value = 1
  getCustomerList()
}

// 重置按钮点击事件
const handleReset = () => {
  queryForm.name = ''
  queryForm.phone = ''
  handleSearch()
}

// 分页大小改变事件
const handleSizeChange = (val) => {
  pageSize.value = val
  getCustomerList()
}

// 当前页改变事件
const handleCurrentChange = (val) => {
  currentPage.value = val
  getCustomerList()
}

// 新增客户
const handleAdd = () => {
  dialogTitle.value = '新增客户'
  Object.assign(form, {
    id: null,
    name: '',
    phone: '',
    email: '',
    address: ''
  })
  dialogVisible.value = true
}

// 编辑客户
const handleEdit = (row) => {
  dialogTitle.value = '编辑客户'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 查看客户订单
const handleOrders = (row) => {
  router.push(`/layout/customer-orders/${row.id}`)
}

// 删除客户
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该客户吗？删除后无法恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.delete(`/api/customers/${row.id}`)
      if (res.data.code === 200) {
        ElMessage.success('删除成功')
        getCustomerList()
      } else {
        ElMessage.error(res.data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除客户出错：', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const method = form.id ? 'put' : 'post'
    const url = form.id ? `/api/customers/${form.id}` : '/api/customers'
    const res = await axios[method](url, form)
    
    if (res.data.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      getCustomerList()
    } else {
      ElMessage.error(res.data.message || (form.id ? '更新失败' : '添加失败'))
    }
  } catch (error) {
    console.error('提交表单出错：', error)
    ElMessage.error(form.id ? '更新失败' : '添加失败')
  }
}

// 对话框关闭事件
const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 页面加载时获取数据
onMounted(() => {
  getCustomerList()
})
</script>

<style scoped>
.customer-list {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.customer-form {
  padding: 20px 0;
}

:deep(.el-dialog__body) {
  padding-top: 10px;
  padding-bottom: 10px;
}

.dialog-footer {
  text-align: right;
}
</style> 