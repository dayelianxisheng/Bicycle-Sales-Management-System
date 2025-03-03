<template>
  <div class="stock-check">
    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入商品名称/品牌/型号" clearable />
        </el-form-item>
        <el-form-item label="盘点状态">
          <el-select v-model="searchForm.status" placeholder="请选择盘点状态" clearable>
            <el-option label="待盘点" value="PENDING" />
            <el-option label="盘点中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="盘点时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleStartCheck">
            <el-icon><Plus /></el-icon>开始盘点
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 盘点记录表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>盘点记录</span>
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

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="checkNo" label="盘点单号" min-width="150" show-overflow-tooltip />
        <el-table-column prop="checkTime" label="盘点时间" width="180" align="center" />
        <el-table-column prop="operator" label="盘点人" width="120" />
        <el-table-column prop="totalProducts" label="商品总数" width="100" align="center" />
        <el-table-column prop="diffCount" label="差异数" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.diffCount > 0 ? 'danger' : 'success'" effect="light">
              {{ scope.row.diffCount }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              @click="handleDetail(scope.row)"
            >
              查看详情
            </el-button>
            <el-button 
              v-if="scope.row.status === 'PENDING'"
              type="success" 
              link
              @click="handleContinue(scope.row)"
            >
              继续盘点
            </el-button>
            <el-button 
              v-if="scope.row.status === 'IN_PROGRESS'"
              type="warning" 
              link
              @click="handleComplete(scope.row)"
            >
              完成盘点
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

    <!-- 盘点详情对话框 -->
    <el-dialog v-model="detailVisible" title="盘点详情" width="80%">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="盘点单号">{{ detail.checkNo }}</el-descriptions-item>
        <el-descriptions-item label="盘点时间">{{ detail.checkTime }}</el-descriptions-item>
        <el-descriptions-item label="盘点人">{{ detail.operator }}</el-descriptions-item>
        <el-descriptions-item label="商品总数">{{ detail.totalProducts }}</el-descriptions-item>
        <el-descriptions-item label="差异数">{{ detail.diffCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detail.status)">
            {{ getStatusText(detail.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-table :data="detail.items" stripe style="width: 100%; margin-top: 20px">
        <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" min-width="100" />
        <el-table-column prop="model" label="型号" min-width="100" />
        <el-table-column prop="systemStock" label="系统库存" width="100" align="center" />
        <el-table-column prop="actualStock" label="实际库存" width="100" align="center" />
        <el-table-column prop="diffQuantity" label="差异数量" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.diffQuantity !== 0 ? 'danger' : 'success'" effect="light">
              {{ scope.row.diffQuantity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      </el-table>
    </el-dialog>

    <!-- 盘点操作对话框 -->
    <el-dialog 
      v-model="checkDialogVisible" 
      :title="checkDialogTitle"
      width="80%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form :model="checkForm" inline class="check-form">
        <el-form-item label="商品筛选">
          <el-input 
            v-model="checkForm.filterKeyword" 
            placeholder="输入商品名称/品牌/型号筛选"
            @input="filterCheckItems"
            clearable
          />
        </el-form-item>
      </el-form>

      <el-table 
        :data="filteredCheckItems" 
        stripe 
        style="width: 100%"
        v-loading="checkLoading"
      >
        <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" min-width="100" />
        <el-table-column prop="model" label="型号" min-width="100" />
        <el-table-column prop="systemStock" label="系统库存" width="100" align="center" />
        <el-table-column label="实际库存" width="150" align="center">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.actualStock"
              :min="0"
              :step="1"
              :step-strictly="true"
              @change="handleStockChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="diffQuantity" label="差异数量" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.diffQuantity !== 0 ? 'danger' : 'success'" effect="light">
              {{ scope.row.diffQuantity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="200">
          <template #default="scope">
            <el-input 
              v-model="scope.row.remark"
              placeholder="请输入备注说明"
              @change="handleRemarkChange(scope.row)"
            />
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCheckCancel">取消</el-button>
          <el-button type="primary" @click="handleCheckSave" :loading="saving">
            {{ checkDialogTitle === '开始盘点' ? '保存并开始' : '保存' }}
          </el-button>
          <el-button 
            v-if="checkDialogTitle !== '开始盘点'"
            type="success" 
            @click="handleCheckComplete"
            :loading="completing"
          >
            完成盘点
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
  Plus,
  Download
} from '@element-plus/icons-vue'
import axios from 'axios'

// 搜索表单
const searchForm = ref({
  keyword: '',
  status: '',
  dateRange: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 详情对话框
const detailVisible = ref(false)
const detail = ref({})

// 盘点对话框
const checkDialogVisible = ref(false)
const checkDialogTitle = ref('开始盘点')
const checkLoading = ref(false)
const saving = ref(false)
const completing = ref(false)

const checkForm = ref({
  filterKeyword: '',
  checkId: null,
  items: [],
  modifiedItems: new Set() // 用于跟踪修改过的商品ID
})

const filteredCheckItems = computed(() => {
  const items = checkForm.value.items
  const keyword = checkForm.value.filterKeyword.toLowerCase()
  
  if (!keyword) {
    return items
  }
  
  return items.filter(item => {
    const nameMatch = item.productName?.toLowerCase().includes(keyword)
    const brandMatch = item.brand?.toLowerCase().includes(keyword)
    const modelMatch = item.model?.toLowerCase().includes(keyword)
    return nameMatch || brandMatch || modelMatch
  })
})

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'IN_PROGRESS': 'primary',
    'COMPLETED': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待盘点',
    'IN_PROGRESS': '盘点中',
    'COMPLETED': '已完成'
  }
  return textMap[status] || status
}

// 获取盘点记录列表
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.value.keyword,
      status: searchForm.value.status,
      startDate: searchForm.value.dateRange?.[0],
      endDate: searchForm.value.dateRange?.[1]
    }
    
    const response = await axios.get('/api/stock/records', { params })
    if (response.data.code === 200) {
      const { records, total: totalCount } = response.data.data
      tableData.value = records
      total.value = totalCount
    }
  } catch (error) {
    console.error('获取盘点记录失败:', error)
    ElMessage.error('获取盘点记录失败')
  } finally {
    loading.value = false
  }
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
    status: '',
    dateRange: []
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

// 查看详情
const handleDetail = async (row) => {
  try {
    const response = await axios.get('/api/stock/records/' + row.id)
    if (response.data.code === 200) {
      detail.value = response.data.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取盘点详情失败:', error)
    ElMessage.error('获取盘点详情失败')
  }
}

// 开始新的盘点
const handleStartCheck = async () => {
  try {
    checkLoading.value = true
    checkDialogTitle.value = '开始盘点'
    checkDialogVisible.value = true
    
    // 重置表单数据
    checkForm.value = {
      filterKeyword: '',
      checkId: null,
      items: [],
      modifiedItems: new Set()
    }
    
    // 调用准备盘点数据接口
    const response = await axios.get('/api/stock/prepare')
    if (response.data.code === 200) {
      const prepareData = response.data.data
      checkForm.value.items = prepareData.products.map(product => ({
        productId: product.id,
        productName: product.name,
        brand: product.brand,
        model: product.model,
        systemStock: product.stock,
        actualStock: product.stock,
        diffQuantity: 0,
        remark: ''
      }))
    } else {
      throw new Error(response.data.message || '准备盘点数据失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '准备盘点数据失败')
    checkDialogVisible.value = false
  } finally {
    checkLoading.value = false
  }
}

// 继续盘点
const handleContinue = async (row) => {
  try {
    checkLoading.value = true
    checkDialogTitle.value = '继续盘点'
    
    // 重置表单数据
    checkForm.value = {
      filterKeyword: '',
      checkId: row.id,
      items: [],
      modifiedItems: new Set()
    }
    
    const response = await axios.get(`/api/stock/records/${row.id}/items`)
    if (response.data.code === 200) {
      checkForm.value.items = response.data.data
      checkDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取盘点数据失败:', error)
    ElMessage.error('获取盘点数据失败')
  } finally {
    checkLoading.value = false
  }
}

// 处理库存变化
const handleStockChange = (row) => {
  row.diffQuantity = row.actualStock - row.systemStock
  checkForm.value.modifiedItems.add(row.productId) // 记录修改过的商品ID
}

// 处理备注变化
const handleRemarkChange = (row) => {
  checkForm.value.modifiedItems.add(row.productId) // 记录修改过的商品ID
}

// 过滤盘点商品
const filterCheckItems = () => {
  // 通过计算属性 filteredCheckItems 自动处理
}

// 取消盘点
const handleCheckCancel = () => {
  ElMessageBox.confirm('确定要取消盘点吗？未保存的数据将丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    checkDialogVisible.value = false
  }).catch(() => {})
}

// 保存盘点数据
const handleCheckSave = async () => {
  try {
    saving.value = true
    if (!checkForm.value.checkId) {
      // 创建新的盘点单
      const params = new URLSearchParams()
      params.append('operator', 'admin') // 这里应该使用实际的操作员信息
      
      const response = await axios.post('/api/stock/records', params)
      
      if (response.data.code === 200) {
        checkForm.value.checkId = response.data.data.id
      } else {
        throw new Error(response.data.message || '创建盘点单失败')
      }
    }

    // 只保存修改过的盘点明细
    const modifiedItems = checkForm.value.items.filter(item => 
      checkForm.value.modifiedItems.has(item.productId)
    )

    for (const item of modifiedItems) {
      const params = new URLSearchParams()
      params.append('productId', item.productId)
      params.append('actualStock', item.actualStock)
      if (item.remark) {
        params.append('remark', item.remark)
      }
      
      await axios.put(`/api/stock/records/${checkForm.value.checkId}/items`, params)
    }

    ElMessage.success('保存成功')
    await fetchList()
    checkDialogVisible.value = false
    // 清空修改记录
    checkForm.value.modifiedItems.clear()
  } catch (error) {
    console.error('保存失败:', error.response?.data || error.message)
    ElMessage.error(error.response?.data?.message || error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 完成盘点
const handleCheckComplete = async () => {
  try {
    completing.value = true
    await axios.post(`/api/stock/records/${checkForm.value.checkId}/complete`)
    ElMessage.success('盘点完成')
    checkDialogVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error.message || '完成盘点失败')
  } finally {
    completing.value = false
  }
}

// 直接完成盘点
const handleComplete = (row) => {
  ElMessageBox.confirm(
    '确定要完成此次盘点吗？完成后数据将不能修改。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await axios.post('/api/stock/records/complete/' + row.id)
      if (response.data.code === 200) {
        ElMessage.success('盘点完成')
        fetchList()
      }
    } catch (error) {
      console.error('完成盘点失败:', error)
      ElMessage.error('完成盘点失败')
    }
  }).catch(() => {})
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
})
</script>

<style scoped>
.stock-check {
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

.check-form {
  margin-bottom: 20px;
}

:deep(.el-input-number) {
  width: 120px;
}

:deep(.el-tag) {
  min-width: 60px;
  text-align: center;
}

:deep(.el-descriptions) {
  margin-bottom: 20px;
}
</style> 