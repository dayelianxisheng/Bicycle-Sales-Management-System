import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/login/login.vue';
import Layout from '../components/Layout.vue';
import Home from '../views/Home.vue';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/layout',
    name: 'Layout',
    component: Layout,
    redirect: '/layout/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: Home,
        meta: { title: '首页', icon: 'house' }
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: () => import('../views/system/UserManagement.vue')
      },
      {
        path: 'product-management',
        name: 'ProductManagement',
        component: () => import('../views/product/ProductManagement.vue')
      },
      {
        path: 'user-profile',
        name: 'UserProfile',
        component: () => import('../views/system/UserProfile.vue')
      },
      {
        path: 'order-list',
        name: 'OrderList',
        component: () => import('../views/order/OrderList.vue')
      },
      {
        path: 'customer-list',
        name: 'CustomerList',
        component: () => import('@/views/customer/CustomerList.vue'),
        meta: { title: '客户管理', icon: 'user' }
      },
      {
        path: 'customer-orders/:id',
        name: 'CustomerOrders',
        component: () => import('@/views/customer/CustomerOrders.vue'),
        meta: { title: '客户订单', hidden: true }
      },
      {
        path: 'order-detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/OrderDetail.vue'),
        meta: { title: '订单详情', hidden: true }
      },
      {
        path: 'order-analysis',
        name: 'OrderAnalysis',
        component: () => import('@/views/order/OrderAnalysis.vue'),
        meta: { title: '订单分析' }
      },
      {
        path: 'generate-orders',
        name: 'GenerateOrders',
        component: () => import('@/views/generate/GenerateOrders.vue'),
        meta: { title: '订单生成' }
      },
      {
        path: 'generate-customers',
        name: 'GenerateCustomers',
        component: () => import('@/views/generate/GenerateCustomers.vue'),
        meta: { title: '客户生成' }
      },
      {
        path: 'generate-products',
        name: 'GenerateProducts',
        component: () => import('@/views/generate/GenerateProducts.vue'),
        meta: { title: '商品生成' }
      },
      {
        path: 'spark-analysis',
        name: 'SparkAnalysis',
        component: () => import('@/views/spark/SparkAnalysis.vue'),
        meta: {
          title: '数据分析',
          icon: 'TrendCharts'
        }
      },
      {
        path: 'customer-analysis',
        name: 'CustomerAnalysis',
        component: () => import('@/views/customer/CustomerAnalysis.vue'),
        meta: { 
          title: '客户分析',
          icon: 'chart'
        }
      },
      {
        path: 'stock-overview',
        name: 'StockOverview',
        component: () => import('@/views/stock/StockOverview.vue'),
        meta: { title: '库存概览', icon: 'chart' }
      },
      {
        path: 'stock-in',
        name: 'StockIn',
        component: () => import('@/views/stock/StockIn.vue'),
        meta: { title: '入库管理', icon: 'plus' }
      },
      {
        path: 'stock-out',
        name: 'StockOut',
        component: () => import('@/views/stock/StockOut.vue'),
        meta: { title: '出库管理', icon: 'minus' }
      },
      {
        path: 'stock-warning',
        name: 'StockWarning',
        component: () => import('@/views/stock/StockWarning.vue'),
        meta: { title: '库存预警', icon: 'warning' }
      },
      // {
      //   path: 'stock-check',
      //   name: 'StockCheck',
      //   component: () => import('@/views/stock/StockCheck.vue'),
      //   meta: { title: '库存盘点', icon: 'document' }
      // }
    ]
  },
  {
    path: '/product-detail/:id',
    name: 'ProductDetail',
    component: () => import('@/views/product/ProductDetail.vue')
  },
  {
    path: '/redirect/:path(.*)',
    component: () => import('../views/Redirect.vue')
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/' && !token) {
    next('/');
  } else {
    next();
  }
});

export default router; 