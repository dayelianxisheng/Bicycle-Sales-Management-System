<!--<template>-->
<!--  <div class="generate-products">-->
<!--    <el-card class="box-card">-->
<!--      <template #header>-->
<!--        <div class="card-header">-->
<!--          <span>商品数据生成</span>-->
<!--        </div>-->
<!--      </template>-->
<!--      -->
<!--      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">-->
<!--        <el-form-item label="生成数量" prop="count">-->
<!--          <el-input-number v-model="form.count" :min="1" :max="100" />-->
<!--        </el-form-item>-->

<!--        <el-form-item label="价格范围" prop="priceRange">-->
<!--          <el-input-number-->
<!--            v-model="form.minPrice"-->
<!--            :min="0"-->
<!--            :max="form.maxPrice"-->
<!--            :precision="2"-->
<!--            style="width: 180px"-->
<!--            placeholder="最低价格"-->
<!--          />-->
<!--          <span class="separator">-</span>-->
<!--          <el-input-number-->
<!--            v-model="form.maxPrice"-->
<!--            :min="form.minPrice"-->
<!--            :precision="2"-->
<!--            style="width: 180px"-->
<!--            placeholder="最高价格"-->
<!--          />-->
<!--        </el-form-item>-->

<!--        <el-form-item label="品牌选择" prop="brands">-->
<!--          <el-select-->
<!--            v-model="form.brands"-->
<!--            multiple-->
<!--            collapse-tags-->
<!--            collapse-tags-tooltip-->
<!--            placeholder="请选择品牌"-->
<!--          >-->
<!--            <el-option-->
<!--              v-for="brand in brandOptions"-->
<!--              :key="brand.value"-->
<!--              :label="brand.label"-->
<!--              :value="brand.value"-->
<!--            />-->
<!--          </el-select>-->
<!--        </el-form-item>-->

<!--        <el-form-item>-->
<!--          <el-button type="primary" @click="handleGenerate" :loading="loading">-->
<!--            生成数据-->
<!--          </el-button>-->
<!--          <el-button @click="handleReset">重置</el-button>-->
<!--        </el-form-item>-->
<!--      </el-form>-->
<!--    </el-card>-->

<!--    &lt;!&ndash; 生成结果展示 &ndash;&gt;-->
<!--    <el-card class="box-card result-card" v-if="generatedData.length">-->
<!--      <template #header>-->
<!--        <div class="card-header">-->
<!--          <span>生成结果</span>-->
<!--          <el-button type="success" link @click="handleExport">-->
<!--            导出数据-->
<!--          </el-button>-->
<!--        </div>-->
<!--      </template>-->
<!--      -->
<!--      <el-table :data="generatedData" border stripe style="width: 100%">-->
<!--        <el-table-column type="index" label="序号" width="60" align="center" />-->
<!--        <el-table-column prop="name" label="商品名称" min-width="180" show-overflow-tooltip />-->
<!--        <el-table-column prop="brand" label="品牌" width="120" />-->
<!--        <el-table-column prop="price" label="价格" width="120">-->
<!--          <template #default="{ row }">-->
<!--            ¥{{ row.price?.toFixed(2) }}-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column prop="description" label="商品描述" min-width="200" show-overflow-tooltip />-->
<!--        <el-table-column prop="createdAt" label="创建时间" width="180" />-->
<!--      </el-table>-->
<!--    </el-card>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref, reactive } from 'vue'-->
<!--import { ElMessage } from 'element-plus'-->
<!--import axios from 'axios'-->

<!--const formRef = ref(null)-->
<!--const loading = ref(false)-->
<!--const generatedData = ref([])-->

<!--const brandOptions = [-->
<!--  { value: 'Giant', label: 'Giant' },-->
<!--  { value: 'Specialized', label: 'Specialized' },-->
<!--  { value: 'Trek', label: 'Trek' },-->
<!--  { value: 'Cannondale', label: 'Cannondale' },-->
<!--  { value: 'Scott', label: 'Scott' }-->
<!--]-->

<!--const form = reactive({-->
<!--  count: 10,-->
<!--  minPrice: 1000,-->
<!--  maxPrice: 10000,-->
<!--  brands: []-->
<!--})-->

<!--const rules = {-->
<!--  count: [-->
<!--    { required: true, message: '请输入生成数量', trigger: 'blur' },-->
<!--    { type: 'number', min: 1, max: 100, message: '数量范围在 1 到 100 之间', trigger: 'blur' }-->
<!--  ],-->
<!--  brands: [-->
<!--    { type: 'array', message: '请至少选择一个品牌', trigger: 'change' }-->
<!--  ]-->
<!--}-->

<!--// 生成数据-->
<!--const handleGenerate = async () => {-->
<!--  if (!formRef.value) return-->
<!--  -->
<!--  try {-->
<!--    await formRef.value.validate()-->
<!--    -->
<!--    loading.value = true-->
<!--    const params = {-->
<!--      count: form.count,-->
<!--      minPrice: form.minPrice,-->
<!--      maxPrice: form.maxPrice,-->
<!--      brands: form.brands-->
<!--    }-->
<!--    -->
<!--    const res = await axios.post('/api/generate/products', params)-->
<!--    if (res.data.code === 200) {-->
<!--      generatedData.value = res.data.data-->
<!--      ElMessage.success(`成功生成 ${form.count} 条商品数据`)-->
<!--    } else {-->
<!--      ElMessage.error(res.data.message || '生成数据失败')-->
<!--    }-->
<!--  } catch (error) {-->
<!--    console.error('生成数据失败：', error)-->
<!--    ElMessage.error('生成数据失败')-->
<!--  } finally {-->
<!--    loading.value = false-->
<!--  }-->
<!--}-->

<!--// 重置表单-->
<!--const handleReset = () => {-->
<!--  if (formRef.value) {-->
<!--    formRef.value.resetFields()-->
<!--  }-->
<!--  generatedData.value = []-->
<!--}-->

<!--// 导出数据-->
<!--const handleExport = () => {-->
<!--  if (!generatedData.value.length) {-->
<!--    ElMessage.warning('没有可导出的数据')-->
<!--    return-->
<!--  }-->

<!--  // 创建CSV内容-->
<!--  const headers = ['商品名称,品牌,价格,商品描述,创建时间\n']-->
<!--  const rows = generatedData.value.map(item => {-->
<!--    return [-->
<!--      item.name,-->
<!--      item.brand,-->
<!--      item.price.toFixed(2),-->
<!--      item.description,-->
<!--      item.createdAt-->
<!--    ].join(',')-->
<!--  })-->
<!--  -->
<!--  const csvContent = headers.concat(rows).join('')-->
<!--  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })-->
<!--  const link = document.createElement('a')-->
<!--  link.href = URL.createObjectURL(blob)-->
<!--  link.download = `商品数据_${new Date().getTime()}.csv`-->
<!--  link.click()-->
<!--  URL.revokeObjectURL(link.href)-->
<!--}-->
<!--</script>-->

<!--<style scoped>-->
<!--.generate-products {-->
<!--  padding: 20px;-->
<!--}-->

<!--.box-card {-->
<!--  margin-bottom: 20px;-->
<!--}-->

<!--.card-header {-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  align-items: center;-->
<!--}-->

<!--.result-card {-->
<!--  margin-top: 20px;-->
<!--}-->

<!--.separator {-->
<!--  margin: 0 12px;-->
<!--  color: #909399;-->
<!--}-->
<!--</style>-->