<template>
  <div class="home">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总商品数</div>
              <div class="stat-value">{{ totalProducts }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order-icon">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总订单数</div>
              <div class="stat-value">{{ totalOrders }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon customer-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总客户数</div>
              <div class="stat-value">{{ totalCustomers }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">库存预警</div>
              <div class="stat-value">{{ warningCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="8">
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
          <div class="chart-container" ref="customerChart"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>订单趋势</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshOrderData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <div class="chart-container" ref="orderTrendChart"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存状态</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshStockData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <div class="chart-container" ref="stockChart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>新增客户趋势</span>
            </div>
          </template>
          <div class="chart-container" ref="customerTrendChart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户购买频率</span>
            </div>
          </template>
          <div class="chart-container" ref="customerFrequencyChart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-row :gutter="20" class="data-table-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="left-section">
                <span>数据明细</span>
                <el-radio-group v-model="activeTab" size="small" style="margin-left: 20px">
                  <el-radio-button value="stock">库存</el-radio-button>
                  <el-radio-button value="order">订单</el-radio-button>
                  <el-radio-button value="customer">客户</el-radio-button>
                </el-radio-group>
              </div>
              <div class="right-section">
                <el-button type="primary" size="small" @click="refreshAllData">
                  <el-icon><Refresh /></el-icon> 刷新数据
                </el-button>
              </div>
            </div>
          </template>
          
          <el-table
            v-if="activeTab === 'stock'"
            :data="stockData"
            stripe
            style="width: 100%"
            v-loading="loading.stock"
            :max-height="400"
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

          <el-table
            v-if="activeTab === 'order'"
            :data="orderData"
            stripe
            style="width: 100%"
            v-loading="loading.order"
            :max-height="400"
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

          <el-table
            v-if="activeTab === 'customer'"
            :data="customerData"
            stripe
            style="width: 100%"
            v-loading="loading.customer"
            :max-height="400"
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
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import axios from 'axios'
import { Refresh, Goods, List, User, Warning } from '@element-plus/icons-vue'

export default {
  name: 'Home',
  components: {
    Refresh,
    Goods,
    List,
    User,
    Warning
  },
  setup() {
    // 状态数据
    const totalProducts = ref(0)
    const totalOrders = ref(0)
    const totalCustomers = ref(0)
    const warningCount = ref(0)
    const activeTab = ref('stock')
    const stockData = ref([])
    const orderData = ref([])
    const customerData = ref([])
    const loading = ref({
      stock: false,
      order: false,
      customer: false
    })

    // 图表实例
    const customerChart = ref(null)
    const orderTrendChart = ref(null)
    const stockChart = ref(null)
    const customerTrendChart = ref(null)
    const customerFrequencyChart = ref(null)
    let customerChartInstance = null
    let orderTrendChartInstance = null
    let stockChartInstance = null
    let customerTrendChartInstance = null
    let customerFrequencyChartInstance = null

    // 定时刷新
    let refreshTimer = null
    const refreshInterval = 30000 // 30秒

    // 图表主题颜色
    const themeColors = [
      '#5470c6',
      '#91cc75',
      '#fac858',
      '#ee6666',
      '#73c0de',
      '#3ba272',
      '#fc8452',
      '#9a60b4'
    ]

    // 计算当前表格数据
    const currentTableData = computed(() => {
      switch (activeTab.value) {
        case 'stock':
          return stockData.value
        case 'order':
          return orderData.value
        case 'customer':
          return customerData.value
        default:
          return []
      }
    })

    // 获取列属性
    const getColumnProp = (base) => {
      switch (activeTab.value) {
        case 'stock':
          return base === 'type' ? 'category' : base === 'amount' ? 'avgPrice' : 'count'
        case 'order':
          return base === 'type' ? 'category' : base
        default:
          return base
      }
    }

    // 获取列标签
    const getColumnLabel = (base) => {
      switch (activeTab.value) {
        case 'stock':
          return base === 'type' ? '类别' : base === 'amount' ? '平均价格' : '数量'
        case 'order':
          return base === 'type' ? '类别' : base === 'amount' ? '金额' : '数量'
        case 'customer':
          return base === 'type' ? '客户类型' : base === 'amount' ? '消费金额' : '数量'
        default:
          return ''
      }
    }

    // 定义通用的图表样式
    const commonChartStyle = {
      title: {
        textStyle: {
          fontSize: 16,
          fontWeight: 'normal',
          color: '#303133'
        },
        top: 10,
        left: 'center'
      },
      tooltip: {
        backgroundColor: 'rgba(255,255,255,0.9)',
        borderColor: '#eee',
        borderWidth: 1,
        textStyle: {
          color: '#333'
        },
        padding: [10, 15],
        extraCssText: 'box-shadow: 0 0 10px rgba(0,0,0,0.1);'
      },
      legend: {
        type: 'scroll',
        bottom: 10,
        textStyle: {
          color: '#666',
          fontSize: 12
        },
        pageTextStyle: {
          color: '#666'
        },
        icon: 'circle'
      }
    }

    // 初始化图表方法
    const initChart = async (chartRef, chartInstance, initFunction) => {
      if (!chartRef.value) return null
      
      if (chartInstance) {
        chartInstance.dispose()
      }
      
      const instance = echarts.init(chartRef.value)
      initFunction(instance)
      return instance
    }

    // 刷新数据方法
    const refreshStockData = async () => {
      loading.value.stock = true
      try {
        const response = await axios.get('http://localhost:8081/api/spark/stock/analysis')
        if (response.data.code === 200) {
          const data = response.data.data
          stockData.value = data
          totalProducts.value = data.totalProducts || 0
          warningCount.value = data.warningCount || 0
          updateStockChart(data)
        }
      } catch (error) {
        console.error('获取库存分析数据失败:', error)
        ElMessage.error('获取库存分析数据失败')
      } finally {
        loading.value.stock = false
      }
    }

    const refreshOrderData = async () => {
      loading.value.order = true
      try {
        const response = await axios.get('http://localhost:8081/api/spark/order/analysis')
        if (response.data.code === 200) {
          const data = response.data.data
          orderData.value = data
          totalOrders.value = data.orderStatus?.reduce((sum, item) => sum + item.count, 0) || 0
          updateOrderChart(data)
        }
      } catch (error) {
        console.error('获取订单分析数据失败:', error)
        ElMessage.error('获取订单分析数据失败')
      } finally {
        loading.value.order = false
      }
    }

    const refreshCustomerData = async () => {
      loading.value.customer = true
      try {
        const response = await axios.get('http://localhost:8081/api/spark/customer/analysis')
        if (response.data.code === 200) {
          const data = response.data.data
          customerData.value = data
          totalCustomers.value = data.totalCustomers || 0
          updateCustomerChart(data)
        }
      } catch (error) {
        console.error('获取客户分析数据失败:', error)
        ElMessage.error('获取客户分析数据失败')
      } finally {
        loading.value.customer = false
      }
    }

    // 更新图表方法
    const updateStockChart = (data) => {
      if (stockChartInstance && data) {
        const stockStatus = data.stockStatus || []
        const chartData = stockStatus.map(item => ({
          name: item.status,
          value: item.count
        }))

        stockChartInstance.setOption({
          series: [{
            data: chartData
          }]
        })

        // 更新表格数据
        stockData.value = [
          // 预警商品信息
          ...(data.warningProducts || []).map(item => ({
            category: `预警商品：${item.name}`,
            count: item.stock,
            avgPrice: `预警值: ${item.warningStock}`
          })),
          // 出入库信息
          ...(data.stockChanges || []).map(item => ({
            category: item.type === 'IN' ? '入库记录' : '出库记录',
            count: item.count,
            avgPrice: `总量: ${item.totalAmount}`
          })),
          // 汇总信息
          {
            category: '总商品数',
            count: data.totalProducts || 0,
            avgPrice: '-'
          },
          {
            category: '预警商品数',
            count: data.warningCount || 0,
            avgPrice: '-'
          },
          {
            category: '预警比率',
            count: ((data.warningRate || 0) * 100).toFixed(2) + '%',
            avgPrice: '-'
          }
        ]
      }
    }

    const updateOrderChart = (data) => {
      if (orderTrendChartInstance && data) {
        const dailyTrend = data.dailyTrend || []
        dailyTrend.sort((a, b) => new Date(a.date) - new Date(b.date))
        
        const dates = dailyTrend.map(item => item.date.substring(5))
        const counts = dailyTrend.map(item => item.count)
        const amounts = dailyTrend.map(item => item.amount)

        orderTrendChartInstance.setOption({
          xAxis: {
            data: dates
          },
          series: [
            {
              name: '订单数',
              data: counts
            },
            {
              name: '订单金额',
              data: amounts
            }
          ]
        })

        // 更新表格数据
        orderData.value = [
          // 订单状态统计
          ...(data.orderStatus || []).map(item => ({
            category: `订单状态：${item.name}`,
            count: item.count,
            amount: `¥${(item.amount || 0).toFixed(2)}`
          })),
          // 支付方式统计
          ...(data.paymentMethods || []).map(item => ({
            category: `支付方式：${item.name}`,
            count: item.count,
            amount: '-'
          })),
          // 热销商品统计
          ...(data.hotProducts || []).map(item => ({
            category: `热销商品：${item.name}`,
            count: item.orderCount,
            amount: `¥${(item.amount || 0).toFixed(2)}`
          })),
          // 汇总信息
          {
            category: '总订单数',
            count: (data.orderStatus || []).reduce((sum, item) => sum + (item.count || 0), 0),
            amount: `¥${(data.orderStatus || []).reduce((sum, item) => sum + (item.amount || 0), 0).toFixed(2)}`
          }
        ]
      }
    }

    const updateCustomerChart = (data) => {
      if (!customerChartInstance || !customerTrendChartInstance || !customerFrequencyChartInstance || !data) {
        return
      }

      // 更新客户等级分布
      const customerLevels = data.customerLevels || []
      const chartData = customerLevels.map(item => ({
        name: item.level,
        value: item.count
      }))

      customerChartInstance.setOption({
        series: [{
          data: chartData
        }]
      })

      // 更新新增客户趋势
      if (data.newCustomers?.length > 0) {
        const dates = data.newCustomers.map(item => item.date.substring(5))
        const counts = data.newCustomers.map(item => item.count)

        customerTrendChartInstance.setOption({
          xAxis: {
            data: dates
          },
          series: [{
            data: counts
          }]
        })
      }

      // 更新购买频率分布
      if (data.purchaseFrequency) {
        const frequencyData = data.purchaseFrequency.map(item => ({
          name: item.range,
          value: item.count
        }))

        customerFrequencyChartInstance.setOption({
          series: [{
            data: frequencyData
          }]
        })
      }

      // 更新表格数据
      customerData.value = [
        // 客户等级分布
        ...(data.customerLevels || []).map(item => ({
          type: `客户等级：${item.level}`,
          count: item.count,
          amount: `¥${(item.avgSpent || 0).toFixed(2)}`
        })),
        
        // 客户总览
        {
          type: '总客户数',
          count: data.totalCustomers || 0,
          amount: '-'
        },

        // 新增客户趋势
        {
          type: '最近新增客户',
          count: (data.newCustomers || []).reduce((sum, item) => sum + (item.count || 0), 0),
          amount: '-'
        },

        // 购买频率分布
        ...(data.purchaseFrequency || []).map(item => ({
          type: `购买频率：${item.range}`,
          count: item.count,
          amount: '-'
        })),

        // 地域分布
        ...(data.regionDistribution || []).map(item => ({
          type: `地区：${item.region}`,
          count: item.customerCount,
          amount: `¥${(item.totalAmount || 0).toFixed(2)}`
        }))
      ]
    }

    // 刷新所有数据
    const refreshAllData = () => {
      refreshStockData()
      refreshOrderData()
      refreshCustomerData()
    }

    // 初始化各个图表
    onMounted(async () => {
      await nextTick()
      
      customerChartInstance = await initChart(customerChart, customerChartInstance, (instance) => {
        instance.setOption({
          ...commonChartStyle,
          title: {
            ...commonChartStyle.title,
            text: '客户等级分布'
          },
          tooltip: {
            ...commonChartStyle.tooltip,
            formatter: '{b}: {c}人 ({d}%)'
          },
          series: [{
            type: 'pie',
            radius: ['45%', '70%'],
            center: ['50%', '50%'],
            data: [],
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}\n{c}人 ({d}%)',
              color: '#666',
              fontSize: 12,
              lineHeight: 18
            },
            labelLine: {
              length: 10,
              length2: 15,
              smooth: true
            },
            itemStyle: {
              borderRadius: 6,
              borderWidth: 2,
              borderColor: '#fff'
            },
            emphasis: {
              label: {
                fontSize: 14,
                fontWeight: 'bold'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0,0,0,0.2)'
              }
            },
            animationType: 'scale',
            animationEasing: 'elasticOut'
          }]
        })
      })

      orderTrendChartInstance = await initChart(orderTrendChart, orderTrendChartInstance, (instance) => {
        instance.setOption({
          ...commonChartStyle,
          title: {
            ...commonChartStyle.title,
            text: '订单趋势'
          },
          tooltip: {
            ...commonChartStyle.tooltip,
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
              crossStyle: {
                color: '#999'
              },
              lineStyle: {
                type: 'dashed'
              }
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            top: '15%',
            containLabel: true
          },
          legend: {
            ...commonChartStyle.legend,
            data: ['订单数', '订单金额']
          },
          xAxis: {
            type: 'category',
            data: [],
            axisLine: {
              lineStyle: {
                color: '#ddd'
              }
            },
            axisTick: {
              alignWithLabel: true,
              lineStyle: {
                color: '#ddd'
              }
            },
            axisLabel: {
              color: '#666',
              rotate: 45,
              interval: 0
            }
          },
          yAxis: [
            {
              type: 'value',
              name: '订单数',
              nameTextStyle: {
                color: '#666'
              },
              axisLine: {
                show: true,
                lineStyle: {
                  color: '#ddd'
                }
              },
              axisTick: {
                lineStyle: {
                  color: '#ddd'
                }
              },
              axisLabel: {
                color: '#666'
              },
              splitLine: {
                lineStyle: {
                  type: 'dashed',
                  color: '#eee'
                }
              }
            },
            {
              type: 'value',
              name: '金额',
              position: 'right',
              nameTextStyle: {
                color: '#666'
              },
              axisLine: {
                show: true,
                lineStyle: {
                  color: '#ddd'
                }
              },
              axisTick: {
                lineStyle: {
                  color: '#ddd'
                }
              },
              axisLabel: {
                color: '#666',
                formatter: '¥{value}'
              },
              splitLine: {
                show: false
              }
            }
          ],
          series: [
            {
              name: '订单数',
              type: 'bar',
              data: [],
              barMaxWidth: 30,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#83bff6' },
                  { offset: 0.5, color: '#5470c6' },
                  { offset: 1, color: '#2f4999' }
                ]),
                borderRadius: [4, 4, 0, 0]
              },
              emphasis: {
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#91c7f8' },
                    { offset: 0.5, color: '#6684d8' },
                    { offset: 1, color: '#3d5aad' }
                  ])
                }
              }
            },
            {
              name: '订单金额',
              type: 'line',
              yAxisIndex: 1,
              data: [],
              smooth: true,
              symbol: 'emptyCircle',
              symbolSize: 8,
              lineStyle: {
                width: 3,
                color: '#91cc75'
              },
              itemStyle: {
                color: '#91cc75',
                borderWidth: 2
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(145, 204, 117, 0.3)' },
                  { offset: 1, color: 'rgba(145, 204, 117, 0.1)' }
                ])
              },
              emphasis: {
                scale: true,
                focus: 'series'
              }
            }
          ],
          animation: true,
          animationDuration: 1000,
          animationEasing: 'cubicInOut'
        })
      })

      stockChartInstance = await initChart(stockChart, stockChartInstance, (instance) => {
        instance.setOption({
          ...commonChartStyle,
          title: {
            ...commonChartStyle.title,
            text: '库存状态分布'
          },
          tooltip: {
            ...commonChartStyle.tooltip,
            formatter: '{b}: {c} ({d}%)'
          },
          series: [{
            type: 'pie',
            radius: ['45%', '70%'],
            center: ['50%', '50%'],
            data: [],
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}\n{c} ({d}%)',
              color: '#666',
              fontSize: 12,
              lineHeight: 18
            },
            labelLine: {
              length: 10,
              length2: 15,
              smooth: true
            },
            itemStyle: {
              borderRadius: 6,
              borderWidth: 2,
              borderColor: '#fff'
            },
            emphasis: {
              label: {
                fontSize: 14,
                fontWeight: 'bold'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0,0,0,0.2)'
              }
            },
            animationType: 'scale',
            animationEasing: 'elasticOut'
          }]
        })
      })

      customerTrendChartInstance = await initChart(customerTrendChart, customerTrendChartInstance, (instance) => {
        instance.setOption({
          ...commonChartStyle,
          title: {
            ...commonChartStyle.title,
            text: '新增客户趋势'
          },
          tooltip: {
            ...commonChartStyle.tooltip,
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            top: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: [],
            axisLine: {
              lineStyle: {
                color: '#ddd'
              }
            },
            axisTick: {
              alignWithLabel: true,
              lineStyle: {
                color: '#ddd'
              }
            },
            axisLabel: {
              color: '#666',
              rotate: 45,
              interval: 0
            }
          },
          yAxis: {
            type: 'value',
            name: '新增客户数',
            nameTextStyle: {
              color: '#666',
              padding: [0, 0, 0, 30]
            },
            axisLine: {
              show: true,
              lineStyle: {
                color: '#ddd'
              }
            },
            axisTick: {
              lineStyle: {
                color: '#ddd'
              }
            },
            axisLabel: {
              color: '#666'
            },
            splitLine: {
              lineStyle: {
                type: 'dashed',
                color: '#eee'
              }
            }
          },
          series: [{
            type: 'line',
            data: [],
            smooth: true,
            symbol: 'emptyCircle',
            symbolSize: 8,
            lineStyle: {
              width: 3,
              color: '#5470c6'
            },
            itemStyle: {
              color: '#5470c6',
              borderWidth: 2
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(84,112,198,0.3)' },
                { offset: 1, color: 'rgba(84,112,198,0.1)' }
              ])
            },
            emphasis: {
              scale: true,
              focus: 'series'
            },
            animationDuration: 1000,
            animationEasing: 'cubicInOut'
          }]
        })
      })

      customerFrequencyChartInstance = await initChart(customerFrequencyChart, customerFrequencyChartInstance, (instance) => {
        instance.setOption({
          ...commonChartStyle,
          title: {
            ...commonChartStyle.title,
            text: '客户购买频率分布'
          },
          tooltip: {
            ...commonChartStyle.tooltip,
            formatter: '{b}: {c}人 ({d}%)'
          },
          series: [{
            type: 'pie',
            radius: '55%',
            center: ['50%', '50%'],
            data: [],
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}\n{c}人\n{d}%',
              color: '#666',
              fontSize: 12,
              lineHeight: 18
            },
            labelLine: {
              length: 10,
              length2: 15,
              smooth: true
            },
            itemStyle: {
              borderRadius: 6,
              borderWidth: 2,
              borderColor: '#fff'
            },
            emphasis: {
              label: {
                fontSize: 14,
                fontWeight: 'bold'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0,0,0,0.2)'
              }
            },
            animationType: 'scale',
            animationEasing: 'elasticOut'
          }]
        })
      })

      // 加载初始数据
      refreshAllData()

      // 启动定时刷新
      refreshTimer = setInterval(refreshAllData, refreshInterval)

      // 监听窗口大小变化
      window.addEventListener('resize', handleResize)
    })

    // 处理窗口大小变化
    const handleResize = () => {
      customerChartInstance?.resize()
      orderTrendChartInstance?.resize()
      stockChartInstance?.resize()
      customerTrendChartInstance?.resize()
      customerFrequencyChartInstance?.resize()
    }

    onUnmounted(() => {
      // 清理定时器
      if (refreshTimer) {
        clearInterval(refreshTimer)
      }

      // 移除事件监听
      window.removeEventListener('resize', handleResize)

      // 销毁图表实例
      customerChartInstance?.dispose()
      orderTrendChartInstance?.dispose()
      stockChartInstance?.dispose()
      customerTrendChartInstance?.dispose()
      customerFrequencyChartInstance?.dispose()
    })

    return {
      // 数据
      totalProducts,
      totalOrders,
      totalCustomers,
      warningCount,
      activeTab,
      stockData,
      orderData,
      customerData,
      loading,
      currentTableData,
      // 图表引用
      customerChart,
      orderTrendChart,
      stockChart,
      customerTrendChart,
      customerFrequencyChart,
      // 方法
      refreshStockData,
      refreshOrderData,
      refreshCustomerData,
      refreshAllData,
      getColumnProp,
      getColumnLabel
    }
  }
}
</script>

<style scoped>
.home {
  padding: 20px;
  background-color: #f5f7fa;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon :deep(.el-icon) {
  font-size: 30px;
  color: white;
}

.order-icon {
  background-color: #67c23a;
}

.customer-icon {
  background-color: #e6a23c;
}

.warning-icon {
  background-color: #f56c6c;
}

.stat-info {
  flex-grow: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
}

.chart-container {
  height: 300px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
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

:deep(.el-card__header) {
  padding: 0;
  border-bottom: 1px solid #ebeef5;
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

.data-table-row {
  margin-top: 20px;
}

:deep(.el-table) {
  margin-top: 10px;
  border-radius: 4px;
}
</style>
