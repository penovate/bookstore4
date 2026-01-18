<script setup>
import { ref } from 'vue'
import { useDisplay } from 'vuetify'

const { mobile } = useDisplay()
const drawer = ref(false) // Mobile drawer state

const menuItems = ref([
    { title: '首頁', to: '/dev/user', icon: 'mdi-home' },

    { title: '書籍專區', to: '/dev/user/books', icon: 'mdi-book-open-page-variant' }, // 假設路由
    { title: '會員中心', to: '', icon: 'mdi-book-open-page-variant' }, // 假設路由
    { title: '讀書會', to: '', icon: 'mdi-book-open-page-variant' }, // 假設路由
    { title: '關於我們', to: '', icon: 'mdi-information' },

]);

const user = ref({
    name: '訪客',
    avatar: '',
    isLoggedIn: false
})

</script>

<template>
    <!-- 使用 forestTheme 以符合 AdminLayout 配色風格，或可保留自定義 colors 但風格統一 -->
    <v-app theme="forestTheme">

        <!-- 頂部導航列 (App Bar) -->
        <v-app-bar color="primary" elevation="2" class="px-md-4">
            <!-- Mobile Menu Icon -->
            <v-app-bar-nav-icon variant="text" @click.stop="drawer = !drawer" class="d-md-none"></v-app-bar-nav-icon>

            <!-- Logo / Brand -->
            <v-toolbar-title class="font-weight-bold text-h5 cursor-pointer" @click="$router.push('/dev/user')">
                <v-icon icon="mdi-book-open-variant" class="me-2"></v-icon>
                網路書籍商城
            </v-toolbar-title>

            <v-spacer></v-spacer>

            <!-- Desktop Navigation Links (Centered) -->
            <div class="d-none d-md-flex align-center">
                <v-btn v-for="item in menuItems" :key="item.title" :to="item.to" variant="text"
                    class="mx-1 text-subtitle-1 font-weight-medium">
                    {{ item.title }}
                </v-btn>
            </div>

            <v-spacer></v-spacer>

            <!-- Right Side Icons: Search, Cart, User -->
            <div class="d-flex align-center">
                <v-btn icon="mdi-magnify" variant="text"></v-btn>

                <v-btn icon class="me-2">
                    <v-badge content="2" color="accent">
                        <v-icon icon="mdi-cart-outline"></v-icon>
                    </v-badge>
                </v-btn>

                <div v-if="user.isLoggedIn" class="d-flex align-center">
                    <v-menu open-on-hover>
                        <template v-slot:activator="{ props }">
                            <v-avatar color="surface" size="36" class="cursor-pointer" v-bind="props">
                                <span class="text-primary font-weight-bold">{{ user.name.charAt(0) }}</span>
                            </v-avatar>
                        </template>
                        <v-list density="compact">
                            <v-list-item prepend-icon="mdi-account" title="會員中心" to="/profile"></v-list-item>
                            <v-list-item prepend-icon="mdi-clipboard-list" title="我的訂單" to="/orders"></v-list-item>
                            <v-divider></v-divider>
                            <v-list-item prepend-icon="mdi-logout" title="登出"></v-list-item>
                        </v-list>
                    </v-menu>
                </div>
                <div v-else>
                    <v-btn variant="outlined" color="surface" class="ms-2" to="/login">
                        登入 / 註冊
                    </v-btn>
                </div>
            </div>
        </v-app-bar>

        <!-- Mobile Navigation Drawer -->
        <v-navigation-drawer v-model="drawer" temporary location="left">
            <v-list>
                <v-list-item title="導覽選單" subtitle="BookStore"></v-list-item>
                <v-divider></v-divider>
                <v-list-item v-for="item in menuItems" :key="item.title" :to="item.to" :prepend-icon="item.icon"
                    :title="item.title"></v-list-item>
            </v-list>
        </v-navigation-drawer>

        <!-- Main Content (有 padding-top 避免被 App Bar 遮擋) -->
        <v-main class="bg-background">
            <v-container class="py-6" style="min-height: 80vh;">
                <router-view></router-view>
            </v-container>
        </v-main>

        <!-- Footer -->
        <v-footer color="primary" class="d-flex flex-column py-6">
            <div class="d-flex w-100 align-center px-4">
                <strong class="text-h6">與我們保持聯繫，獲取最新好書資訊！</strong>
                <v-spacer></v-spacer>
                <v-btn v-for="icon in ['mdi-facebook', 'mdi-twitter', 'mdi-instagram']" :key="icon" :icon="icon"
                    class="mx-2" variant="text"></v-btn>
            </div>
            <v-divider class="w-100 my-4 border-opacity-25"></v-divider>
            <div class="text-center w-100 text-body-2">
                {{ new Date().getFullYear() }} — <strong>BookStore Inc.</strong>
            </div>
        </v-footer>

    </v-app>
</template>

<style scoped>
/* 可在此微調 navbar 樣式 */
.v-btn {
    text-transform: none;
    /* 防止按鈕文字全大寫 */
}
</style>
