<template>
  <div class="order-detail">
    <!-- 订单基本信息 -->
    <div class="detail-section">
      <el-descriptions title="订单信息" :column="3" border>
        <el-descriptions-item label="订单编号">{{ order?.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(order?.status)">{{ getStatusText(order?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ order?.totalAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ getPaymentMethodText(order?.paymentMethod) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ order?.paymentTime }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order?.createdAt }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 收货信息 -->
    <div class="detail-section">
      <el-descriptions title="收货信息" :column="2" border>
        <el-descriptions-item label="收货人">{{ order?.shippingName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order?.shippingPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ order?.shippingAddress }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 订单商品列表 -->
    <div class="detail-section">
      <div class="section-header">
        <h3>商品信息</h3>
        <el-button type="primary" @click="handleBack">返回列表</el-button>
      </div>
      <el-table :data="items" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column prop="productPrice" label="单价" width="120">
          <template #default="{ row }">¥{{ row.productPrice?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" align="center" />
        <el-table-column prop="subtotal" label="小计" width="120">
          <template #default="{ row }">¥{{ row.subtotal?.toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const orderId = route.params.id

const order = ref(null)
const items = ref([])

// 获取订单详情
const getOrderDetail = async () => {
  try {
    const res = await axios.get(`/api/orders/${orderId}`)
    if (res.data.code === 200) {
      order.value = res.data.data.order
      items.value = res.data.data.items
    } else {
      ElMessage.error(res.data.message || '获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败：', error)
    ElMessage.error('获取订单详情失败')
  }
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

// 返回列表
const handleBack = () => {
  router.back()
}

onMounted(() => {
  getOrderDetail()
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
}

.detail-section {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}
</style> 