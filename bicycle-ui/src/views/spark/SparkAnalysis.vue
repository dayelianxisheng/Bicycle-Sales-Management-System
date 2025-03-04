<template>
  <div class="spark-analysis">
    <el-row :gutter="20" class="chart-row">
      <!-- 库存分析 -->
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存分析</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshStockData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <div class="chart-container" ref="stockChart"></div>
          <div class="chart-container" ref="stockTrendChart"></div>
          <div class="chart-container" ref="stockWarningChart"></div>
        </el-card>
      </el-col>

      <!-- 订单分析 -->
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>订单分析</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshOrderData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <div class="chart-container" ref="orderChart"></div>
          <div class="chart-container" ref="orderTrendChart"></div>
          <div class="chart-container" ref="paymentChart"></div>
        </el-card>
      </el-col>

      <!-- 客户分析 -->
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>客户分析</span>
              <el-button-group>
                <el-button type="primary" size="small" @click="refreshCustomerData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </template>
          <div class="chart-container" ref="customerChart"></div>
          <div class="chart-container" ref="customerTrendChart"></div>
          <div class="chart-container" ref="customerFrequencyChart"></div>
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
            :data="stockData"
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
            :data="orderData"
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
            :data="customerData"
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
import * as echarts from 'echarts'
import axios from 'axios'
import { Refresh } from '@element-plus/icons-vue'

