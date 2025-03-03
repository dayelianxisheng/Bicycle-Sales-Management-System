<template>
  <div class="stock-out">
    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入商品名称/品牌/型号" clearable />
        </el-form-item>
        <el-form-item label="出库时间">
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
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增出库
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 出库记录表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" min-width="100" />
        <el-table-column prop="model" label="型号" min-width="100" />
        <el-table-column prop="amount" label="出库数量" width="100" align="center" />
        <el-table-column prop="operateTime" label="出库时间" width="180" align="center" />
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="reason" label="出库原因" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleDetail(scope.row)">
              查看详情
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

    <!-- 新增出库对话框 -->
    <el-dialog v-model="dialogVisible" title="新增出库" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="选择商品" prop="productId">
          <el-select
            v-model="form.productId"
            placeholder="请选择商品"
            filterable
            remote
            :remote-method="searchProducts"
            :loading="searching"
          >
            <el-option
              v-for="item in productOptions"
              :key="item.id"
              :label="item.name + ' - ' + item.brand + ' ' + item.model"
              :value="item.id"
            >
              <div class="product-option">
                <span>{{ item.name }} - {{ item.brand }} {{ item.model }}</span>
                <span class="stock-info">
                  库存: <el-tag size="small" :type="getStockTagType(item)">{{ item.stock }}</el-tag>
                </span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出库数量" prop="amount">
          <el-input-number v-model="form.amount" :min="1" :max="selectedProductStock" :step="1" />
        </el-form-item>
        <el-form-item label="出库原因" prop="reason">
          <el-select v-model="form.reason" placeholder="请选择出库原因">
            <el-option label="销售出库" value="SALE" />
            <el-option label="报废出库" value="SCRAP" />
            <el-option label="其他出库" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入出库备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="出库详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="商品名称">{{ detail.productName }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ detail.brand }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ detail.model }}</el-descriptions-item>
        <el-descriptions-item label="出库数量">{{ detail.amount }}</el-descriptions-item>
        <el-descriptions-item label="出库时间">{{ detail.operateTime }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detail.operator }}</el-descriptions-item>
        <el-descriptions-item label="出库原因">{{ getReasonText(detail.reason) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Refresh,
  Plus
} from '@element-plus/icons-vue'
import axios from 'axios'

// 搜索表单
const searchForm = ref({
  keyword: '',
  dateRange: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 新增出库表单
const dialogVisible = ref(false)
const formRef = ref(null)
const submitting = ref(false)
const form = ref({
  productId: null,
  amount: 1,
  reason: '',
  remark: ''
})

// 商品选择
const searching = ref(false)
const productOptions = ref([])

// 详情
const detailVisible = ref(false)
const detail = ref({})

// 计算选中商品的库存
const selectedProductStock = computed(() => {
  const product = productOptions.value.find(p => p.id === form.value.productId)
  return product ? product.stock : 0
})

// 表单验证规则
const rules = {
  productId: [
    { required: true, message: '请选择商品', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入出库数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '出库数量必须大于0', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value > selectedProductStock.value) {
          callback(new Error('出库数量不能大于当前库存'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  reason: [
    { required: true, message: '请选择出库原因', trigger: 'change' }
  ]
}

// 获取出库记录列表
const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.value.keyword,
      type: 'OUT',  // 添加类型参数，只获取出库记录
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
    console.error('获取出库记录失败:', error)
    ElMessage.error('获取出库记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索商品
const searchProducts = async (query) => {
  if (!query) return
  
  searching.value = true
  try {
    const response = await axios.get('/api/product/search', {
      params: { keyword: query }
    })
    if (response.data.code === 200) {
      productOptions.value = response.data.data
    }
  } catch (error) {
    console.error('搜索商品失败:', error)
  } finally {
    searching.value = false
  }
}

// 获取库存标签类型
const getStockTagType = (product) => {
  if (product.stock === 0) return 'danger'
  if (product.stock <= product.warningStock) return 'warning'
  return 'success'
}

// 获取出库原因文本
const getReasonText = (reason) => {
  const reasonMap = {
    'SALE': '销售出库',
    'SCRAP': '报废出库',
    'OTHER': '其他出库'
  }
  return reasonMap[reason] || reason
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
    dateRange: []
  }
  handleSearch()
}

// 新增出库
const handleAdd = () => {
  form.value = {
    productId: null,
    amount: 1,
    reason: '',
    remark: ''
  }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const response = await axios.post('/api/stock/out', {
      productId: form.value.productId,
      amount: form.value.amount,
      reason: form.value.reason,
      remark: form.value.remark
    })
    
    if (response.data.code === 200) {
      ElMessage.success('出库成功')
      dialogVisible.value = false
      fetchList()
    }
  } catch (error) {
    if (error.name === 'ValidationError') {
      return
    }
    console.error('出库失败:', error)
    ElMessage.error('出库失败')
  } finally {
    submitting.value = false
  }
}

// 查看详情
const handleDetail = (row) => {
  detail.value = row
  detailVisible.value = true
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
.stock-out {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
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

.product-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stock-info {
  color: #909399;
  font-size: 13px;
}

.stock-info .el-tag {
  margin-left: 5px;
}
</style> 