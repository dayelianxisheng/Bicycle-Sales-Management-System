<template>
  <div class="customer-analysis">
    <el-card class="analysis-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span>客户分析</span>
            <el-radio-group v-model="timeRange" class="time-range" @change="handleTimeRangeChange">
              <el-radio-button value="all">全部</el-radio-button>
              <el-radio-button value="month">本月</el-radio-button>
              <el-radio-button value="year">本年</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="data-card">
            <div class="data-title">总客户数</div>
            <div class="data-value">{{ totalCustomers }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-title">新增客户</div>
            <div class="data-value">{{ newCustomers }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-title">客单价</div>
            <div class="data-value">¥{{ avgCustomerValue }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-title">活跃率</div>
            <div class="data-value">{{ activeRate }}%</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <div ref="registerTrendChart" style="height: 400px;"></div>
        </el-col>
        <el-col :span="12">
          <div ref="orderAnalysisChart" style="height: 400px;"></div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <div ref="regionDistributionChart" style="height: 400px;"></div>
        </el-col>
        <el-col :span="12">
          <div ref="activityAnalysisChart" style="height: 400px;"></div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 基础数据
const timeRange = ref('all')
const totalCustomers = ref(0)
const newCustomers = ref(0)
const avgCustomerValue = ref('0.00')
const activeRate = ref('0.0')

// 图表实例
let charts = {
  registerTrend: null,
  orderAnalysis: null,
  regionDistribution: null,
  activityAnalysis: null
}

// 图表DOM引用
const registerTrendChart = ref(null)
const orderAnalysisChart = ref(null)
const regionDistributionChart = ref(null)
const activityAnalysisChart = ref(null)

// 初始化图表
const initCharts = () => {
  charts.registerTrend = echarts.init(registerTrendChart.value)
  charts.orderAnalysis = echarts.init(orderAnalysisChart.value)
  charts.regionDistribution = echarts.init(regionDistributionChart.value)
  charts.activityAnalysis = echarts.init(activityAnalysisChart.value)
  window.addEventListener('resize', handleResize)
}

// 处理窗口大小变化
const handleResize = () => {
  Object.values(charts).forEach(chart => {
    chart && chart.resize()
  })
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return null
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取分析数据
const fetchAnalysisData = async () => {
  try {
    let params = {}
    
    // 处理时间范围
    const now = new Date()
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
    
    const response = await axios.get('/api/customer/analysis', { params })
    
    if (response.data.code === 200) {
      const data = response.data.data
      if (data && data.stats) {
        updateCharts(data.stats)
        updateSummary(data.stats)
      } else {
        ElMessage.warning('暂无数据')
      }
    } else {
      ElMessage.error(response.data.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取客户分析数据失败:', error)
    ElMessage.error('获取数据失败')
  }
}

// 更新图表
const updateCharts = (data) => {
  if (!data) return

  // 更新注册趋势图
  const dates = Object.keys(data.dailyRegistrations || {}).sort()
  const registrations = dates.map(date => data.dailyRegistrations[date] || 0)

  charts.registerTrend.setOption({
    title: { text: '客户注册趋势' },
    tooltip: { 
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c} 人'
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
      name: '注册人数',
      type: 'line',
      data: registrations,
      smooth: true,
      areaStyle: {
        opacity: 0.3
      },
      itemStyle: {
        color: '#409EFF'
      }
    }]
  }, true)

  // 更新订单分析图
  const orderRanges = ['0-1单', '2-5单', '6-10单', '10单以上']
  const orderCounts = data.orderDistribution || [0, 0, 0, 0]

  charts.orderAnalysis.setOption({
    title: { text: '客户订单分布' },
    tooltip: { 
      trigger: 'item',
      formatter: '{b}: {c} 人 ({d}%)'
    },
    legend: { 
      orient: 'vertical',
      left: 'left',
      padding: 5
    },
    series: [{
      name: '订单分布',
      type: 'pie',
      radius: '50%',
      data: orderRanges.map((range, index) => ({
        name: range,
        value: orderCounts[index]
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }, true)

  // 更新地域分布图
  charts.regionDistribution.setOption({
    title: { text: '客户地域分布' },
    tooltip: { 
      trigger: 'item',
      formatter: '{b}: {c} 人 ({d}%)'
    },
    legend: { 
      orient: 'vertical',
      left: 'left',
      padding: 5
    },
    series: [{
      name: '地域分布',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        position: 'outside'
      },
      labelLine: {
        show: true
      },
      data: data.regionDistribution || []
    }]
  }, true)

  // 更新活跃度分析图
  const activityDates = Object.keys(data.dailyActiveUsers || {}).sort()
  const activeUsers = activityDates.map(date => data.dailyActiveUsers[date] || 0)

  charts.activityAnalysis.setOption({
    title: { text: '客户活跃度趋势' },
    tooltip: { 
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c} 人'
    },
    xAxis: { 
      type: 'category',
      data: activityDates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: { 
      type: 'value',
      minInterval: 1
    },
    series: [{
      name: '活跃用户',
      type: 'bar',
      data: activeUsers,
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

// 更新统计数据
const updateSummary = (data) => {
  if (!data) return
  
  totalCustomers.value = data.totalCustomers || 0
  newCustomers.value = data.newCustomers || 0
  avgCustomerValue.value = data.avgCustomerValue ? 
    (data.avgCustomerValue / 100).toFixed(2) : '0.00'
  activeRate.value = data.activeRate ? 
    data.activeRate.toFixed(1) : '0.0'
}

// 处理时间范围变化
const handleTimeRangeChange = () => {
  fetchAnalysisData()
}

// 生命周期钩子
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
</script>

<style scoped>
.customer-analysis {
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