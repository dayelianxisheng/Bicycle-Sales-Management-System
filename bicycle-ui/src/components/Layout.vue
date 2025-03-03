<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo" :class="{ 'collapsed-logo': isCollapse }">
        <img src="../assets/img_2.png" alt="logo">
        <span v-show="!isCollapse">管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409EFF"
        router
        :collapse-transition="false"
      >
        <el-menu-item index="/layout/home">
          <el-icon>
            <template #default>
              <House />
            </template>
          </el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-sub-menu index="1">
          <template #title>
            <el-icon>
              <template #default>
                <Setting />
              </template>
            </el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/layout/user-management">
            <el-icon>
              <template #default>
                <User />
              </template>
            </el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/layout/product-management">
            <el-icon>
              <template #default>
                <ShoppingCart />
              </template>
            </el-icon>
            <span>产品管理</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="6">
          <template #title>
            <el-icon>
              <template #default>
                <Goods />
              </template>
            </el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/layout/stock-overview">
            <el-icon>
              <template #default>
                <DataLine />
              </template>
            </el-icon>
            <span>库存概览</span>
          </el-menu-item>
          <el-menu-item index="/layout/stock-in">
            <el-icon>
              <template #default>
                <ArrowDown />
              </template>
            </el-icon>
            <span>入库管理</span>
          </el-menu-item>
          <el-menu-item index="/layout/stock-out">
            <el-icon>
              <template #default>
                <ArrowUp />
              </template>
            </el-icon>
            <span>出库管理</span>
          </el-menu-item>
          <el-menu-item index="/layout/stock-warning">
            <el-icon>
              <template #default>
                <Warning />
              </template>
            </el-icon>
            <span>库存预警</span>
          </el-menu-item>
