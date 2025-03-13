<template>
  <div class="product-detail">
    <div class="header">
      <el-page-header @back="goBack" :title="product.name">
        <template #content>
          <span class="page-header-title">产品详情</span>
        </template>
      </el-page-header>
    </div>

    <div class="detail-content" v-loading="loading">
      <div class="content-layout">
        <div class="left-section">
          <div class="image-section">
            <el-carousel v-if="getImages.length > 0" height="400px" :autoplay="false">
              <el-carousel-item v-for="(image, index) in getImages" :key="index">
                <el-image 
                  :src="image" 
                  fit="contain"
                  class="product-image"
                  :preview-src-list="getImages"
                />
              </el-carousel-item>
            </el-carousel>
            <div v-else class="no-image">
              <el-icon><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
          </div>
          <div class="image-upload-section">
            <el-input v-model="editForm.imageInput" placeholder="请输入本地图片路径，例如：/uploads/bikes/xxx.jpg" style="margin: 10px 0" />
            <el-button @click="addImage" style="margin-bottom: 10px">添加图片</el-button>
            <div class="image-list">
              <div v-for="(image, index) in editForm.images" :key="index" class="image-item">
                <el-image :src="image" fit="cover" class="preview-image" />
                <el-button type="danger" @click="removeImage(index)" size="small">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <div class="right-section">
          <el-form :model="editForm" :rules="rules" ref="formRef" label-width="100px">
            <div class="form-section">
              <h2 class="section-title">基本信息</h2>
              <el-form-item label="产品名称" prop="name">
                <el-input v-model="editForm.name" />
              </el-form-item>
              <el-form-item label="品牌" prop="brand">
                <el-input v-model="editForm.brand" />
              </el-form-item>
              <el-form-item label="型号" prop="model">
                <el-input v-model="editForm.model" />
              </el-form-item>
              <el-form-item label="销售价格" prop="price">
                <el-input-number v-model="editForm.price" :precision="2" :step="0.1" :min="0" />
              </el-form-item>
              <el-form-item label="成本价格" prop="costPrice">
                <el-input-number v-model="editForm.costPrice" :precision="2" :step="0.1" :min="0" />
              </el-form-item>
              <div class="info-item">
                <span class="label">库存：</span>
                <span class="value">{{ product.stock }}</span>
              </div>
              <div class="info-item">
                <span class="label">销量：</span>
                <span class="value">{{ product.sales }}</span>
              </div>
              <div class="info-item">
                <span class="label">状态：</span>
                <el-tag :type="product.status === 1 ? 'success' : 'info'">
                  {{ product.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </div>
            </div>

            <div class="form-section">
              <h2 class="section-title">规格参数</h2>
              <el-form-item label="类别" prop="category">
                <el-input v-model="editForm.category" />
              </el-form-item>
              <el-form-item label="子类别" prop="subcategory">
                <el-input v-model="editForm.subcategory" />
              </el-form-item>
              <el-form-item 
                v-for="(spec, index) in editForm.specifications" 
                :key="index"
                :label="spec.key"
                :prop="'specifications.' + index + '.value'"
              >
                <div class="spec-item">
                  <el-input v-model="spec.key" placeholder="参数名" style="width: 150px; margin-right: 10px" />
                  <el-input v-model="spec.value" placeholder="参数值" style="flex: 1" />
                  <el-button type="danger" @click="removeSpec(index)" style="margin-left: 10px">删除</el-button>
                </div>
              </el-form-item>
              <el-button type="primary" @click="addSpec" plain>添加规格参数</el-button>
            </div>

            <div class="form-section">
              <h2 class="section-title">产品描述</h2>
              <el-form-item label="描述" prop="description">
                <el-input
                  type="textarea"
                  v-model="editForm.description"
                  :rows="3"
                />
              </el-form-item>
              <el-form-item label="产品链接" prop="productUrl">
                <el-input v-model="editForm.productUrl" />
              </el-form-item>
            </div>

            <div class="bottom-section">
              <el-tabs>
                <el-tab-pane label="规格参数">
                  <div class="specs" v-if="product.specs">
                    <el-descriptions :column="2" border>
                      <el-descriptions-item 
                        v-for="(value, key) in parseSpecs(product.specs)" 
                        :key="key" 
                        :label="key.replace(' :', '')"
                      >
                        {{ value }}
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                  <div class="no-data" v-else>暂无规格参数</div>
                </el-tab-pane>
                
                <el-tab-pane label="产品描述">
                  <div class="description">
                    <template v-if="product.description || (parseSpecs(product.specs)?.url)">
                      <div v-if="product.description" class="description-text">
                        {{ product.description }}
                      </div>
                      <div class="links-section">
                        <div v-for="(link, index) in parseDescription(product.description)" :key="index" class="link-item">
                          <span class="link-label">{{ link.label }}：</span>
                          <template v-if="link.label === '描述'">
                            <span class="description-content">{{ link.url }}</span>
                          </template>
                          <template v-else>
                            <el-link type="primary" :href="link.url" target="_blank">{{ link.url }}</el-link>
                          </template>
                        </div>
                      </div>
                    </template>
                    <div v-else class="no-data">暂无描述</div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>

            <div class="form-actions">
              <el-button type="primary" @click="saveEdit">保存</el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const product = ref({})
const isEditing = ref(false)
const formRef = ref(null)

// 编辑表单数据
const editForm = ref({
  name: '',
  brand: '',
  model: '',
  price: 0,
  costPrice: 0,
  stock: 0,
  warningStock: 10,
  status: 1,
  category: '',
  subcategory: '',
  specifications: [],
  images: [],
  imageInput: '',
  description: '',
  productUrl: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
  price: [{ required: true, message: '请输入销售价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

// 获取图片列表
const getImages = computed(() => {
  try {
    // 优先使用本地图片路径
    if (product.value.localImages) {
      if (typeof product.value.localImages === 'string') {
        const images = JSON.parse(product.value.localImages)
        return images
          .filter(url => url && typeof url === 'string' && url.trim() !== '')
          .map(url => {
            // 确保所有路径都以 /uploads/bikes/ 开头，并且没有重复的bikes目录
            if (!url.startsWith('/uploads/bikes/')) {
              return '/uploads/bikes/' + url.replace(/^\/+|^\/?(uploads\/)?bikes\//g, '')
            }
            return url
          })
      }
      if (Array.isArray(product.value.localImages)) {
        return product.value.localImages
          .filter(url => url && typeof url === 'string' && url.trim() !== '')
          .map(url => {
            // 确保所有路径都以 /uploads/bikes/ 开头，并且没有重复的bikes目录
            if (!url.startsWith('/uploads/bikes/')) {
              return '/uploads' + url.replace(/^\/+|^\/?(uploads\/)?bikes\//g, '')
            }
            return url
          })
      }
    }
    // 如果没有本地图片，返回空数组
    return []
  } catch (e) {
    console.error('解析本地图片列表失败:', e)
    return []
  }
})

// 初始化编辑表单
const initEditForm = () => {
  const specs = product.value.specs || {}
  const specifications = []
  
  // 添加类别和子类别
  specifications.push({ key: '类别', value: specs.category || '' })
  specifications.push({ key: '子类别', value: specs.subcategory || '' })

  // 处理规格参数
  if (specs.specifications) {
    Object.entries(specs.specifications).forEach(([key, value]) => {
      specifications.push({ key, value: String(value) })
    })
  }

  // 处理图片数组
  let images = []
  try {
    if (typeof product.value.localImages === 'string') {
      images = JSON.parse(product.value.localImages)
      // 确保所有图片路径都是正确的格式
      images = images.map(url => {
        if (!url.startsWith('/uploads/bikes/')) {
          return '/uploads/bikes/' + url.replace(/^\/+|^\/?(uploads\/)?bikes\//g, '')
        }
        return url
      })
    } else if (Array.isArray(product.value.localImages)) {
      images = product.value.localImages.map(url => {
        if (!url.startsWith('/uploads/bikes/')) {
          return '/uploads/bikes/' + url.replace(/^\/+|^\/?(uploads\/)?bikes\//g, '')
        }
        return url
      })
    }
  } catch (e) {
    console.error('解析图片数组失败:', e)
  }

  // 从数据库中获取的数据填充到表单中
  editForm.value = {
    name: product.value.name || '',
    brand: product.value.brand || '',
    model: product.value.model || '',
    price: product.value.price || 0,
    costPrice: product.value.costPrice || 0,
    stock: product.value.stock || 0,
    warningStock: product.value.warningStock || 10,
    status: product.value.status || 1,
    category: specs.category || '',
    subcategory: specs.subcategory || '',
    specifications,
    images: Array.isArray(images) ? images : [],
    imageInput: '',
    description: product.value.description || '',
    productUrl: specs.url || ''
  }
}

// 处理编辑按钮点击
const handleEdit = () => {
  isEditing.value = true
  initEditForm()
}

// 添加规格参数
const addSpec = () => {
  editForm.value.specifications.push({ key: '', value: '' })
}

// 删除规格参数
const removeSpec = (index) => {
  editForm.value.specifications.splice(index, 1)
}

// 添加图片
const addImage = () => {
  if (editForm.value.imageInput && editForm.value.imageInput.trim()) {
    const newImage = editForm.value.imageInput.trim()
    // 检查是否是有效的本地图片路径
    if (!newImage.startsWith('/uploads/bikes/')) {
      ElMessage.warning('请输入有效的本地图片路径，必须以 /uploads/bikes/ 开头')
      return
    }
    // 确保images是数组
    if (!Array.isArray(editForm.value.images)) {
      editForm.value.images = []
    }
    // 检查是否已存在相同的图片
    if (!editForm.value.images.includes(newImage)) {
      editForm.value.images.push(newImage)
    }
    editForm.value.imageInput = ''
  }
}

// 删除图片
const removeImage = (index) => {
  if (Array.isArray(editForm.value.images)) {
    editForm.value.images.splice(index, 1)
  }
}

// 取消编辑
const cancelEdit = () => {
  ElMessageBox.confirm('确定要取消编辑吗？未保存的修改将会丢失。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    isEditing.value = false
  }).catch(() => {})
}

// 保存编辑
const saveEdit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 构建规格参数对象
    const specs = {
      category: editForm.value.category || '',
      subcategory: editForm.value.subcategory || '',
      specifications: {},
      url: editForm.value.productUrl || ''
    }

    // 处理规格参数
    editForm.value.specifications.forEach(spec => {
      if (spec.key && spec.value) {
        specs.specifications[spec.key] = spec.value
      }
    })

    // 处理图片数组，确保是有效的路径
    const images = Array.isArray(editForm.value.images) 
      ? editForm.value.images
          .filter(url => url && typeof url === 'string' && url.trim() !== '')
          .map(url => {
            if (!url.startsWith('/uploads/bikes/')) {
              return '/uploads/bikes/' + url.replace(/^\/+|^\/?(uploads\/)?bikes\//g, '')
            }
            return url
          })
      : []

    // 构建提交的数据
    const submitData = {
      id: product.value.id,
      name: editForm.value.name,
      brand: editForm.value.brand,
      model: editForm.value.model,
      price: Number(editForm.value.price),
      costPrice: Number(editForm.value.costPrice),
      stock: Number(editForm.value.stock),
      status: Number(editForm.value.status),
      specs: JSON.stringify(specs),
      categoryId: product.value.categoryId,
      description: editForm.value.description,
      warningStock: Number(editForm.value.warningStock),
      sales: product.value.sales || 0,
      images: JSON.stringify(images),
      localImages: JSON.stringify(images)  // 使用相同的图片路径更新localImages字段
    }

    // 发送更新请求
    const response = await axios.put(`/api/products/${product.value.id}`, submitData)
    if (response.data.code === 200) {
      ElMessage.success('保存成功')
      fetchProductDetail()
    } else {
      throw new Error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败: ' + (error.response?.data?.message || error.message))
  }
}

const fetchProductDetail = async () => {
  loading.value = true
  try {
    const response = await axios.get(`/api/products/${route.params.id}`)
    if (response.data.code === 200) {
      product.value = response.data.data
      initEditForm()
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
    ElMessage.error('获取产品详情失败')
  } finally {
    loading.value = false
  }
}

const parseSpecs = (specs) => {
  if (!specs) return {}
  
  // 提取所有规格信息
  const result = {
    '类别': specs.category,
    '子类别': specs.subcategory
  }

  // 处理规格参数
  if (specs.specifications) {
    Object.entries(specs.specifications).forEach(([key, value]) => {
      if (key !== 'images') {
        result[key] = String(value)
      }
    })
  }
  
  return result
}

const parseImages = (images) => {
  return Array.isArray(images) ? images : []
}

const parseDescription = (description) => {
  if (!description) {
    const specs = product.value.specs || {}
    if (specs.url) {
      return [{
        label: '产品链接',
        url: specs.url
      }]
    }
    return []
  }

  const links = []
  const lines = description.split('\n')
  lines.forEach(line => {
    const parts = line.split(': ')
    if (parts.length === 2) {
      links.push({
        label: parts[0],
        url: parts[1]
      })
    } else {
      links.push({
        label: '描述',
        url: line
      })
    }
  })
  return links
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchProductDetail().then(() => {
    initEditForm()
  })
})
</script>

<style scoped>
.product-detail {
  padding: 20px;
  background-color: #f5f7fa;
  height: 100vh;
  overflow-y: auto;
}

.header {
  background-color: #fff;
  padding: 16px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.page-header-title {
  font-size: 18px;
  font-weight: 600;
}

.detail-content {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.content-layout {
  display: flex;
  gap: 20px;
  width: 100%;
}

.left-section {
  position: sticky;
  top: 20px;
  flex: 0 0 500px;
  height: fit-content;
}

.right-section {
  flex: 1;
  min-width: 600px;
  overflow-y: auto;
  padding-right: 20px;
}

.image-section {
  width: 100%;
  height: 400px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 20px;
}

.image-upload-section {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
}

.no-image .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.product-name {
  font-size: 24px;
  color: #303133;
  margin: 0 0 20px 0;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.label {
  color: #909399;
  width: 80px;
}

.value {
  color: #303133;
}

.value.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 600;
}

.description {
  padding: 20px;
}

.link-item {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.link-label {
  color: #909399;
  margin-right: 10px;
  min-width: 80px;
}

.specs {
  padding: 20px;
}

:deep(.el-descriptions__label) {
  width: 120px;
  color: #606266;
}

:deep(.el-descriptions__content) {
  color: #303133;
}

.no-data {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

:deep(.el-carousel__item) {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
}

.description-text {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #606266;
}

.links-section {
  margin-top: 15px;
}

.description-content {
  color: #606266;
  line-height: 1.6;
}

:deep(.el-descriptions__cell) {
  padding: 12px 20px;
}

.edit-form {
  width: 100%;
  padding: 20px;
}

.form-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow-x: hidden;
}

.section-title {
  font-size: 18px;
  color: #303133;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #dcdfe6;
}

.spec-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.image-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  margin-top: 10px;
}

.image-item {
  text-align: center;
}

.preview-image {
  width: 100%;
  height: 150px;
  margin-bottom: 5px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

:deep(.el-form-item__label) {
  font-weight: normal;
  color: #606266;
}

:deep(.el-input-number) {
  width: 100%;
}

.bottom-section {
  margin-top: 20px;
}

.form-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.info-item {
  margin: 15px 0;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

:deep(.el-form) {
  height: 100%;
  overflow-y: auto;
}

:deep(.el-tabs__content) {
  overflow-x: auto;
}

:deep(.el-descriptions) {
  width: 100%;
  overflow-x: auto;
}
</style> 