<script setup>
// import { HomeFilled } from '@element-plus/icons-vue';
// import router from '../../router';


</script>

<template>
  <div class="common-layout">
    <el-container>
      <!-- Header 区域 -->
      <el-header class="header">
        <span class="title">电商管理辅助系统</span>
        <span class="right_tool">
          <a href="">
            <el-icon><EditPen /></el-icon> 修改密码 &nbsp;&nbsp;&nbsp; |  &nbsp;&nbsp;&nbsp;
          </a>
          <a href="">
            <el-icon><SwitchButton /></el-icon> 退出登录
          </a>
        </span>
      </el-header>
      
      <el-container>
        <!-- 左侧菜单 -->
        <el-aside width="200px" class="aside">
          <el-menu router>
              <!-- 首页菜单 -->
              <el-menu-item index="/index">
                <el-icon><Promotion /></el-icon> 首页
              </el-menu-item>
              
              <!-- 客户管理菜单 -->
              <el-sub-menu index="/manage">
                <template #title>
                  <el-icon><Menu /></el-icon> 客户信息管理
                </template>
                <!-- <el-menu-item index="/clazz">
                  <el-icon><HomeFilled /></el-icon>客户管理
                </el-menu-item> -->
                <el-menu-item index="/customer">
                  <el-icon><UserFilled /></el-icon>客户管理
                </el-menu-item>
              </el-sub-menu>
              
              <!-- 系统信息管理 -->
              <el-sub-menu index="/system">
                <template #title>
                  <el-icon><Tools /></el-icon>商品管理
                </template>
                <el-menu-item index="/brand">
                  <el-icon><HelpFilled /></el-icon>品牌管理
                </el-menu-item>
                <el-menu-item index="/commodity">
                  <el-icon><HomeFilled /></el-icon>商品管理
                </el-menu-item>
              </el-sub-menu>

              <!-- 系统信息管理 -->
              <el-sub-menu index="/system2">
                <template #title>
                  <el-icon><Tools /></el-icon>订单配送管理
                </template>
                <el-menu-item index="/order">
                  <el-icon><HelpFilled /></el-icon>订单管理
                </el-menu-item>
                <el-menu-item index="/delivery">
                  <el-icon><Avatar /></el-icon>配送管理
                </el-menu-item>
              </el-sub-menu>
              
              <!-- 数据统计管理 -->
              <el-sub-menu index="/report">
                <template #title>
                  <el-icon><Histogram /></el-icon>数据统计管理
                </template>
                <el-menu-item index="/customerReport">
                  <el-icon><InfoFilled /></el-icon>客户信息统计
                </el-menu-item>
                <el-menu-item index="/commodityReport">
                  <el-icon><Share /></el-icon>商品信息统计
                </el-menu-item>
              </el-sub-menu>
          </el-menu>           
        </el-aside>
        
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
      
    </el-container>
  </div>
</template>

<style scoped>
.header {
  /* background-image: linear-gradient(to right, #00547d, #007fa4, #00aaa0, #00d072, #a8eb12); */
  background-image: linear-gradient(to right, #81c784,#a5d6a7, #e8f5e9, #c8e6c9);
}

.title {
  color: white;
  font-size: 40px;
  font-family: 楷体;
  line-height: 60px;
  font-weight: bolder;
}

.right_tool{
  float: right;
  line-height: 60px;
}

a {
  color: white;
  text-decoration: none;
}

.aside {
  width: 220px;
  border-right: 1px solid #ccc;
  height: 730px;
}
</style>
