<template>
  <div class="product-management">
    <!-- 标题和操作按钮 -->
    <div class="header-actions">
      <h2 class="page-title">产品管理</h2>
      <div class="action-buttons">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入产品名称/品牌/型号"
          clearable
          style="width: 250px; margin-right: 8px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-button type="primary" @click="dialogVisible = true">添加产品</el-button>
        <el-button type="success" @click="handleExport">导出Excel</el-button>
      </div>
    </div>

    <!-- 产品列表表格 -->
    <el-table :data="products" style="width: 100%" border stripe v-loading="loading">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="name" label="产品名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="brand" label="品牌" width="100" show-overflow-tooltip />
      <el-table-column prop="model" label="型号" width="120" show-overflow-tooltip />
      <el-table-column prop="price" label="销售价格" width="100" align="right">
        <template #default="scope">
          ¥{{ scope.row.price ? scope.row.price.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column prop="costPrice" label="成本价格" width="100" align="right">
        <template #default="scope">
          ¥{{ scope.row.costPrice ? scope.row.costPrice.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" align="center" />
      <el-table-column prop="sales" label="销量" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" type="info" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteProduct(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30, 50]"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑产品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑产品' : '添加产品'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="产品类别" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择类别">
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="销售价格" prop="price">
          <el-input-number
            v-model="form.price"
            :precision="2"
            :step="0.1"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="成本价格" prop="costPrice">
          <el-input-number
            v-model="form.costPrice"
            :precision="2"
            :step="0.1"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="form.stock"
            :min="0"
            :precision="0"
          />
        </el-form-item>
        <el-form-item label="库存预警值" prop="warningStock">
          <el-input-number
            v-model="form.warningStock"
            :min="1"
            :precision="0"
          />
        </el-form-item>
        <el-form-item label="产品描述" prop="description">
          <el-input
            type="textarea"
            v-model="form.description"
            placeholder="请输入产品描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="规格参数" prop="specs">
          <el-input
            type="textarea"
            v-model="form.specs"
            placeholder="请输入规格参数（JSON格式）"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

// 数据定义
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const products = ref([])

// 产品类别（根据实际分类修改）
const categories = ['山地车', '公路车', '城市车', '折叠车', '电动车']

// 搜索表单简化
const searchForm = reactive({
  keyword: ''
})

// 添加/编辑表单
const form = reactive({
  id: null,
  name: '',
  brand: '',
  model: '',
  categoryId: null,
  price: 0,
  costPrice: 0,
  stock: 0,
  warningStock: 10,
  description: '',
  specs: '',
  images: '',
  status: 1,
  sales: 0
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  brand: [
    { required: true, message: '请输入品牌', trigger: 'blur' }
  ],
  model: [
    { required: true, message: '请输入型号', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择产品类别', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入销售价格', trigger: 'blur' }
  ],
  costPrice: [
    { required: true, message: '请输入成本价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入产品描述', trigger: 'blur' }
  ]
}

// 获取产品列表修改
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.keyword || undefined
    }
    
    const response = await axios.get('/api/products', { params })
    if (response.data.code === 200) {
      products.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取产品列表失败:', error)
    ElMessage.error('获取产品列表失败')
  } finally {
    loading.value = false
  }
}

// 导出Excel
const handleExport = async () => {
  try {
    const response = await axios.get('/api/products/export', {
      responseType: 'blob'
    })
    
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `产品列表_${new Date().toLocaleDateString()}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchProducts()
}

// 重置搜索简化
const resetSearch = () => {
  searchForm.keyword = ''
  handleSearch()
}

// 编辑产品
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除产品
const deleteProduct = async (id) => {
  try {
    await axios.delete(`/api/products/${id}`)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await axios.put(`/api/products/${form.id}`, form)
      ElMessage.success('修改成功')
    } else {
      await axios.post('/api/products', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchProducts()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchProducts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchProducts()
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 页面加载时获取数据
onMounted(() => {
  fetchProducts()
})

// 在script setup中添加
const router = useRouter()

// 查看详情
const handleDetail = (row) => {
  router.push(`/product-detail/${row.id}`)
}
</script>

<style scoped>
.product-management {
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: #fff;
  padding: 15px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 15px 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-form-item) {
  margin-bottom: 18px;
  margin-right: 18px;
}

:deep(.el-form-item__label) {
  font-weight: normal;
  color: #606266;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 15px 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-pagination) {
  padding: 0;
  margin: 0;
  white-space: nowrap;
  .el-pagination__total,
  .el-pagination__sizes,
  .el-pagination__jump {
    margin-right: 16px;
  }
  .el-pagination__jump {
    margin-left: 16px;
  }
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-table td) {
  padding: 8px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>