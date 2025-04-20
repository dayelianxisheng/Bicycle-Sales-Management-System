<template>
  <div class="order-analysis">
    <el-card class="analysis-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span>订单分析</span>
            <el-radio-group v-model="timeRange" class="time-range" @change="handleTimeRangeChange">
              <el-radio-button :value="'all'">全部</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="data-card">
            <div class="data-title">总订单数</div>
            <div class="data-value">{{ totalOrders }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-title">总销售额</div>
            <div class="data-value">¥{{ totalAmount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-title">平均订单金额</div>
            <div class="data-value">¥{{ avgOrderAmount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-title">完成率</div>
            <div class="data-value">{{ completionRate }}%</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <div ref="orderTrendChart" style="height: 400px;"></div>
        </el-col>
        <el-col :span="12">
          <div ref="orderStatusChart" style="height: 400px;"></div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <div ref="paymentMethodChart" style="height: 400px;"></div>
        </el-col>
        <el-col :span="12">
          <div ref="salesChart" style="height: 400px;"></div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'OrderAnalysis',
  setup() {
    const timeRange = ref('all')
    const allOrders = ref([]) // 存储所有订单数据
    const totalOrders = ref(0)
    const totalAmount = ref(0)
    const avgOrderAmount = ref(0)
    const completionRate = ref(0)

    let charts = {
      orderTrend: null,
      orderStatus: null,
      paymentMethod: null,
      sales: null
    }

    const orderTrendChart = ref(null)
    const orderStatusChart = ref(null)
    const paymentMethodChart = ref(null)
    const salesChart = ref(null)

    const initCharts = () => {
      charts.orderTrend = echarts.init(orderTrendChart.value)
      charts.orderStatus = echarts.init(orderStatusChart.value)
      charts.paymentMethod = echarts.init(paymentMethodChart.value)
      charts.sales = echarts.init(salesChart.value)
      window.addEventListener('resize', handleResize)
    }

    const handleResize = () => {
      Object.values(charts).forEach(chart => {
        chart && chart.resize()
      })
    }

    // 获取指定时间范围内的订单
    const getOrdersInRange = () => {
      const now = new Date() // 使用当前时间
      let startDate = null
      let endDate = null

      if (timeRange.value === 'month') {
        startDate = new Date(now.getFullYear(), now.getMonth(), 1)
        endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0)
      } else if (timeRange.value === 'year') {
        startDate = new Date(now.getFullYear(), 0, 1)
        endDate = new Date(now.getFullYear(), 11, 31)
      }

      if (startDate && endDate) {
        return allOrders.value.filter(order => {
          const orderDate = new Date(order.createdAt)
          return orderDate >= startDate && orderDate <= endDate
        })
      }

      return allOrders.value
    }

    const fetchAnalysisData = async () => {
      try {
        let params = {}
        
        // 处理时间范围
        const now = new Date() // 使用当前时间
        if (timeRange.value === 'month') {
          const firstDay = new Date(now.getFullYear(), now.getMonth(), 1)
          const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0)
          params.startDate = formatDate(firstDay)
          params.endDate = formatDate(lastDay)
        } else if (timeRange.value === 'year') {
          const firstDay = new Date(now.getFullYear(), 0, 1)
          const lastDay = new Date(now.getFullYear(), 11, 31)
          params.startDate = formatDate(firstDay)
          params.endDate = formatDate(lastDay)
        }
        
        console.log('Fetching data with params:', params)
        const response = await axios.get('/api/orders/analysis', { params })
        
        if (response.data.code === 200) {
          const data = response.data.data
          if (data && data.stats) {
            console.log('Received stats:', data.stats)
            updateCharts(data.stats)
            updateSummary(data.stats)
          } else {
            ElMessage.warning('暂无数据')
          }
        } else {
          ElMessage.error(response.data.message || '获取数据失败')
        }
      } catch (error) {
        console.error('获取订单分析数据失败:', error)
        ElMessage.error('获取数据失败')
      }
    }

    // 格式化日期的工具函数
    const formatDate = (date) => {
      if (!date) return null
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }

    const calculateStats = (orders) => {
      // 基础统计
      const stats = {
        totalOrders: orders.length,
        totalAmount: 0,
        statusCounts: [0, 0, 0, 0, 0],
        paymentCounts: [0, 0, 0, 0],
        dailyOrderCounts: {},
        dailySales: {}
      }

      orders.forEach(order => {
        // 累加总金额
        stats.totalAmount += order.totalAmount || 0

        // 统计订单状态
        if (order.status >= 0 && order.status < 5) {
          stats.statusCounts[order.status]++
        }

        // 统计支付方式
        if (order.paymentMethod > 0 && order.paymentMethod <= 3) {
          stats.paymentCounts[order.paymentMethod]++
        }

        // 按日期统计
        const dateStr = order.createdAt.split('T')[0]
        stats.dailyOrderCounts[dateStr] = (stats.dailyOrderCounts[dateStr] || 0) + 1
        stats.dailySales[dateStr] = (stats.dailySales[dateStr] || 0) + (order.totalAmount || 0)
      })

      return stats
    }

    const updateCharts = (data) => {
      if (!data) return

      // 更新订单趋势图
      const dates = Object.keys(data.dailyOrderCounts || {}).sort()
      const orderCounts = dates.map(date => data.dailyOrderCounts[date] || 0)
      const salesAmounts = dates.map(date => {
        const amount = data.dailySales[date] || 0
        return amount / 100 // 转换为元
      })

      charts.orderTrend.setOption({
        title: { text: '订单数量趋势' },
        tooltip: { 
          trigger: 'axis',
          formatter: '{b}<br/>{a}: {c} 单'
        },
        xAxis: { 
          type: 'category', 
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: { 
          type: 'value',
          minInterval: 1
        },
        series: [{
          name: '订单数量',
          type: 'line',
          data: orderCounts,
          smooth: true,
          areaStyle: {
            opacity: 0.3
          },
          itemStyle: {
            color: '#409EFF'
          }
        }]
      }, true)

      // 更新订单状态分布图
      const statusNames = ['待支付', '已支付', '已发货', '已完成', '已取消']
      const statusData = (data.statusCounts || [0, 0, 0, 0, 0]).map((count, index) => ({
        value: count,
        name: statusNames[index]
      })).filter(item => item.value > 0)

      charts.orderStatus.setOption({
        title: { 
          text: '订单状态分布',
          left: 'center'
        },
        tooltip: { 
          trigger: 'item',
          formatter: '{b}: {c} 单 ({d}%)'
        },
        legend: { 
          orient: 'horizontal', 
          bottom: 'bottom',
          type: 'scroll',
          padding: [0, 20]
        },
        series: [{
          name: '订单状态',
          type: 'pie',
          radius: ['0%', '60%'],
          center: ['50%', '45%'],
          data: statusData,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          label: {
            show: true,
            formatter: '{b}\n{c}单\n{d}%'
          }
        }]
      }, true)

      // 更新支付方式分布图
      const paymentNames = ['未支付', '微信支付', '支付宝', '银行卡']
      const paymentData = (data.paymentCounts || [0, 0, 0, 0]).map((count, index) => ({
        value: count,
        name: paymentNames[index]
      })).filter(item => item.value > 0 && item.name !== '未支付')

      charts.paymentMethod.setOption({
        title: { 
          text: '支付方式分布',
          left: 'center'
        },
        tooltip: { 
          trigger: 'item',
          formatter: '{b}: {c} 单 ({d}%)'
        },
        legend: { 
          orient: 'horizontal', 
          bottom: 'bottom',
          type: 'scroll',
          padding: [0, 20]
        },
        series: [{
          name: '支付方式',
          type: 'pie',
          radius: ['30%', '60%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: true,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            position: 'outside',
            formatter: '{b}\n{c}单\n{d}%',
            alignTo: 'edge',
            minMargin: 5,
            edgeDistance: 10,
            lineHeight: 15
          },
          labelLine: {
            length: 15,
            length2: 0,
            maxSurfaceAngle: 80
          },
          data: paymentData
        }]
      }, true)

      // 更新销售额统计图
      charts.sales.setOption({
        title: { text: '销售额统计' },
        tooltip: { 
          trigger: 'axis',
          formatter: '{b}<br/>{a}: ¥{c}'
        },
        xAxis: { 
          type: 'category', 
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: { 
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [{
          name: '销售额',
          type: 'bar',
          data: salesAmounts,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ])
          },
          emphasis: {
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#66b1ff' },
                { offset: 0.7, color: '#409EFF' },
                { offset: 1, color: '#409EFF' }
              ])
            }
          }
        }]
      }, true)
    }

    const updateSummary = (data) => {
      if (!data) return
      
      totalOrders.value = data.totalOrders || 0
      totalAmount.value = data.totalAmount ? (data.totalAmount / 100).toFixed(2) : '0.00'
      avgOrderAmount.value = data.avgOrderAmount ? (data.avgOrderAmount / 100).toFixed(2) : '0.00'
      
      const completedOrders = data.statusCounts ? data.statusCounts[3] : 0
      const canceledOrders = data.statusCounts ? data.statusCounts[4] : 0
      const validOrders = data.totalOrders - canceledOrders
      completionRate.value = validOrders > 0 ? 
        ((completedOrders / validOrders) * 100).toFixed(1) : '0.0'
    }

    const handleTimeRangeChange = () => {
      fetchAnalysisData()
    }

    onMounted(() => {
      initCharts()
      fetchAnalysisData()
    })

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      Object.values(charts).forEach(chart => {
        chart && chart.dispose()
      })
    })

    return {
      timeRange,
      totalOrders,
      totalAmount,
      avgOrderAmount,
      completionRate,
      orderTrendChart,
      orderStatusChart,
      paymentMethodChart,
      salesChart,
      handleTimeRangeChange
    }
  }
}
</script>

<style scoped>
.order-analysis {
  padding: 20px;
}

.analysis-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.time-range {
  margin-left: 20px;
}

.data-card {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  text-align: center;
}

.data-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.data-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
</style> 