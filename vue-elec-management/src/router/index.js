import { createRouter, createWebHistory } from 'vue-router'

import IndexView from '../views/index/index.vue'
import LogView from '@/views/log/index.vue';
import LoginView from '@/views/login/index.vue';
import CustomerView from '@/views/customer/index.vue';
import OrderView from '@/views/order/index.vue';
import CommodityView from '@/views/commodity/index.vue';
import BrandView from '@/views/brand/index.vue';
import DeliveryView from '@/views/delivery/index.vue';
import CuReportView from '@/views/report/customer/index.vue';
import CoReportView from '@/views/report/commodity/index.vue';

const routes = [
    {path: '/index', component: IndexView },
    {path: '/log', component: LogView },
    {path: '/login', component: LoginView },
    {path: '/customer', component: CustomerView },
    {path: '/order', component: OrderView },
    {path: '/commodity', component: CommodityView },
    {path: '/brand', component: BrandView },
    {path: '/delivery', component: DeliveryView },
    {path: '/customerReport', component: CuReportView },
    {path: '/commodityReport', component: CoReportView },

];

const router = createRouter({
  history: createWebHistory(),//import.meta.env.BASE_URL
  routes,
    // {
    //   path: '/',
    //   name: 'home',
    //   component: HomeView
    // },
    // {
    //   path: '/about',
    //   name: 'about',
    //   component: () => import('../views/AboutView.vue')
    // }
});

export default router
