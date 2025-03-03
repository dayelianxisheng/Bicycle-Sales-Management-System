<template>
  <div class="generate-orders">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>订单数据生成</span>
        </div>
      </template>
      
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="生成数量" prop="count">
          <el-input-number v-model="form.count" :min="1" :max="100" />
        </el-form-item>
        
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择订单状态" clearable>
            <el-option label="全部状态" :value="-1" />
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="支付方式" prop="paymentMethod">
          <el-select v-model="form.paymentMethod" placeholder="请选择支付方式" clearable>
            <el-option label="随机" :value="-1" />
            <el-option label="微信支付" :value="1" />
            <el-option label="支付宝" :value="2" />
            <el-option label="银行卡" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="订单日期范围" prop="dateRange">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleGenerate" :loading="loading">
            生成数据
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 生成结果展示 -->
    <el-card class="box-card result-card" v-if="generatedData.length">
      <template #header>
        <div class="card-header">
          <span>生成结果</span>
          <el-button type="success" link @click="handleExport">
            导出数据
          </el-button>
        </div>
      </template>
      
      <el-table :data="generatedData" border stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="120">
          <template #default="{ row }">
            {{ getPaymentMethodText(row.paymentMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const formRef = ref(null)
const loading = ref(false)
const generatedData = ref([])

const form = reactive({
  count: 10,
  status: -1,
  paymentMethod: -1,
  dateRange: []
})

const rules = {
  count: [
    { required: true, message: '请输入生成数量', trigger: 'blur' },
    { type: 'number', min: 1, max: 100, message: '数量范围在 1 到 100 之间', trigger: 'blur' }
  ]
}

// 获取订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'primary',
    3: 'success',
    4: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    1: '微信支付',
    2: '支付宝',
    3: '银行卡'
  }
  return methodMap[method] || '未支付'
}

// 生成数据
const handleGenerate = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    loading.value = true
    const params = {
      count: form.count,
      status: form.status === -1 ? null : form.status,
      paymentMethod: form.paymentMethod === -1 ? null : form.paymentMethod,
      startDate: form.dateRange?.[0],
      endDate: form.dateRange?.[1]
    }
    
    const res = await axios.post('/api/generate/orders', params)
    if (res.data.code === 200) {
      generatedData.value = res.data.data
      ElMessage.success(`成功生成 ${form.count} 条订单数据`)
    } else {
      ElMessage.error(res.data.message || '生成数据失败')
    }
  } catch (error) {
    console.error('生成数据失败：', error)
    ElMessage.error('生成数据失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  generatedData.value = []
}

// 导出数据
const handleExport = () => {
  if (!generatedData.value.length) {
    ElMessage.warning('没有可导出的数据')
    return
  }

  // 创建CSV内容
  const headers = ['订单编号,客户姓名,订单金额,订单状态,支付方式,创建时间\n']
  const rows = generatedData.value.map(item => {
    return [
      item.orderNo,
      item.customerName,
      item.totalAmount.toFixed(2),
      getStatusText(item.status),
      getPaymentMethodText(item.paymentMethod),
      item.createdAt
    ].join(',')
  })
  
  const csvContent = headers.concat(rows).join('')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `订单数据_${new Date().getTime()}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
}
</script>

<style scoped>
.generate-orders {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-card {
  margin-top: 20px;
}
</style> 