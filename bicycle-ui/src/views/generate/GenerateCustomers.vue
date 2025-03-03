<template>
  <div class="generate-customers">
    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <span>客户数据生成配置</span>
        </div>
      </template>
      
      <el-form :model="generateConfig" label-width="120px">
        <el-form-item label="生成数量">
          <el-input-number 
            v-model="generateConfig.count" 
            :min="1" 
            :max="1000" 
            controls-position="right"
          />
        </el-form-item>

        <el-form-item label="地区范围">
          <el-select v-model="generateConfig.regions" multiple placeholder="请选择地区范围">
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="广州" value="广州" />
            <el-option label="深圳" value="深圳" />
            <el-option label="杭州" value="杭州" />
            <el-option label="成都" value="成都" />
            <el-option label="武汉" value="武汉" />
            <el-option label="西安" value="西安" />
            <el-option label="南京" value="南京" />
            <el-option label="重庆" value="重庆" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleGenerate" :loading="generating">
            开始生成
          </el-button>
          <el-button @click="resetConfig">重置配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 生成结果展示 -->
    <el-card class="result-card" v-if="generatedCount > 0">
      <template #header>
        <div class="card-header">
          <span>生成结果</span>
        </div>
      </template>
      
      <div class="result-info">
        <el-result
          icon="success"
          :title="'成功生成 ' + generatedCount + ' 条客户数据'"
          sub-title="数据已成功保存到数据库"
        >
          <template #extra>
            <el-button type="primary" @click="goToCustomerList">查看客户列表</el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const generating = ref(false)
const generatedCount = ref(0)

// 生成配置
const generateConfig = reactive({
  count: 10,
  regions: ['北京', '上海', '广州']
})

// 重置配置
const resetConfig = () => {
  Object.assign(generateConfig, {
    count: 10,
    regions: ['北京', '上海', '广州']
  })
}

// 生成客户数据
const handleGenerate = async () => {
  if (generating.value) return
  
  if (generateConfig.regions.length === 0) {
    ElMessage.warning('请至少选择一个地区')
    return
  }
  
  try {
    generating.value = true
    const res = await axios.post('/api/customers/generate', generateConfig)
    
    if (res.data.code === 200) {
      generatedCount.value = res.data.data
      ElMessage.success(`成功生成 ${generatedCount.value} 条客户数据`)
    } else {
      ElMessage.error(res.data.message || '生成客户数据失败')
    }
  } catch (error) {
    console.error('生成客户数据出错：', error)
    ElMessage.error('生成客户数据失败')
  } finally {
    generating.value = false
  }
}

// 跳转到客户列表
const goToCustomerList = () => {
  router.push('/layout/customer-list')
}
</script>

<style scoped>
.generate-customers {
  padding: 20px;
}

.config-card {
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

.result-info {
  text-align: center;
  padding: 20px 0;
}
</style> 