export default {
  name: 'SparkAnalysis',
  components: {
    Refresh
  },
  setup() {
    // 定时器
    let refreshTimer = null
    const refreshInterval = ref(30000) // 30秒刷新一次

    // 图表实例
    const stockChart = ref(null)
    const stockTrendChart = ref(null)
    const stockWarningChart = ref(null)
    const orderChart = ref(null)
    const orderTrendChart = ref(null)
    const paymentChart = ref(null)
    const customerChart = ref(null)
    const customerTrendChart = ref(null)
    const customerFrequencyChart = ref(null)
    let stockChartInstance = null
    let stockTrendChartInstance = null
    let stockWarningChartInstance = null
    let orderChartInstance = null
    let orderTrendChartInstance = null
    let paymentChartInstance = null
    let customerChartInstance = null
    let customerTrendChartInstance = null
    let customerFrequencyChartInstance = null

    // 数据状态
    const activeTab = ref('stock')
    const stockData = ref([])
    const orderData = ref([])
    const customerData = ref([])
    const loading = ref({
      stock: false,
      order: false,
      customer: false
    })

    // 定义通用的图表主题颜色
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

    // 定义通用的图表样式
    const commonChartStyle = {
      title: {
        textStyle: {
          fontSize: 16,
          fontWeight: 'normal'
        },
        top: 10
      },
      tooltip: {
        backgroundColor: 'rgba(255,255,255,0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333'
        },
        padding: [5, 10]
      },
      legend: {
        type: 'scroll',
        orient: 'horizontal',
        bottom: 10,
        textStyle: {
          fontSize: 12
        }
      }
    }

    // 初始化图表的通用方法
    const initChart = async (chartRef, chartInstance, initFunction) => {
      if (!chartRef.value) return null
      
      if (chartInstance) {
        chartInstance.dispose()
      }
      
      const instance = echarts.init(chartRef.value)
      initFunction(instance)
      return instance
    }

    // 初始化库存分析图表
    const initStockChart = (instance) => {
      const option = {
        ...commonChartStyle,
        title: {
          ...commonChartStyle.title,
          text: '库存状态分布'
        },
        tooltip: {
          ...commonChartStyle.tooltip,
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          ...commonChartStyle.legend
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {c} ({d}%)'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              }
            },
            data: [],
            color: themeColors
          }
        ]
      }
      instance.setOption(option)
      return instance
    }

    // 初始化订单分析图表
    const initOrderChart = (instance) => {
      const option = {
        title: {
          text: '订单状态分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '订单状态',
            type: 'pie',
            radius: '50%',
            data: [],
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
      instance.setOption(option)
      return instance
    }

    // 初始化客户分析图表
    const initCustomerChart = (instance) => {
      const option = {
        ...commonChartStyle,
        title: {
          ...commonChartStyle.title,
          text: '客户等级分布'
        },
        tooltip: {
          ...commonChartStyle.tooltip,
          formatter: '{b}: {c}人 ({d}%)'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {c}人\n{d}%'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              }
            },
            data: [],
            color: themeColors
          }
        ]
      }
      instance.setOption(option)
      return instance
    }

    // 初始化订单趋势图表
    const initOrderTrendChart = (instance) => {
      const option = {
        ...commonChartStyle,
        title: {
          ...commonChartStyle.title,
          text: '订单趋势分析'
        },
        tooltip: {
          ...commonChartStyle.tooltip,
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          formatter: function(params) {
            let result = params[0].axisValue + '<br/>';
            params.forEach(param => {
              if (param.seriesName === '订单金额') {
                result += param.seriesName + ': ¥' + param.value.toFixed(2) + '<br/>';
              } else {
                result += param.seriesName + ': ' + param.value + '<br/>';
              }
            });
            return result;
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
          data: ['订单数', '订单金额'],
          bottom: 0
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: {
            rotate: 45,
            interval: 0
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '订单数',
            minInterval: 1,
            axisLine: {
              show: true
            },
            axisLabel: {
              formatter: '{value}'
            }
          },
          {
            type: 'value',
            name: '订单金额',
            position: 'right',
            axisLine: {
              show: true
            },
            axisLabel: {
              formatter: '¥{value}'
            }
          }
        ],
        series: [
          {
            name: '订单数',
            type: 'bar',
            data: [],
            itemStyle: {
              color: themeColors[0]
            }
          },
          {
            name: '订单金额',
            type: 'line',
            yAxisIndex: 1,
            data: [],
            itemStyle: {
              color: themeColors[1]
            },
            lineStyle: {
              width: 3
            },
            symbol: 'circle',
            symbolSize: 8,
            smooth: true
          }
        ]
      }
      instance.setOption(option)
      return instance
    }

    // 初始化支付方式图表
    const initPaymentChart = (instance) => {
      const option = {
        title: {
          text: '支付方式分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '支付方式',
            type: 'pie',
            radius: '50%',
            data: [],
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
      instance.setOption(option)
      return instance
    }

    // 初始化库存趋势图表
    const initStockTrendChart = (instance) => {
      const option = {
        ...commonChartStyle,
        title: {
          ...commonChartStyle.title,
          text: '库存变化趋势'
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
          axisLabel: {
            rotate: 45,
            interval: 0
          },
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value',
          name: '数量',
          nameTextStyle: {
            padding: [0, 0, 0, 30]
          }
        },
        series: [
          {
            name: '入库数量',
            type: 'bar',
            stack: 'total',
            barWidth: '60%',
            data: [],
            itemStyle: {
              color: themeColors[0]
            }
          },
          {
            name: '出库数量',
            type: 'bar',
            stack: 'total',
            data: [],
            itemStyle: {
              color: themeColors[1]
            }
          }
        ]
      }
      instance.setOption(option)
      return instance
    }

    // 初始化库存预警图表
    const initStockWarningChart = (instance) => {
      const option = {
        ...commonChartStyle,
        title: {
          ...commonChartStyle.title,
          text: '库存预警分析'
        },
        tooltip: {
          ...commonChartStyle.tooltip,
          formatter: '{b}: {c}件'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {c}件'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              }
            },
            data: [],
            color: [themeColors[1], themeColors[3]]
          }
        ]
      }
      instance.setOption(option)
      return instance
    }

    // 初始化客户趋势图表
    const initCustomerTrendChart = (instance) => {
      const option = {
        ...commonChartStyle,
        title: {
          ...commonChartStyle.title,
          text: '新增客户趋势'
        },
        tooltip: {
          ...commonChartStyle.tooltip,
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
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
          axisLabel: {
            rotate: 45,
            interval: 0
          },
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value',
          name: '新增客户数',
          nameTextStyle: {
            padding: [0, 0, 0, 30]
          }
        },
        series: [
          {
            type: 'line',
            data: [],
            smooth: true,
            symbol: 'emptyCircle',
            symbolSize: 8,
            lineStyle: {
              width: 3,
              color: themeColors[0]
            },
            areaStyle: {
              opacity: 0.3,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: themeColors[0]
                },
                {
                  offset: 1,
                  color: '#fff'
                }
              ])
            }
          }
        ]
      }
      instance.setOption(option)
      return instance
    }

    // 初始化客户购买频率图表
    const initCustomerFrequencyChart = (instance) => {
      const option = {
        ...commonChartStyle,
        title: {
          ...commonChartStyle.title,
          text: '客户购买频率分布'
        },
        tooltip: {
          ...commonChartStyle.tooltip,
          formatter: '{b}: {c}人 ({d}%)'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}\n{c}人 ({d}%)'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              }
            },
            data: [],
            color: themeColors
          }
        ]
      }
      instance.setOption(option)
      return instance
    }

    // 刷新数据方法
    const refreshStockData = async () => {
      loading.value.stock = true
      try {
        const response = await axios.get('http://localhost:8081/api/spark/stock/analysis')
        console.log('Stock API Response:', response.data)
        if (response.data.code === 200) {
          stockData.value = response.data.data
          updateStockChart(response.data.data)
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
          orderData.value = response.data.data
          updateOrderChart(response.data.data)
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
          customerData.value = response.data.data
          updateCustomerChart(response.data.data)
        }
      } catch (error) {
        console.error('Customer API Error:', error)
        ElMessage.error('获取客户分析数据失败')
      } finally {
        loading.value.customer = false
      }
    }

    // 更新图表方法
    const updateStockChart = (data) => {
      console.log('Updating stock chart with data:', data)
      if (stockChartInstance && stockTrendChartInstance && stockWarningChartInstance && data) {
        // 确保数据存在且有效
        const stockStatus = data.stockStatus || []
        const warningProducts = data.warningProducts || []
        const stockChanges = data.stockChanges || []
        
        // 处理库存状态数据
        const chartData = stockStatus.map(item => ({
          name: item.status || '未知',
          value: item.count || 0
        }))

        // 更新库存状态饼图
        stockChartInstance.setOption({
          series: [{
            data: chartData
          }]
        })

        // 更新库存趋势图
        const stockChangesByDate = {}
        stockChanges.forEach(item => {
          const date = item.date || '未知'
          if (!stockChangesByDate[date]) {
            stockChangesByDate[date] = { IN: 0, OUT: 0 }
          }
          stockChangesByDate[date][item.type] = item.count || 0
        })

        const dates = Object.keys(stockChangesByDate)
        const inData = dates.map(date => stockChangesByDate[date].IN)
        const outData = dates.map(date => stockChangesByDate[date].OUT)

        stockTrendChartInstance.setOption({
          xAxis: {
            data: dates,
            axisLabel: {
              rotate: 45,
              interval: 0,
              formatter: function(value) {
                return value === '未知' ? value : value.substring(5) // 只显示月-日
              }
            }
          },
          series: [
            {
              name: '入库数量',
              data: inData
            },
            {
              name: '出库数量',
              data: outData
            }
          ]
        })

        // 更新库存预警图
        const warningData = [
          { name: '正常库存', value: data.totalProducts - data.warningCount },
          { name: '预警库存', value: data.warningCount }
        ]

        stockWarningChartInstance.setOption({
          series: [{
            data: warningData
          }]
        })

        // 更新表格数据
        stockData.value = [
          // 预警商品信息
          ...warningProducts.map(item => ({
            category: item.name,
            count: item.stock,
            avgPrice: `预警值: ${item.warningStock}`
          })),
          // 出入库信息
          ...stockChanges.map(item => ({
            category: item.type === 'IN' ? '入库记录' : '出库记录',
            count: item.count,
            avgPrice: `总量: ${item.totalAmount}`
          })),
          // 汇总信息
          {
            category: '总商品数',
            count: data.totalProducts,
            avgPrice: '-'
          },
          {
            category: '预警商品数',
            count: data.warningCount,
            avgPrice: '-'
          },
          {
            category: '预警比率',
            count: (data.warningRate * 100).toFixed(2) + '%',
            avgPrice: '-'
          }
        ]
      }
    }

    const updateOrderChart = (data) => {
      console.log('Updating order chart with data:', data)
      if (!orderChartInstance || !orderTrendChartInstance || !paymentChartInstance || !data) {
        return
      }

      // 处理订单状态分布
      const orderStatus = data.orderStatus || []
      const statusData = orderStatus.map(item => ({
        name: item.name,
        value: item.count
      }))

      // 处理支付方式分布
      const paymentMethods = data.paymentMethods || []
      const paymentData = paymentMethods.map(item => ({
        name: item.name,
        value: item.count
      }))

      // 处理每日订单趋势
      const dailyTrend = data.dailyTrend || []
      dailyTrend.sort((a, b) => new Date(a.date) - new Date(b.date)) // 按日期排序
      const dates = dailyTrend.map(item => item.date.substring(5)) // 只显示月-日
      const counts = dailyTrend.map(item => item.count)
      const amounts = dailyTrend.map(item => item.amount)

      // 使用nextTick确保DOM更新后再更新图表
      nextTick(() => {
        // 更新订单状态饼图
        orderChartInstance.setOption({
          series: [{
            data: statusData
          }]
        })

        // 更新订单趋势图
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

        // 更新支付方式饼图
        paymentChartInstance.setOption({
          series: [{
            data: paymentData
          }]
        })
      })

      // 更新表格数据
      orderData.value = [
        // 订单状态统计
        ...orderStatus.map(item => ({
          category: `订单状态：${item.name}`,
          count: item.count,
          amount: `¥${item.amount.toFixed(2)}`
        })),
        // 支付方式统计
        ...paymentMethods.map(item => ({
          category: `支付方式：${item.name}`,
          count: item.count,
          amount: '-'
        })),
        // 热销商品统计
        ...(data.hotProducts || []).map(item => ({
          category: `热销商品：${item.name}`,
          count: item.orderCount,
          amount: `¥${item.amount.toFixed(2)}`
        })),
        // 汇总信息
        {
          category: '总订单数',
          count: orderStatus.reduce((sum, item) => sum + item.count, 0),
          amount: `¥${orderStatus.reduce((sum, item) => sum + item.amount, 0).toFixed(2)}`
        }
      ]
    }

    const updateCustomerChart = (data) => {
      console.log('Updating customer chart with data:', data)
      if (customerChartInstance && customerTrendChartInstance && customerFrequencyChartInstance && data) {
        // 处理客户等级分布数据
        const customerLevels = data.customerLevels || []
        const chartData = customerLevels.map(item => ({
          name: item.level,
          value: item.count
        }))

        // 更新客户等级饼图
        customerChartInstance.setOption({
          series: [{
            data: chartData
          }]
        })

        // 更新新增客户趋势图
        if (data.newCustomers && data.newCustomers.length > 0) {
          const dates = data.newCustomers.map(item => item.date.substring(5)) // 只显示月-日
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

        // 更新客户购买频率图
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
          ...customerLevels.map(item => ({
            type: item.level,
            count: item.count,
            amount: `¥${item.avgSpent.toFixed(2)}`
          })),
          
          // 客户总览
          {
            type: '总客户数',
            count: data.totalCustomers,
            amount: '-'
          }
        ]

        // 添加新客户趋势数据
        if (data.newCustomers && data.newCustomers.length > 0) {
          customerData.value.push({
            type: '最近新增客户',
            count: data.newCustomers.reduce((sum, item) => sum + item.count, 0),
            amount: '-'
          })
        }

        // 添加购买频率分布数据
        if (data.purchaseFrequency) {
          customerData.value.push(
            ...data.purchaseFrequency.map(item => ({
              type: `购买频率: ${item.range}`,
              count: item.count,
              amount: '-'
            }))
          )
        }

        // 添加地域分布数据
        if (data.regionDistribution) {
          customerData.value.push(
            ...data.regionDistribution.map(item => ({
              type: `地区: ${item.region}`,
              count: item.customerCount,
              amount: `¥${item.totalAmount.toFixed(2)}`
            }))
          )
        }
      }
    }

    // 处理窗口大小变化
    const handleResize = () => {
      stockChartInstance?.resize()
      stockTrendChartInstance?.resize()
      stockWarningChartInstance?.resize()
      orderChartInstance?.resize()
      orderTrendChartInstance?.resize()
      paymentChartInstance?.resize()
      customerChartInstance?.resize()
      customerTrendChartInstance?.resize()
      customerFrequencyChartInstance?.resize()
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
      
      // 初始化图表
      stockChartInstance = await initChart(stockChart, stockChartInstance, initStockChart)
      stockTrendChartInstance = await initChart(stockTrendChart, stockTrendChartInstance, initStockTrendChart)
      stockWarningChartInstance = await initChart(stockWarningChart, stockWarningChartInstance, initStockWarningChart)
      orderChartInstance = await initChart(orderChart, orderChartInstance, initOrderChart)
      orderTrendChartInstance = await initChart(orderTrendChart, orderTrendChartInstance, initOrderTrendChart)
      paymentChartInstance = await initChart(paymentChart, paymentChartInstance, initPaymentChart)
      customerChartInstance = await initChart(customerChart, customerChartInstance, initCustomerChart)
      customerTrendChartInstance = await initChart(customerTrendChart, customerTrendChartInstance, initCustomerTrendChart)
      customerFrequencyChartInstance = await initChart(customerFrequencyChart, customerFrequencyChartInstance, initCustomerFrequencyChart)
      
      // 加载初始数据并启动定时刷新
      refreshAllData()
      startAutoRefresh()
      
      window.addEventListener('resize', handleResize)
    })

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      stockChartInstance?.dispose()
      stockTrendChartInstance?.dispose()
      stockWarningChartInstance?.dispose()
      orderChartInstance?.dispose()
      orderTrendChartInstance?.dispose()
      paymentChartInstance?.dispose()
      customerChartInstance?.dispose()
      customerTrendChartInstance?.dispose()
      customerFrequencyChartInstance?.dispose()
      // 清除定时器
      stopAutoRefresh()
    })

    return {
      stockChart,
      stockTrendChart,
      stockWarningChart,
      orderChart,
      orderTrendChart,
      paymentChart,
      customerChart,
      customerTrendChart,
      customerFrequencyChart,
      activeTab,
      stockData,
      orderData,
      customerData,
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

.chart-container {
  height: 300px;
  width: 100%;
  margin-top: 20px;
  padding: 10px;
}

.chart-container:first-child {
  margin-top: 0;
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