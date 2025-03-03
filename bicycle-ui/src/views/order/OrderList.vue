<template>
  <div class="order-list">
    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryForm" class="query-form">
      <el-form-item label="订单编号">
        <el-input v-model="queryForm.orderNo" placeholder="请输入订单编号" clearable />
      </el-form-item>
      <el-form-item label="客户姓名">
        <el-input v-model="queryForm.customerName" placeholder="请输入客户姓名" clearable />
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="queryForm.status" placeholder="请选择订单状态" clearable>
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
          <el-option label="已发货" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间">
        <el-date-picker
          v-model="queryForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 订单列表 -->
    <el-table :data="orderList" border stripe style="width: 100%">
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column prop="totalAmount" label="订单金额" width="120">
        <template #default="{ row }">
          ¥{{ row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="订单状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="shippingName" label="收货人" width="120" />
      <el-table-column prop="shippingPhone" label="联系电话" width="140" />
      <el-table-column prop="shippingAddress" label="收货地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="下单时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button v-if="row.status === 0" link type="primary" @click="handlePayment(row)">支付</el-button>
          <el-button v-if="row.status === 1" link type="primary" @click="handleShipment(row)">发货</el-button>
          <el-button v-if="row.status === 2" link type="primary" @click="handleComplete(row)">完成</el-button>
          <el-button v-if="row.status === 0" link type="danger" @click="handleCancel(row)">取消</el-button>
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 查询表单数据
const queryForm = reactive({
  orderNo: '',
  customerName: '',
  status: '',
  dateRange: []
})

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orderList = ref([])

// 获取订单列表数据
const getOrderList = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      orderNo: queryForm.orderNo,
      customerName: queryForm.customerName,
      status: queryForm.status,
      startDate: queryForm.dateRange[0],
      endDate: queryForm.dateRange[1]
    }
    const res = await axios.get('/api/orders', { params })
    if (res.data.code === 200) {
      orderList.value = res.data.data.list
      total.value = res.data.data.total
    } else {
      ElMessage.error(res.data.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表出错：', error)
    ElMessage.error('获取订单列表失败')
  }
}

// 查询按钮点击事件
const handleSearch = () => {
  currentPage.value = 1
  getOrderList()
}

// 重置按钮点击事件
const handleReset = () => {
  queryForm.orderNo = ''
  queryForm.customerName = ''
  queryForm.status = ''
  queryForm.dateRange = []
  handleSearch()
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

// 获取状态文本
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

// 获取状态标签类型
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

// 查看详情
const handleDetail = (row) => {
  router.push(`/layout/order-detail/${row.id}`)
}

// 支付订单
const handlePayment = async (row) => {
  try {
    const res = await axios.put(`/api/orders/${row.id}/payment`)
    if (res.data.code === 200) {
      ElMessage.success('支付状态更新成功')
      getOrderList()
    } else {
      ElMessage.error(res.data.message || '支付状态更新失败')
    }
  } catch (error) {
    console.error('支付状态更新出错：', error)
    ElMessage.error('支付状态更新失败')
  }
}

// 发货
const handleShipment = async (row) => {
  try {
    const res = await axios.put(`/api/orders/${row.id}/shipment`)
    if (res.data.code === 200) {
      ElMessage.success('发货状态更新成功')
      getOrderList()
    } else {
      ElMessage.error(res.data.message || '发货状态更新失败')
    }
  } catch (error) {
    console.error('发货状态更新出错：', error)
    ElMessage.error('发货状态更新失败')
  }
}

// 完成订单
const handleComplete = async (row) => {
  try {
    // 先获取订单详情
    const detailRes = await axios.get(`/api/orders/${row.id}`)
    if (detailRes.data.code !== 200) {
      throw new Error(detailRes.data.message || '获取订单详情失败')
    }

    const orderItems = detailRes.data.data.items
    
    // 更新订单状态为已完成
    const res = await axios.put(`/api/orders/${row.id}/complete`)
    if (res.data.code === 200) {
      // 遍历订单商品，调用库存出库接口
      for (const item of orderItems) {
        await axios.post('/api/stock/out', {
          productId: item.productId,
          amount: item.quantity,
          reason: 'SALE',
          remark: `订单${row.orderNo}出库`
        })
      }
      
      ElMessage.success('订单已完成')
      getOrderList()
    } else {
      ElMessage.error(res.data.message || '订单状态更新失败')
    }
  } catch (error) {
    console.error('订单状态更新出错：', error)
    ElMessage.error('订单状态更新失败')
  }
}

// 取消订单
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.put(`/api/orders/${row.id}/cancel`)
      if (res.data.code === 200) {
        ElMessage.success('订单已取消')
        getOrderList()
      } else {
        ElMessage.error(res.data.message || '订单取消失败')
      }
    } catch (error) {
      console.error('订单取消出错：', error)
      ElMessage.error('订单取消失败')
    }
  }).catch(() => {})
}

// 页面加载时获取数据
onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order-list {
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
</style> 