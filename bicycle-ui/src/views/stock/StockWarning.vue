<template>
  <div class="stock-warning">
    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入商品名称/品牌/型号" clearable />
        </el-form-item>
        <el-form-item label="预警类型">
          <el-select v-model="searchForm.warningType" placeholder="请选择预警类型" clearable>
            <el-option label="库存不足" value="LOW" />
            <el-option label="库存过剩" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="warning" @click="handleBatchNotify">
            <el-icon><Bell /></el-icon>批量通知
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预警商品表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>预警商品列表</span>
          <div class="header-operations">
            <el-button type="primary" @click="handleRefresh">
              <el-icon><Refresh /></el-icon>刷新数据
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>导出数据
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        stripe
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" min-width="100" />
        <el-table-column prop="model" label="型号" min-width="100" />
        <el-table-column prop="stock" label="当前库存" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStockTagType(scope.row)" effect="light">
              {{ scope.row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningStock" label="预警阈值" width="100" align="center" />
        <el-table-column prop="price" label="单价" width="120" align="right">
          <template #default="scope">
            ¥{{ scope.row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="预警状态" width="120" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.stock === 0 ? 'danger' : 'warning'"
              effect="dark"
            >
              {{ scope.row.stock === 0 ? '无库存' : '库存不足' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleStockIn(scope.row)">
              <el-icon><Plus /></el-icon>入库
            </el-button>
            <el-button type="warning" size="small" @click="handleNotify(scope.row)">
              <el-icon><Bell /></el-icon>通知
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 入库对话框 -->
    <el-dialog v-model="stockInDialogVisible" title="商品入库" width="500px">
      <el-form :model="stockInForm" :rules="rules" ref="stockInFormRef" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ stockInForm.name }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ stockInForm.stock }}</span>
        </el-form-item>
        <el-form-item label="预警阈值">
          <span>{{ stockInForm.warningStock }}</span>
        </el-form-item>
        <el-form-item label="入库数量" prop="amount">
          <el-input-number
            v-model="stockInForm.amount"
            :min="1"
            :step="1"
            :step-strictly="true"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="stockInForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入入库备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockInDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmStockIn" :loading="submitting">
            确认入库
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 通知对话框 -->
    <el-dialog v-model="notifyDialogVisible" title="发送预警通知" width="500px">
      <el-form :model="notifyForm" :rules="notifyRules" ref="notifyFormRef" label-width="100px">
        <el-form-item label="通知方式" prop="notifyType">
          <el-radio-group v-model="notifyForm.notifyType">
            <el-radio label="EMAIL">邮件通知</el-radio>
            <el-radio label="SMS">短信通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="通知对象" prop="receivers">
          <el-select
            v-model="notifyForm.receivers"
            multiple
            filterable
            placeholder="请选择通知对象"
          >
            <el-option
              v-for="item in userOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input
            v-model="notifyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
            :disabled="true"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="notifyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmNotify" :loading="notifying">
            发送通知
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Plus,
  Bell
} from '@element-plus/icons-vue'
import axios from 'axios'

// 搜索表单
const searchForm = ref({
  keyword: '',
  warningType: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedRows = ref([])

// 入库表单相关
const stockInDialogVisible = ref(false)
const stockInFormRef = ref(null)
const submitting = ref(false)
const stockInForm = ref({
  id: null,
  name: '',
  stock: 0,
  warningStock: 0,
  amount: 1,
  remark: ''
})

// 通知表单相关
const notifyDialogVisible = ref(false)
const notifyFormRef = ref(null)
const notifying = ref(false)
const notifyForm = ref({
  notifyType: 'EMAIL',
  receivers: [],
  content: ''
})
const userOptions = ref([])

// 表单验证规则
const rules = {
  amount: [
    { required: true, message: '请输入入库数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '入库数量必须大于0', trigger: 'blur' }
  ]
}

const notifyRules = {
  notifyType: [
    { required: true, message: '请选择通知方式', trigger: 'change' }
  ],
  receivers: [
    { required: true, message: '请选择通知对象', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一个通知对象', trigger: 'change' }
  ]
}

// 获取预警商品列表
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.value.keyword,
      warningType: searchForm.value.warningType
    }
    
    const response = await axios.get('/api/stock/warning/page', { params })
    if (response.data.code === 200) {
      const { records, total: totalCount } = response.data.data
      tableData.value = records
      total.value = totalCount
    }
  } catch (error) {
    console.error('获取预警商品列表失败:', error)
    ElMessage.error('获取预警商品列表失败')
  } finally {
    loading.value = false
  }
}

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await axios.get('/api/users')
    if (response.data.code === 200) {
      userOptions.value = response.data.data.map(user => ({
        id: user.id,
        name: `${user.username}${user.nickname ? ` (${user.nickname})` : ''}`
      }))
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.warning('获取用户列表失败，可能影响通知功能')
  }
}

// 获取库存标签类型
const getStockTagType = (row) => {
  if (row.stock === 0) return 'danger'
  if (row.stock <= row.warningStock) return 'warning'
  return 'success'
}

// 生成通知内容
const generateNotifyContent = (products) => {
  const productList = products.map(p => 
    `${p.name}（${p.brand} ${p.model}）当前库存：${p.stock}，预警阈值：${p.warningStock}`
  ).join('\\n')
  
  return `以下商品库存不足，请及时处理：\\n${productList}`
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    warningType: ''
  }
  handleSearch()
}

// 刷新数据
const handleRefresh = () => {
  fetchList()
  ElMessage.success('数据已刷新')
}

// 导出数据
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 表格选择
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

// 处理入库操作
const handleStockIn = (row) => {
  stockInForm.value = {
    id: row.id,
    name: row.name,
    stock: row.stock,
    warningStock: row.warningStock,
    amount: 1,
    remark: ''
  }
  stockInDialogVisible.value = true
}

// 确认入库
const confirmStockIn = async () => {
  if (!stockInFormRef.value) return
  
  try {
    await stockInFormRef.value.validate()
    submitting.value = true
    
    const response = await axios.post('/api/stock/in', {
      productId: stockInForm.value.id,
      amount: stockInForm.value.amount,
      remark: stockInForm.value.remark
    })
    
    if (response.data.code === 200) {
      ElMessage.success('入库成功')
      stockInDialogVisible.value = false
      fetchList()
    }
  } catch (error) {
    if (error.name === 'ValidationError') {
      return
    }
    console.error('入库失败:', error)
    ElMessage.error('入库失败')
  } finally {
    submitting.value = false
  }
}

// 处理通知操作
const handleNotify = (row) => {
  notifyForm.value = {
    notifyType: 'EMAIL',
    receivers: [],
    content: generateNotifyContent([row])
  }
  notifyDialogVisible.value = true
}

// 批量通知
const handleBatchNotify = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择需要通知的商品')
    return
  }
  
  notifyForm.value = {
    notifyType: 'EMAIL',
    receivers: [],
    content: generateNotifyContent(selectedRows.value)
  }
  notifyDialogVisible.value = true
}

// 确认发送通知
const confirmNotify = async () => {
  if (!notifyFormRef.value) return
  
  try {
    await notifyFormRef.value.validate()
    notifying.value = true
    
    const response = await axios.post('/api/stock/warning/notify', {
      notifyType: notifyForm.value.notifyType,
      receivers: notifyForm.value.receivers,
      content: notifyForm.value.content
    })
    
    if (response.data.code === 200) {
      ElMessage.success('通知发送成功')
      notifyDialogVisible.value = false
    }
  } catch (error) {
    if (error.name === 'ValidationError') {
      return
    }
    console.error('发送通知失败:', error)
    ElMessage.error('发送通知失败')
  } finally {
    notifying.value = false
  }
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchList()
  fetchUsers()
})
</script>

<style scoped>
.stock-warning {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-operations {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-input-number) {
  width: 180px;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-tag) {
  min-width: 60px;
  text-align: center;
}
</style> 