<!--          <el-menu-item index="/layout/stock-check">-->
<!--            <el-icon>-->
<!--              <template #default>-->
<!--                <Document />-->
<!--              </template>-->
<!--            </el-icon>-->
<!--            <span>库存盘点</span>-->
<!--          </el-menu-item>-->
        </el-sub-menu>

        <el-sub-menu index="2">
          <template #title>
            <el-icon>
              <template #default>
                <ShoppingBag />
              </template>
            </el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/layout/order-list">
            <el-icon>
              <template #default>
                <List />
              </template>
            </el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/layout/order-analysis">
            <el-icon>
              <template #default>
                <Histogram />
              </template>
            </el-icon>
            <span>订单分析</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="3">
          <template #title>
            <el-icon>
              <template #default>
                <UserFilled />
              </template>
            </el-icon>
            <span>客户管理</span>
          </template>
          <el-menu-item index="/layout/customer-list">
            <el-icon>
              <template #default>
                <List />
              </template>
            </el-icon>
            <span>客户列表</span>
          </el-menu-item>
          <el-menu-item index="/layout/customer-analysis">
            <el-icon>
              <template #default>
                <DataLine />
              </template>
            </el-icon>
            <span>客户分析</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="4">
          <template #title>
            <el-icon>
              <template #default>
                <Tools />
              </template>
            </el-icon>
            <span>数据分析</span>
          </template>
          <el-menu-item index="/layout/spark-analysis">
            <el-icon>
              <template #default>
                <DataLine />
              </template>
            </el-icon>
            <span>Spark分析</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="5">
          <template #title>
            <el-icon>
              <template #default>
                <Tools />
              </template>
            </el-icon>
            <span>数据生成</span>
          </template>
          <el-menu-item index="/layout/generate-orders">
            <el-icon>
              <template #default>
                <Document />
              </template>
            </el-icon>
            <span>订单生成</span>
          </el-menu-item>
          <el-menu-item index="/layout/generate-customers">
            <el-icon>
              <template #default>
                <User />
              </template>
            </el-icon>
            <span>客户生成</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主要内容区 -->
    <el-container class="main-container" :class="{ 'collapsed-main': isCollapse }">
      <el-header class="header">
        <div class="header-left">
          <el-icon 
            class="toggle-sidebar" 
            @click="toggleSidebar"
          >
            <template #default>
              <component :is="isCollapse ? Expand : Fold" />
            </template>
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/layout/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-space>
            <div class="search-wrapper" :class="{ 'is-active': showSearch }">
              <el-icon class="search-icon" @click="handleSearchClick">
                <template #default>
                  <Search />
                </template>
              </el-icon>
              <el-input
                v-model="searchKeyword"
                class="search-input"
                placeholder="请输入菜单内容"
                ref="searchInput"
                @blur="handleSearchBlur"
                clearable
              >
                <template #prefix>
                  <el-icon>
                    <template #default>
                      <Search />
                    </template>
                  </el-icon>
                </template>
              </el-input>
            </div>

            <div class="icon-button" @click="toggleFullScreen">
              <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏'" placement="bottom">
                <el-icon>
                  <template #default>
                    <component :is="isFullscreen ? Aim : FullScreen" />
                  </template>
                </el-icon>
              </el-tooltip>
            </div>
            
            <div class="icon-button">
              <el-tooltip content="消息" placement="bottom">
                <el-badge :value="3">
                  <el-icon>
                    <template #default>
                      <Bell />
                    </template>
                  </el-icon>
                </el-badge>
              </el-tooltip>
            </div>

            <el-dropdown @command="handleCommand">  
              <span class="user-info">
                <el-avatar 
                  size="small" 
                  :src="avatarUrl" 
                  @error="handleAvatarError"
                >
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span>{{ username }}</span>
                <el-icon class="el-icon--right">
                  <CaretBottom />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>系统设置
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-space>
        </div>
      </el-header>

      <!-- 添加标签页导航 -->
      <div class="tabs-nav">
        <el-tabs
          v-model="activeTab"
          type="card"
          :closable="true"
          @tab-click="handleTabClick"
          @tab-remove="handleTabRemove"
        >
          <el-tab-pane
            v-for="tab in visitedViews"
            :key="tab.path"
            :label="tab.title"
            :name="tab.path"
            :closable="tab.path !== '/layout'"
          >
            <template #label>
              <span @contextmenu.prevent="(e) => handleContextMenu(e, tab)">{{ tab.title }}</span>
            </template>
          </el-tab-pane>
        </el-tabs>

        <!-- 右键菜单 -->
        <ul v-show="contextMenuVisible" 
            :style="{ left: contextMenuX + 'px', top: contextMenuY + 'px' }" 
            class="contextmenu"
        >
          <li @click="refreshSelected">
            <el-icon>
              <template #default>
                <Refresh />
              </template>
            </el-icon>
            重新加载
          </li>
          <li @click="closeSelected" v-if="selectedTab.path !== '/layout'">
            <el-icon>
              <template #default>
                <Close />
              </template>
            </el-icon>
            关闭标签页
          </li>
          <li @click="closeOthers">
            <el-icon>
              <template #default>
                <CircleClose />
              </template>
            </el-icon>
            关闭其他标签页
          </li>
          <li @click="closeLeft">
            <el-icon>
              <template #default>
                <ArrowLeftBold />
              </template>
            </el-icon>
            关闭左侧标签页
          </li>
          <li @click="closeRight">
            <el-icon>
              <template #default>
                <ArrowRightBold />
              </template>
            </el-icon>
            关闭右侧标签页
          </li>
          <li @click="closeAll">
            <el-icon>
              <template #default>
                <Delete />
              </template>
            </el-icon>
            关闭全部标签页
          </li>
        </ul>
      </div>

      <!-- 搜索结果下拉框 -->
      <div v-show="showSearch && searchResults.length" class="search-dropdown">
        <div
          v-for="item in searchResults"
          :key="item.path"
          class="search-item"
          @click="handleSearchItemClick(item)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </div>
      </div>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <component :is="Component" :key="refreshKey" />
        </router-view>
      </el-main>
    </el-container>

    <!-- 搜索对话框 -->
    <el-dialog
      v-model="showSearch"
      title="搜索菜单"
      width="500px"
      :show-close="false"
      @closed="handleSearchClose"
    >
      <el-input
        v-model="searchKeyword"
        placeholder="搜索菜单"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <div class="search-result" v-if="searchResults.length">
        <div
          v-for="item in searchResults"
          :key="item.path"
          class="search-item"
          @click="handleSearchItemClick(item)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </div>
      </div>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  House, 
  User, 
  Fold,
  Expand,
  Setting,
  ShoppingCart,
  Bell,
  FullScreen,
  CaretBottom,
  SwitchButton,
  Refresh,
  Close,
  CircleClose,
  ArrowLeftBold,
  ArrowRightBold,
  Delete,
  Search,
  Aim,
  ShoppingBag,
  UserFilled,
  List,
  DataLine,
  Tools,
  Document,
  Histogram,
  Goods,
  ArrowDown,
  ArrowUp,
  Warning
} from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

