<template>
  <div class="home">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">
              <el-icon><ShoppingCart /></el-icon>
              <span>总订单数</span>
            </div>
            <div class="stat-value">{{ statistics.orderCount }}</div>
          </div>
          <div class="stat-footer">
            <span>较昨日</span>
            <span :class="statistics.orderIncrease >= 0 ? 'up' : 'down'">
              {{ statistics.orderIncrease >= 0 ? '+' : '' }}{{ statistics.orderIncrease }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">
              <el-icon><User /></el-icon>
              <span>客户总数</span>
            </div>
            <div class="stat-value">{{ statistics.customerCount }}</div>
          </div>
          <div class="stat-footer">
            <span>较昨日</span>
            <span :class="statistics.customerIncrease >= 0 ? 'up' : 'down'">
              {{ statistics.customerIncrease >= 0 ? '+' : '' }}{{ statistics.customerIncrease }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">
              <el-icon><Goods /></el-icon>
              <span>商品总数</span>
            </div>
            <div class="stat-value">{{ statistics.productCount }}</div>
          </div>
          <div class="stat-footer">
            <span>库存总量</span>
            <span>{{ statistics.totalStock }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <div class="stat-title">
              <el-icon><Warning /></el-icon>
              <span>库存预警</span>
            </div>
            <div class="stat-value">{{ statistics.warningCount }}</div>
          </div>
          <div class="stat-footer">
            <span>预警商品</span>
            <el-link type="danger" @click="handleWarningClick">查看详情</el-link>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="salesTimeRange" size="small">
                <el-radio-button value="week">本周</el-radio-button>
                <el-radio-button value="month">本月</el-radio-button>
                <el-radio-button value="year">全年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container" ref="salesChart"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存状态</span>
            </div>
          </template>
          <div class="chart-container" ref="stockChart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单 -->
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <span>最近订单</span>
          <el-button type="primary" link @click="handleMoreOrders">
            查看更多<el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <el-table :data="recentOrders" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="customerName" label="客户名称" width="120" />
        <el-table-column prop="amount" label="订单金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getOrderStatusType(scope.row.status)">
              {{ getOrderStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="scope">
            <el-button type="primary" link @click="handleOrderDetail(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  ShoppingCart,
  User,
  Goods,
  Warning,
  ArrowRight
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 统计数据
const statistics = ref({
  orderCount: 0,
  orderIncrease: 0,
  customerCount: 0,
  customerIncrease: 0,
  productCount: 0,
  totalStock: 0,
  warningCount: 0
})

// 图表相关
const salesTimeRange = ref('week')
const salesChart = ref(null)
const stockChart = ref(null)
let salesChartInstance = null
let stockChartInstance = null

// 最近订单
const recentOrders = ref([])

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await axios.get('/api/dashboard/statistics')
    if (response.data.code === 200) {
      statistics.value = response.data.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取最近订单
const fetchRecentOrders = async () => {
  try {
    const response = await axios.get('/api/dashboard/recent-orders')
    if (response.data.code === 200) {
      recentOrders.value = response.data.data
    }
  } catch (error) {
    console.error('获取最近订单失败:', error)
  }
}

// 初始化销售趋势图表
const initSalesChart = () => {
  if (!salesChart.value) return
  
  // 确保容器可见且有尺寸
  const container = salesChart.value
  if (!container.offsetHeight || !container.offsetWidth) {
    console.warn('销售趋势图表容器尺寸未就绪，将重试初始化')
    return false
  }

  try {
    if (salesChartInstance) {
      salesChartInstance.dispose()
    }
    salesChartInstance = echarts.init(container)
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['订单数', '销售额']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: [
        {
          type: 'value',
          name: '订单数'
        },
        {
          type: 'value',
          name: '销售额',
          axisLabel: {
            formatter: '{value} 元'
          }
        }
      ],
      series: [
        {
          name: '订单数',
          type: 'line',
          data: [10, 15, 20, 25, 22, 30, 35]
        },
        {
          name: '销售额',
          type: 'line',
          yAxisIndex: 1,
          data: [1000, 1500, 2000, 2500, 2200, 3000, 3500]
        }
      ]
    }
    salesChartInstance.setOption(option)
    return true
  } catch (error) {
    console.error('初始化销售趋势图表失败:', error)
    return false
  }
}

// 初始化库存状态图表
const initStockChart = () => {
  if (!stockChart.value) return
  
  // 确保容器可见且有尺寸
  const container = stockChart.value
  if (!container.offsetHeight || !container.offsetWidth) {
    console.warn('库存状态图表容器尺寸未就绪，将重试初始化')
    return false
  }

  try {
    if (stockChartInstance) {
      stockChartInstance.dispose()
    }
    stockChartInstance = echarts.init(container)
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '库存状态',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 60, name: '正常' },
            { value: 20, name: '偏高' },
            { value: 15, name: '偏低' },
            { value: 5, name: '预警' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    stockChartInstance.setOption(option)
    return true
  } catch (error) {
    console.error('初始化库存状态图表失败:', error)
    return false
  }
}

// 获取订单状态样式
const getOrderStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'PROCESSING': 'primary',
    'SHIPPED': 'info',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const textMap = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

// 处理查看更多订单
const handleMoreOrders = () => {
  router.push('/layout/order-list')
}

// 处理查看订单详情
const handleOrderDetail = (order) => {
  router.push(`/layout/order-list?id=${order.id}`)
}

// 处理查看库存预警
const handleWarningClick = () => {
  router.push('/layout/stock-warning')
}

// 页面加载时初始化数据
onMounted(async () => {
  await fetchStatistics()
  await fetchRecentOrders()
  
  // 使用递归方式确保图表正确初始化
  const initCharts = () => {
    const salesInitialized = initSalesChart()
    const stockInitialized = initStockChart()
    
    if (!salesInitialized || !stockInitialized) {
      setTimeout(initCharts, 100)
    } else {
      // 图表都初始化成功后获取数据
      fetchSalesTrendData(salesTimeRange.value)
    }
  }
  
  initCharts()
  window.addEventListener('resize', handleResize)
})

// 获取销售趋势数据
const fetchSalesTrendData = async (range) => {
  try {
    const response = await axios.get(`/api/dashboard/sales-trend?range=${range}`)
    if (response.data.code === 200 && salesChartInstance) {
      const data = response.data.data
      salesChartInstance.setOption({
        xAxis: {
          data: data.dates
        },
        series: [
          {
            data: data.orderCounts
          },
          {
            data: data.amounts
          }
        ]
      })
    }
  } catch (error) {
    console.error('获取销售趋势数据失败:', error)
  }
}

// 监听销售时间范围变化
watch(salesTimeRange, (newValue) => {
  fetchSalesTrendData(newValue)
})

// 处理窗口大小变化
const handleResize = () => {
  salesChartInstance?.resize()
  stockChartInstance?.resize()
}

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  salesChartInstance?.dispose()
  stockChartInstance?.dispose()
})
</script>

<style scoped>
.home {
  padding: 20px;
}

.stat-card {
  height: 120px;
}

.stat-header {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
}

.stat-title {
  display: flex;
  align-items: center;
  color: #666;
  margin-bottom: 10px;
}

.stat-title .el-icon {
  font-size: 20px;
  margin-right: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 14px;
}

.up {
  color: #67C23A;
}

.down {
  color: #F56C6C;
}

.chart-row {
  margin: 20px 0;
}

.chart-card {
  height: 400px;
}

.chart-container {
  height: calc(100% - 60px);  /* 减去头部的高度 */
  min-height: 300px;  /* 设置最小高度 */
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-card {
  margin-bottom: 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table) {
  margin: -10px 0;
}
</style> 