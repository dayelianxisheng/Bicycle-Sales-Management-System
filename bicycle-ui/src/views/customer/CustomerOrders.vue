<template>
  <div class="customer-orders">
    <!-- 客户信息 -->
    <div class="customer-info" v-if="customer">
      <el-descriptions title="客户信息" :column="3" border>
        <el-descriptions-item label="客户姓名">{{ customer.name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ customer.phone }}</el-descriptions-item>
        <el-descriptions-item label="电子邮箱">{{ customer.email }}</el-descriptions-item>
        <el-descriptions-item label="联系地址" :span="3">{{ customer.address }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <div class="table-header">
        <h2>订单记录</h2>
        <el-button type="primary" @click="handleBack">返回客户列表</el-button>
      </div>
      
      <el-table :data="orderList" border stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="120">
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
        <el-table-column prop="paymentTime" label="支付时间" width="180" />
        <el-table-column prop="shippingAddress" label="收货地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="shippingName" label="收货人" width="120" />
        <el-table-column prop="shippingPhone" label="收货电话" width="140" />
        <el-table-column prop="createdAt" label="下单时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleOrderDetail(row)">查看详情</el-button>
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
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const customerId = route.params.id

// 客户信息
const customer = ref(null)

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orderList = ref([])

// 获取客户信息
const getCustomerInfo = async () => {
  try {
    const res = await axios.get(`/api/customers/${customerId}`)
    if (res.data.code === 200) {
      customer.value = res.data.data
    }
  } catch (error) {
    console.error('获取客户信息失败：', error)
    ElMessage.error('获取客户信息失败')
  }
}

// 获取订单列表
const getOrderList = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      customerId: customerId
    }
    const res = await axios.get('/api/orders', { params })
    if (res.data.code === 200) {
      orderList.value = res.data.data.records
      total.value = res.data.data.total || res.data.data.records.length
    }
  } catch (error) {
    console.error('获取订单列表失败：', error)
    ElMessage.error('获取订单列表失败')
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

// 分页大小改变事件
const handleSizeChange = (val) => {
  pageSize.value = val
  getOrderList()
}

// 当前页改变事件
const handleCurrentChange = (val) => {
  currentPage.value = val
  getOrderList()
}

// 查看订单详情
const handleOrderDetail = (row) => {
  router.push(`/layout/order-detail/${row.id}`)
}

// 返回客户列表
const handleBack = () => {
  router.push('/layout/customer-list')
}

// 页面加载时获取数据
onMounted(() => {
  getCustomerInfo()
  getOrderList()
})
</script>

<style scoped>
.customer-orders {
  padding: 20px;
}

.customer-info {
  margin-bottom: 20px;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.order-list {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 