// 基础状态
const username = ref(localStorage.getItem('username') || '管理员')
const isCollapse = ref(false)
const activeMenu = ref(route.path)
const activeTab = ref('/layout/home')
const visitedViews = ref([
  {
    title: '首页',
    path: '/layout/home'
  }
])

// 右键菜单相关
const contextMenuVisible = ref(false)
const contextMenuX = ref(0)
const contextMenuY = ref(0)
const selectedTab = ref({})

// 添加 refreshKey
const refreshKey = ref(0)

// 全屏相关
const isFullscreen = ref(false)

// 路由映射表
const routeMap = {
  '/layout': '首页',
  '/layout/user-management': '用户管理',
  '/layout/product-management': '产品管理',
  '/layout/user-profile': '个人信息',
  '/layout/order-list': '订单列表',
  '/layout/order-analysis': '订单分析',
  '/layout/customer-list': '客户列表',
  '/layout/customer-analysis': '客户分析',
  '/layout/generate-orders': '订单生成',
  '/layout/generate-customers': '客户生成',
  '/layout/generate-products': '商品生成',
  '/layout/spark-analysis': 'Spark分析',
  '/layout/stock-overview': '库存概览',
  '/layout/stock-in': '入库管理',
  '/layout/stock-out': '出库管理',
  '/layout/stock-warning': '库存预警',
  // '/layout/stock-check': '库存盘点'
}

// 计算当前路由名称
const currentRoute = computed(() => {
  return routeMap[route.path] || '首页'
})

// 添加访问视图
const addVisitedView = (route) => {
  try {
    const { path } = route
    const title = routeMap[path] || '未知页面'
    
    if (!visitedViews.value.some(v => v.path === path)) {
      visitedViews.value.push({
        title,
        path
      })
    }
  } catch (error) {
    console.error('添加访问视图失败:', error)
  }
}

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    try {
      addVisitedView(route)
      activeTab.value = newPath
      activeMenu.value = newPath
    } catch (error) {
      console.error('路由监听处理失败:', error)
    }
  },
  { immediate: true }
)

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 切换全屏
const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

// 监听全屏变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 用户信息相关
const userInfo = reactive({
  username: '',
  nickname: '',
  avatar: '',
})

// 计算头像完整URL
const avatarUrl = computed(() => {
  if (!userInfo.avatar) return ''
  if (userInfo.avatar.startsWith('http')) {
    return userInfo.avatar
  }
  return `http://localhost:8080${userInfo.avatar}`
})

// 处理头像加载错误
const handleAvatarError = () => {
  userInfo.avatar = ''
}

