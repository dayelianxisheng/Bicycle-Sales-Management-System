<template>
  <div class="spark-analysis">
    <el-row :gutter="20" class="chart-row">
      <!-- 库存状态分布 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存状态分布</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshStockData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <stock-status-chart :data="stockData.stockStatus || []" />
        </el-card>
      </el-col>

      <!-- 库存变化趋势 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存变化趋势</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshStockData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <stock-trend-chart :data="stockData.stockChanges || []" />
        </el-card>
      </el-col>

      <!-- 库存预警分析 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存预警分析</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshStockData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <stock-warning-chart 
            :normal-stock="stockData.totalProducts - stockData.warningCount" 
            :warning-stock="stockData.warningCount" 
          />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 订单状态分布 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>订单状态分布</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshOrderData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <order-status-chart :data="orderData.orderStatus || []" />
        </el-card>
      </el-col>

      <!-- 订单趋势分析 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>订单趋势分析</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshOrderData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <order-trend-chart :data="orderData.dailyTrend || []" />
        </el-card>
      </el-col>

      <!-- 支付方式分布 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>支付方式分布</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshOrderData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <payment-method-chart :data="orderData.paymentMethods || []" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 客户等级分布 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户等级分布</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshCustomerData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <customer-level-chart :data="customerData.customerLevels || []" />
        </el-card>
      </el-col>

      <!-- 新增客户趋势 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>新增客户趋势</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshCustomerData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <customer-trend-chart :data="customerData.newCustomers || []" />
        </el-card>
      </el-col>

      <!-- 客户购买频率分布 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户购买频率分布</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshCustomerData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <customer-frequency-chart :data="customerData.purchaseFrequency || []" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 数据表格 -->
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="left-section">
                <span>详细数据</span>
                <el-radio-group v-model="activeTab" size="small" style="margin-left: 20px">
                  <el-radio-button value="stock">库存</el-radio-button>
                  <el-radio-button value="order">订单</el-radio-button>
                  <el-radio-button value="customer">客户</el-radio-button>
                </el-radio-group>
              </div>
              <div class="right-section">
                <el-button type="primary" size="small" @click="refreshAllData">
                  <el-icon><Refresh /></el-icon> 立即刷新
                </el-button>
              </div>
            </div>
          </template>
          
          <!-- 库存数据表格 -->
          <el-table
            v-if="activeTab === 'stock'"
            :data="stockTableData"
            stripe
            style="width: 100%"
            v-loading="loading.stock"
          >
            <el-table-column prop="category" label="类别">
              <template #default="scope">
                <span>{{ scope.row.category || '暂无数据' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量">
              <template #default="scope">
                <span>{{ scope.row.count || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="avgPrice" label="平均价格">
              <template #default="scope">
                <span>{{ scope.row.avgPrice || '-' }}</span>
              </template>
            </el-table-column>
          </el-table>

          <!-- 订单数据表格 -->
          <el-table
            v-if="activeTab === 'order'"
            :data="orderTableData"
            stripe
            style="width: 100%"
            v-loading="loading.order"
          >
            <el-table-column prop="category" label="类别">
              <template #default="scope">
                <span>{{ scope.row.category || '暂无数据' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量">
              <template #default="scope">
                <span>{{ scope.row.count || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额">
              <template #default="scope">
                <span>{{ scope.row.amount || '-' }}</span>
              </template>
            </el-table-column>
          </el-table>

          <!-- 客户数据表格 -->
          <el-table
            v-if="activeTab === 'customer'"
            :data="customerTableData"
            stripe
            style="width: 100%"
            v-loading="loading.customer"
          >
            <el-table-column prop="type" label="客户类型">
              <template #default="scope">
                <span>{{ scope.row.type || '暂无数据' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量">
              <template #default="scope">
                <span>{{ scope.row.count || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="消费金额">
              <template #default="scope">
                <span>{{ scope.row.amount || '-' }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Refresh } from '@element-plus/icons-vue'

// 导入图表组件
import StockStatusChart from '@/components/charts/StockStatusChart.vue'
import StockTrendChart from '@/components/charts/StockTrendChart.vue'
import StockWarningChart from '@/components/charts/StockWarningChart.vue'
import OrderStatusChart from '@/components/charts/OrderStatusChart.vue'
import OrderTrendChart from '@/components/charts/OrderTrendChart.vue'
import PaymentMethodChart from '@/components/charts/PaymentMethodChart.vue'
import CustomerLevelChart from '@/components/charts/CustomerLevelChart.vue'
import CustomerTrendChart from '@/components/charts/CustomerTrendChart.vue'
import CustomerFrequencyChart from '@/components/charts/CustomerFrequencyChart.vue'

export default {
  name: 'SparkAnalysis',
  components: {
    Refresh,
    StockStatusChart,
    StockTrendChart,
    StockWarningChart,
    OrderStatusChart,
    OrderTrendChart,
    PaymentMethodChart,
    CustomerLevelChart,
    CustomerTrendChart,
    CustomerFrequencyChart
  },
  setup() {
    // 定时器
    let refreshTimer = null
    const refreshInterval = ref(30000) // 30秒刷新一次

    // 数据状态
    const activeTab = ref('stock')
    const stockData = ref({
      stockStatus: [],
      stockChanges: [],
      totalProducts: 0,
      warningCount: 0,
      warningProducts: []
    })
    const orderData = ref({
      orderStatus: [],
      paymentMethods: [],
      dailyTrend: [],
      hotProducts: []
    })
    const customerData = ref({
      customerLevels: [],
      newCustomers: [],
      purchaseFrequency: [],
      regionDistribution: [],
      totalCustomers: 0
    })
    
    // 表格数据
    const stockTableData = ref([])
    const orderTableData = ref([])
    const customerTableData = ref([])
    
    const loading = ref({
      stock: false,
      order: false,
      customer: false
    })

    // 刷新数据方法
    const refreshStockData = async () => {
      loading.value.stock = true
      try {
        const response = await axios.get('http://localhost:8081/api/spark/stock/analysis')
        console.log('Stock API Response:', response.data)
        if (response.data.code === 200) {
          stockData.value = response.data.data || {}
          
          // 更新表格数据
          stockTableData.value = [
            // 预警商品信息
            ...(stockData.value.warningProducts || []).map(item => ({
              category: item.name,
              count: item.stock,
              avgPrice: `预警值: ${item.warningStock}`
            })),
            // 出入库信息
            ...(stockData.value.stockChanges || []).map(item => ({
              category: item.type === 'IN' ? '入库记录' : '出库记录',
              count: item.count,
              avgPrice: `总量: ${item.totalAmount}`
            })),
            // 汇总信息
            {
              category: '总商品数',
              count: stockData.value.totalProducts || 0,
              avgPrice: '-'
            },
            {
              category: '预警商品数',
              count: stockData.value.warningCount || 0,
              avgPrice: '-'
            },
            {
              category: '预警比率',
              count: ((stockData.value.warningRate || 0) * 100).toFixed(2) + '%',
              avgPrice: '-'
            }
          ]
        }
      } catch (error) {
        console.error('Stock API Error:', error)
        ElMessage.error('获取库存分析数据失败')
      } finally {
        loading.value.stock = false
      }
    }

    const refreshOrderData = async () => {
      loading.value.order = true
      try {
        const response = await axios.get('http://localhost:8081/api/spark/order/analysis')
        console.log('Order API Response:', response.data)
        if (response.data.code === 200) {
          orderData.value = response.data.data || {}
          
          // 更新表格数据
          orderTableData.value = [
            // 订单状态统计
            ...(orderData.value.orderStatus || []).map(item => ({
              category: `订单状态：${item.name}`,
              count: item.count,
              amount: `¥${item.amount.toFixed(2)}`
            })),
            // 支付方式统计
            ...(orderData.value.paymentMethods || []).map(item => ({
              category: `支付方式：${item.name}`,
              count: item.count,
              amount: '-'
            })),
            // 热销商品统计
            ...(orderData.value.hotProducts || []).map(item => ({
              category: `热销商品：${item.name}`,
              count: item.orderCount,
              amount: `¥${item.amount.toFixed(2)}`
            })),
            // 汇总信息
            {
              category: '总订单数',
              count: (orderData.value.orderStatus || []).reduce((sum, item) => sum + item.count, 0),
              amount: `¥${(orderData.value.orderStatus || []).reduce((sum, item) => sum + item.amount, 0).toFixed(2)}`
            }
          ]
        }
      } catch (error) {
        console.error('Order API Error:', error)
        ElMessage.error('获取订单分析数据失败')
      } finally {
        loading.value.order = false
      }
    }

    const refreshCustomerData = async () => {
      loading.value.customer = true
      try {
        const response = await axios.get('http://localhost:8081/api/spark/customer/analysis')
        console.log('Customer API Response:', response.data)
        if (response.data.code === 200) {
          customerData.value = response.data.data || {}
          
          // 更新表格数据
          customerTableData.value = [
            // 客户等级分布
            ...(customerData.value.customerLevels || []).map(item => ({
              type: item.level,
              count: item.count,
              amount: `¥${item.avgSpent.toFixed(2)}`
            })),
            
            // 客户总览
            {
              type: '总客户数',
              count: customerData.value.totalCustomers || 0,
              amount: '-'
            }
          ]

          // 添加新客户趋势数据
          if (customerData.value.newCustomers && customerData.value.newCustomers.length > 0) {
            customerTableData.value.push({
              type: '最近新增客户',
              count: customerData.value.newCustomers.reduce((sum, item) => sum + item.count, 0),
              amount: '-'
            })
          }

          // 添加购买频率分布数据
          if (customerData.value.purchaseFrequency) {
            customerTableData.value.push(
              ...(customerData.value.purchaseFrequency || []).map(item => ({
                type: `购买频率: ${item.range}`,
                count: item.count,
                amount: '-'
              }))
            )
          }

          // 添加地域分布数据
          if (customerData.value.regionDistribution) {
            customerTableData.value.push(
              ...(customerData.value.regionDistribution || []).map(item => ({
                type: `地区: ${item.region}`,
                count: item.customerCount,
                amount: `¥${item.totalAmount.toFixed(2)}`
              }))
            )
          }
        }
      } catch (error) {
        console.error('Customer API Error:', error)
        ElMessage.error('获取客户分析数据失败')
      } finally {
        loading.value.customer = false
      }
    }

    // 刷新所有数据
    const refreshAllData = () => {
      refreshStockData()
      refreshOrderData()
      refreshCustomerData()
    }

    // 启动定时刷新
    const startAutoRefresh = () => {
      if (refreshTimer) {
        clearInterval(refreshTimer)
      }
      refreshTimer = setInterval(() => {
        refreshAllData()
      }, refreshInterval.value)
    }

    // 停止定时刷新
    const stopAutoRefresh = () => {
      if (refreshTimer) {
        clearInterval(refreshTimer)
        refreshTimer = null
      }
    }

    onMounted(async () => {
      await nextTick()
      
      // 加载初始数据并启动定时刷新
      refreshAllData()
      startAutoRefresh()
    })

    onUnmounted(() => {
      // 清除定时器
      stopAutoRefresh()
    })

    return {
      activeTab,
      stockData,
      orderData,
      customerData,
      stockTableData,
      orderTableData,
      customerTableData,
      loading,
      refreshInterval,
      refreshStockData,
      refreshOrderData,
      refreshCustomerData,
      refreshAllData,
      startAutoRefresh,
      stopAutoRefresh
    }
  }
}
</script>

<style scoped>
.spark-analysis {
  padding: 20px;
  background-color: #f5f7fa;
}

.chart-card {
  height: auto;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.left-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.right-section {
  display: flex;
  align-items: center;
}

.chart-row {
  margin-top: 20px;
}

:deep(.el-card__header) {
  padding: 0;
}

:deep(.el-table) {
  margin-top: 10px;
  border-radius: 4px;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-button) {
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: scale(1.05);
}

:deep(.el-radio-button__inner) {
  transition: all 0.3s ease;
}

:deep(.el-radio-button__inner:hover) {
  transform: scale(1.05);
}
</style> 