<template>
  <div class="stock-overview">
    <el-row :gutter="20">
      <!-- 库存统计卡片 -->
      <el-col :span="6" v-for="stat in stockStats" :key="stat.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="24" :class="stat.class">
              <component :is="stat.icon" />
            </el-icon>
            <div class="stat-info">
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-value">{{ stat.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 库存预警表格 -->
    <el-card class="warning-table" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>库存预警</span>
          <el-button type="primary" @click="handleRefresh">刷新数据</el-button>
        </div>
      </template>
      <el-table :data="warningProducts" stripe style="width: 100%">
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="brand" label="品牌" />
        <el-table-column prop="model" label="型号" />
        <el-table-column prop="stock" label="当前库存">
          <template #default="scope">
            <el-tag :type="getStockTagType(scope.row)" effect="light">
              {{ scope.row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warning_stock" label="预警阈值" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleStockIn(scope.row)">
              入库
            </el-button>
            <el-button type="info" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 添加分页组件 -->
      <div class="pagination-container">
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
      <el-form :model="stockInForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ stockInForm.name }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ stockInForm.stock }}</span>
        </el-form-item>
        <el-form-item label="入库数量">
          <el-input-number v-model="stockInForm.amount" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockInDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmStockIn">确认入库</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Goods,
  Warning,
  ArrowDown,
  TrendCharts
} from '@element-plus/icons-vue'
import axios from 'axios'

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 库存统计数据
const stockStats = ref([
  { title: '商品总数', value: 0, icon: 'Goods', class: 'total-icon' },
  { title: '库存预警', value: 0, icon: 'Warning', class: 'warning-icon' },
  { title: '本月入库', value: 0, icon: 'ArrowDown', class: 'in-icon' },
  { title: '库存金额', value: '¥0', icon: 'TrendCharts', class: 'amount-icon' }
])

// 预警商品列表
const warningProducts = ref([])

// 入库表单相关
const stockInDialogVisible = ref(false)
const stockInForm = ref({
  id: null,
  name: '',
  stock: 0,
  amount: 1
})

// 获取库存统计数据
const fetchStockStats = async () => {
  try {
    const response = await axios.get('/api/stock/stats')
    if (response.data.code === 200) {
      const data = response.data.data
      stockStats.value[0].value = data.totalProducts
      stockStats.value[1].value = data.warningCount
      stockStats.value[2].value = data.monthlyIn
      stockStats.value[3].value = '¥' + data.totalAmount.toLocaleString()
    }
  } catch (error) {
    console.error('获取库存统计失败:', error)
    ElMessage.error('获取库存统计失败')
  }
}

// 获取预警商品列表
const fetchWarningProducts = async () => {
  try {
    const response = await axios.get('/api/stock/warning/page', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (response.data.code === 200) {
      const data = response.data.data
      warningProducts.value = data.records
      total.value = data.total
    }
  } catch (error) {
    console.error('获取预警商品列表失败:', error)
    ElMessage.error('获取预警商品列表失败')
  }
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchWarningProducts()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchWarningProducts()
}

// 刷新数据
const handleRefresh = () => {
  fetchStockStats()
  fetchWarningProducts()
  ElMessage.success('数据已刷新')
}

// 获取库存标签类型
const getStockTagType = (row) => {
  if (row.stock === 0) return 'danger'
  if (row.stock <= row.warning_stock) return 'warning'
  return 'success'
}

// 处理入库操作
const handleStockIn = (row) => {
  stockInForm.value = {
    id: row.id,
    name: row.name,
    stock: row.stock,
    amount: 1
  }
  stockInDialogVisible.value = true
}

// 确认入库
const confirmStockIn = async () => {
  try {
    const response = await axios.post('/api/stock/in', {
      productId: stockInForm.value.id,
      amount: stockInForm.value.amount
    })
    
    if (response.data.code === 200) {
      ElMessage.success('入库成功')
      stockInDialogVisible.value = false
      fetchWarningProducts()
      fetchStockStats()
    }
  } catch (error) {
    console.error('入库失败:', error)
    ElMessage.error('入库失败')
  }
}

// 处理编辑操作
const handleEdit = (row) => {
  // 跳转到商品编辑页面
  router.push(`/layout/product-management?id=${row.id}`)
}

// 页面加载时获取数据
onMounted(() => {
  fetchStockStats()
  fetchWarningProducts()
})
</script>

<style scoped>
.stock-overview {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-info {
  margin-left: 15px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 5px;
}

.total-icon {
  color: #409EFF;
}

.warning-icon {
  color: #E6A23C;
}

.in-icon {
  color: #67C23A;
}

.amount-icon {
  color: #F56C6C;
}

.warning-table {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 