// 获取用户信息
const getUserInfo = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      return
    }

    const response = await axios.get('/api/users/profile', {
      headers: { 
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })

    if (response.data.code === 200) {
      const data = response.data.data
      Object.assign(userInfo, {
        ...data,
        avatar: data.avatar || ''
      })
      username.value = data.nickname || data.username
    } else {
      ElMessage.error(response.data.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    if (error.response?.status === 403) {
      ElMessage.error('没有权限或 token 已过期，请重新登录')
    } else {
      ElMessage.error('获取用户信息失败')
    }
  }
}

// 处理用户命令
const handleCommand = async (command) => {
  try {
    if (command === 'logout') {
      await ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      router.push('/')
      ElMessage.success('退出成功')
    } else if (command === 'profile') {
      router.push('/layout/user-profile')
    } else if (command === 'settings') {
      ElMessage.info('系统设置功能开发中...')
    }
  } catch (error) {
    console.log('操作取消')
  }
}

// 标签页相关
const handleTabClick = (tab) => {
  try {
    if (tab?.props?.name && tab.props.name !== route.path) {
      router.push(tab.props.name)
    }
  } catch (error) {
    console.error('标签页点击处理失败:', error)
  }
}

const handleTabRemove = (targetPath) => {
  try {
    if (targetPath === '/layout') return
    
    const tabs = [...visitedViews.value]
    let activePath = null
    
    if (route.path === targetPath) {
      tabs.forEach((tab, index) => {
        if (tab.path === targetPath) {
          const nextTab = tabs[index + 1] || tabs[index - 1]
          if (nextTab) {
            activePath = nextTab.path
          }
        }
      })
    }
    
    visitedViews.value = tabs.filter(tab => tab.path !== targetPath)
    
    if (activePath) {
      router.push(activePath)
    }
  } catch (error) {
    console.error('标签页移除处理失败:', error)
  }
}

// 右键菜单相关
const handleContextMenu = (e, tab) => {
  e.preventDefault()
  contextMenuVisible.value = true
  contextMenuX.value = e.clientX
  contextMenuY.value = e.clientY
  selectedTab.value = tab
}

const closeContextMenu = () => {
  contextMenuVisible.value = false
}

const refreshSelected = () => {
  refreshKey.value = Date.now()
  closeContextMenu()
  ElMessage.success('刷新成功')
}

const closeSelected = () => {
  handleTabRemove(selectedTab.value.path)
  closeContextMenu()
}

const closeOthers = () => {
  visitedViews.value = visitedViews.value.filter(
    tab => tab.path === '/layout' || tab.path === selectedTab.value.path
  )
  if (selectedTab.value.path !== route.path) {
    router.push(selectedTab.value.path)
  }
  closeContextMenu()
}

const closeLeft = () => {
  const selectedIndex = visitedViews.value.findIndex(tab => tab.path === selectedTab.value.path)
  visitedViews.value = visitedViews.value.filter((tab, index) => 
    tab.path === '/layout' || index >= selectedIndex
  )
  if (!visitedViews.value.find(tab => tab.path === route.path)) {
    router.push(selectedTab.value.path)
  }
  closeContextMenu()
}

const closeRight = () => {
  const selectedIndex = visitedViews.value.findIndex(tab => tab.path === selectedTab.value.path)
  visitedViews.value = visitedViews.value.filter((tab, index) => 
    tab.path === '/layout' || index <= selectedIndex
  )
  if (!visitedViews.value.find(tab => tab.path === route.path)) {
    router.push(selectedTab.value.path)
  }
  closeContextMenu()
}

const closeAll = () => {
  visitedViews.value = visitedViews.value.filter(tab => tab.path === '/layout')
  router.push('/layout')
  closeContextMenu()
}

// 搜索相关
const showSearch = ref(false)
const searchKeyword = ref('')
const searchResults = ref([])
const searchInput = ref(null)

const menuList = [
  { path: '/layout/home', title: '首页', icon: 'House' },
  { path: '/layout/user-management', title: '用户管理', icon: 'User' },
  { path: '/layout/product-management', title: '产品管理', icon: 'ShoppingCart' },
  { path: '/layout/user-profile', title: '个人信息', icon: 'User' },
  { path: '/layout/order-list', title: '订单列表', icon: 'List' },
  { path: '/layout/order-analysis', title: '订单分析', icon: 'DataLine' },
  { path: '/layout/customer-list', title: '客户列表', icon: 'List' },
  { path: '/layout/customer-analysis', title: '客户分析', icon: 'DataLine' },
  { path: '/layout/generate-orders', title: '订单生成', icon: 'Document' },
  { path: '/layout/generate-customers', title: '客户生成', icon: 'User' },
  { path: '/layout/stock-overview', title: '库存概览', icon: 'DataLine' },
  { path: '/layout/stock-in', title: '入库管理', icon: 'ArrowDown' },
  { path: '/layout/stock-out', title: '出库管理', icon: 'ArrowUp' },
  { path: '/layout/stock-warning', title: '库存预警', icon: 'Warning' },
  // { path: '/layout/stock-check', title: '库存盘点', icon: 'Document' },
]

const handleSearchClick = () => {
  showSearch.value = true
  nextTick(() => {
    searchInput.value?.focus()
  })
}

const handleSearchBlur = () => {
  if (!searchKeyword.value) {
    showSearch.value = false
  }
}

const handleSearch = () => {
  if (!searchKeyword.value) {
    searchResults.value = []
    return
  }
  searchResults.value = menuList.filter(item => 
    item.title.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
}

const handleSearchItemClick = (item) => {
  router.push(item.path)
  showSearch.value = false
  searchKeyword.value = ''
}

const handleSearchClose = () => {
  searchKeyword.value = ''
  searchResults.value = []
}

// 生命周期钩子
onMounted(() => {
  getUserInfo()
  document.addEventListener('click', closeContextMenu)
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', closeContextMenu)
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})

// 监听搜索关键词变化
watch(searchKeyword, handleSearch)
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.aside {
  background-color: #001529;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  overflow-y: auto;
  transition: width 0.3s;
  z-index: 1000;
}

.logo {
  height: 64px;
  line-height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #002140;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  overflow: hidden;
  white-space: nowrap;
  transition: all 0.3s;
}

.collapsed-logo {
  padding: 0 16px;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.main-container {
  margin-left: v-bind('isCollapse ? "64px" : "220px"');
  min-height: 100vh;
  transition: all 0.3s;
}

.collapsed-main {
  margin-left: 64px;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 64px;
  position: fixed;
  top: 0;
  right: 0;
  left: v-bind('isCollapse ? "64px" : "220px"');
  z-index: 9;
  transition: all 0.3s;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-sidebar {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  color: #666;
}

.header-right {
  display: flex;
  align-items: center;
}

.action-icon {
  font-size: 20px;
  cursor: pointer;
  color: #666;
  padding: 0 12px;
  height: 48px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.action-icon:hover {
  color: #409EFF;
}

.notice-badge {
  height: 48px;
  display: flex;
  align-items: center;
}

.notice-badge :deep(.el-badge__content) {
  right: 8px;
  top: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 16px;
  height: 48px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background: rgba(0,0,0,.025);
}

.user-info span {
  margin-left: 10px;
  margin-right: 6px;
  font-size: 15px;
  color: #666;
}

.header-right .el-space {
  height: 48px;
  align-items: center;
}

.tabs-nav {
  position: fixed;
  top: 64px;
  left: v-bind('isCollapse ? "64px" : "220px"');
  right: 0;
  height: 40px;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  z-index: 8;
  transition: all 0.3s;
}

.main {
  margin-top: 104px;
  padding: 20px;
  background-color: #f0f2f5;
  height: calc(100vh - 104px);
  overflow-y: auto;
}

.main::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.main::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.main::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.main::-webkit-scrollbar-corner {
  background: #f1f1f1;
}

.main:hover::-webkit-scrollbar-thumb {
  background: #999;
}

:deep(.el-tabs__header) {
  margin: 0;
  border-bottom: none;
}

:deep(.el-tabs__nav) {
  border: none !important;
}

:deep(.el-tabs__item) {
  height: 40px;
  line-height: 40px;
  border: none;
  color: #666;
  background: transparent;
}

:deep(.el-tabs__item.is-active) {
  color: #409EFF;
  background: #f0f2f5;
}

:deep(.el-tabs__item:hover) {
  color: #409EFF;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-menu-item.is-active) {
  background-color: #1890ff !important;
}

:deep(.el-breadcrumb__inner) {
  color: #666 !important;
  font-weight: normal !important;
}

:deep(.el-breadcrumb__inner.is-link:hover) {
  color: #1890ff !important;
}

:deep(.el-dropdown-menu__item i) {
  margin-right: 8px;
}

.contextmenu {
  position: fixed;
  z-index: 3000;
  background: #fff;
  border-radius: 4px;
  padding: 5px 0;
  margin: 0;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  list-style: none;
}

.contextmenu li {
  margin: 0;
  padding: 8px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;
}

.contextmenu li:hover {
  background: #f5f7fa;
  color: #409EFF;
}

.contextmenu li .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

.icon-button {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s;
}

.icon-button:hover {
  background-color: rgba(0,0,0,0.04);
}

.icon-button .el-icon {
  font-size: 20px;
  color: #666;
}

.icon-button :deep(.el-badge__content) {
  top: 8px;
  right: 8px;
}

.search-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 32px;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.search-wrapper.is-active {
  width: 200px;
}

.search-wrapper .search-icon {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
  cursor: pointer;
  color: #666;
  padding: 0 8px;
  z-index: 1;
  transition: all 0.3s;
}

.search-wrapper.is-active .search-icon {
  display: none;
}

.search-wrapper .search-input {
  position: absolute;
  right: 0;
  width: 200px;
  opacity: 0;
  transition: all 0.3s;
}

.search-wrapper.is-active .search-input {
  opacity: 1;
}

.search-wrapper :deep(.el-input__wrapper) {
  background-color: #f5f7fa;
  box-shadow: none;
  border-radius: 16px;
}

.search-wrapper :deep(.el-input__wrapper:hover) {
  background-color: #e6e8ea;
}

.search-wrapper :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: 0 0 0 1px #409EFF;
}

.search-dropdown {
  position: absolute;
  top: 50px;
  right: 0;
  width: 250px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  padding: 8px 0;
  z-index: 2000;
}

.search-item {
  padding: 8px 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.search-item:hover {
  background-color: #f5f7fa;
}

.search-item .el-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #909399;
}

.search-item span {
  color: #606266;
  font-size: 14px;
}

.fullscreen-btn,
.fullscreen-text {
  display: none;
}
</style> 