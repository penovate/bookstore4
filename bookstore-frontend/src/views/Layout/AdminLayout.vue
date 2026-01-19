<script setup>
import { ref } from 'vue'
const drawer = ref(true)

const items = ref([
    {
        title: '會員管理',
        icon: 'mdi-account-group',
        // to: '/dev/admin/users',
    },
    {
        title: '書籍管理',
        icon: 'mdi-book-open-page-variant',
        children: [
            { title: '書籍列表', to: '/dev/admin/books', icon: 'mdi-format-list-bulleted' },
            { title: '進退貨管理', to: '/dev/admin/logs', icon: 'mdi-truck' },
            { title: '數據報表分析', icon: 'mdi-chart-bar', to: '/dev/admin/reports' },
        ]
    },
    {
        title: '訂單管理',
        icon: 'mdi-clipboard-list-outline',
        // to: '/dev/admin/orders',
    },
    {
        title: '評價管理',
        icon: 'mdi-star-half-full',
        // to: '/dev/admin/reviews',
    },
    {
        title: '讀書會管理',
        icon: 'mdi-book-multiple',
        to: '/dev/admin/bookclubs',
    },
]);
</script>
<template>
    <v-layout class="rounded rounded-md">

        <v-navigation-drawer v-model="drawer" color="primary">
            <!-- 列表頂部的標題區域 -->
            <v-list-item title="BookStore" subtitle="後台管理系統" class="py-4">
                <template v-slot:prepend>
                    <!-- 這裡可以放 Logo 圖片 -->
                    <v-icon icon="mdi-leaf" class="me-2"></v-icon>
                </template>
            </v-list-item>
            <v-divider></v-divider>
            <!-- 選單列表 -->
            <v-list density="compact" nav>
                <template v-for="(item, i) in items" :key="i">
                    <!-- 如果有子選單 -->
                    <v-list-group v-if="item.children" :value="item.title">
                        <template v-slot:activator="{ props }">
                            <v-list-item v-bind="props" :prepend-icon="item.icon" :title="item.title"></v-list-item>
                        </template>

                        <v-list-item v-for="(child, k) in item.children" :key="k" :title="child.title"
                            :prepend-icon="child.icon" :to="child.to" :value="child.title" color="accent"></v-list-item>
                    </v-list-group>

                    <!-- 如果沒有子選單 -->
                    <v-list-item v-else :value="item" :to="item.to" :prepend-icon="item.icon" color="accent">
                        <v-list-item-title v-text="item.title"></v-list-item-title>
                    </v-list-item>
                </template>
            </v-list>

            <!-- 底部登出區 -->
            <template v-slot:append>
                <div class="pa-2">
                    <v-btn block color="secondary">
                        登出
                    </v-btn>
                </div>
            </template>
        </v-navigation-drawer>

        <v-app-bar elevation="0" class="bg-background" density="compact">
            <!--漢堡選單按鈕：點擊切換側邊欄 -->
            <v-app-bar-nav-icon variant="text" color="primary" @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
            <v-spacer></v-spacer>
            <!-- 右側功能區 -->
            <v-btn icon="mdi-bell-outline" color="secondary" variant="text"></v-btn>
            <v-avatar class="mx-2" size="32" color="secondary">
                <span class="text-white text-caption">Admin</span>
            </v-avatar>
        </v-app-bar>

        <v-main class="bg-background" style="min-height: 100vh;">
            <v-container fluid class="pa-6">
                <!-- 路由插槽：Home.vue 或其他頁面會顯示在這裡 -->
                <router-view></router-view>
            </v-container>
        </v-main>
    </v-layout>
</template>

<style scoped>
/* 覆寫 Vuetify 預設的縮排 */
:deep(.v-list-group__items .v-list-item) {
    padding-inline-start: 16px !important;
    /* 調整此數值以改變縮排寬度 (原預設較大) */
}
</style>
