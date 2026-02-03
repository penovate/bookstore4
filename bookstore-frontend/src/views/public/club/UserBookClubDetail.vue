<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import bookClubService from '@/api/bookClubService.js';
import { statusMap } from './UserBookClubConfig.js';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const club = ref({});

const API_BASE_URL = 'http://localhost:8080/bookstore'; // Adjust if needed or import from config

const getImageUrl = (path) => {
    if (!path) return '/default-book-cover.png'; // Placeholder
    if (path.startsWith('http')) return path;
    return `${API_BASE_URL}${path}`;
};

const formatDate = (dateStr) => {
    if (!dateStr) return '未定';
    return new Date(dateStr).toLocaleString();
};

const getStatusInfo = (status) => {
    return statusMap[status] || { text: '未知', color: 'grey' };
};

const getDifficultyLabel = (level) => {
    const map = { 1: '樹苗級', 2: '樹幹級', 3: '巨木級(須提供專業相關證明)' };
    return map[level] || '一般';
};

const getDifficultyColor = (level) => {
    const map = { 1: 'green', 2: 'orange', 3: 'red' };
    return map[level] || 'grey';
};

onMounted(async () => {
    const clubId = route.params.id;
    if (!clubId) {
        Swal.fire('錯誤', '無效的讀書會ID', 'error');
        router.push('/dev/user/bookclubs');
        return;
    }

    try {
        const response = await bookClubService.getClub(clubId);
        club.value = response.data;
        // Ensure clubDetail exists for template safety
        if (!club.value.clubDetail) {
            club.value.clubDetail = {};
        }
    } catch (error) {
        console.error('Failed to load club details:', error);
        Swal.fire('錯誤', '無法載入讀書會資料', 'error');
        router.push('/dev/user/bookclubs');
    } finally {
        loading.value = false;
    }
});
</script>

<template>
    <v-container class="py-10">
        <v-btn variant="text" prepend-icon="mdi-arrow-left" class="mb-4" @click="router.back()">返回列表</v-btn>

        <div v-if="loading" class="d-flex justify-center my-10">
            <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
        </div>

        <template v-else>
            <!-- Header Section -->
            <div class="d-flex align-center justify-space-between mb-6">
                <div>
                    <h1 class="text-h4 font-weight-bold text-primary mb-2">
                        {{ club.clubName }}
                    </h1>
                    <div class="d-flex align-center gap-2">
                        <v-chip :color="getStatusInfo(club.status).color" class="mr-2" label>
                            {{ getStatusInfo(club.status).text }}
                        </v-chip>
                        <v-chip variant="outlined" color="secondary" size="small">
                            {{ club.categoriesBean?.categoryName || '未分類' }}
                        </v-chip>
                    </div>
                </div>
                <!-- Future actions like 'Apply' could go here -->
            </div>

            <v-row>
                <!-- Left Column: Book Info & Visuals -->
                <v-col cols="12" md="4">
                    <v-card class="rounded-lg elevation-2 h-100">
                        <v-img :src="getImageUrl(club.book?.bookImage)" cover height="400" class="bg-grey-lighten-2">
                            <template v-slot:placeholder>
                                <div class="d-flex align-center justify-center fill-height">
                                    <v-icon icon="mdi-book-open-page-variant" size="64" color="grey"></v-icon>
                                </div>
                            </template>
                        </v-img>
                        <v-card-text>
                            <h3 class="text-h6 font-weight-bold mb-1">{{ club.book?.bookName || '自選書籍/未指定' }}</h3>
                            <div class="text-body-2 text-grey-darken-1 mb-3" v-if="club.externalBookInfo">
                                <v-icon icon="mdi-link" start size="small"></v-icon>
                                外部資訊: {{ club.externalBookInfo }}
                            </div>
                            <div class="text-subtitle-2 mb-2">主辦人</div>
                            <v-list-item class="px-0">
                                <template v-slot:prepend>
                                    <v-avatar color="primary" size="40">
                                        <span class="text-white">{{ club.host?.userName?.charAt(0) || 'U' }}</span>
                                    </v-avatar>
                                </template>
                                <v-list-item-title class="font-weight-bold">{{ club.host?.userName || '未知用戶' }}
                                </v-list-item-title>
                                <v-list-item-subtitle>{{ club.host?.email }}</v-list-item-subtitle>
                            </v-list-item>
                        </v-card-text>
                    </v-card>
                </v-col>

                <!-- Right Column: Club Details -->
                <v-col cols="12" md="8">
                    <v-card class="rounded-lg elevation-2 h-100">
                        <v-card-text class="pa-6">
                            <!-- Key Info Grid -->
                            <v-row class="mb-6">
                                <v-col cols="12" sm="6">
                                    <div class="d-flex align-start mb-4">
                                        <v-icon icon="mdi-calendar-clock" color="primary" class="mt-1 mr-3"
                                            size="large"></v-icon>
                                        <div>
                                            <div class="text-caption text-grey">活動時間</div>
                                            <div class="text-subtitle-1 font-weight-medium">{{ formatDate(club.eventDate) }}
                                            </div>
                                        </div>
                                    </div>
                                </v-col>
                                <v-col cols="12" sm="6">
                                    <div class="d-flex align-start mb-4">
                                        <v-icon icon="mdi-clock-alert" color="error" class="mt-1 mr-3"
                                            size="large"></v-icon>
                                        <div>
                                            <div class="text-caption text-grey">報名截止</div>
                                            <div class="text-subtitle-1 font-weight-medium">{{ formatDate(club.deadline) }}
                                            </div>
                                        </div>
                                    </div>
                                </v-col>
                                <v-col cols="12" sm="6">
                                    <div class="d-flex align-start mb-4">
                                        <v-icon icon="mdi-map-marker" color="primary" class="mt-1 mr-3"
                                            size="large"></v-icon>
                                        <div>
                                            <div class="text-caption text-grey">活動地點</div>
                                            <div class="text-subtitle-1 font-weight-medium">{{ club.location || '線上/未定' }}
                                            </div>
                                        </div>
                                    </div>
                                </v-col>
                                <v-col cols="12" sm="6">
                                    <div class="d-flex align-start mb-4">
                                        <v-icon icon="mdi-account-group" color="primary" class="mt-1 mr-3"
                                            size="large"></v-icon>
                                        <div>
                                            <div class="text-caption text-grey">人數限制</div>
                                            <div class="text-subtitle-1 font-weight-medium">
                                                {{ club.currentParticipants || 0 }} / {{ club.maxParticipants }} 人
                                            </div>
                                        </div>
                                    </div>
                                </v-col>
                            </v-row>

                            <v-divider class="mb-6"></v-divider>

                            <!-- Rich Content -->
                            <div class="mb-6">
                                <h3 class="text-h6 font-weight-bold text-primary mb-3">
                                    <v-icon icon="mdi-target" start></v-icon>
                                    活動宗旨
                                </h3>
                                <div class="text-body-1 text-grey-darken-3 bg-grey-lighten-4 pa-4 rounded">
                                    {{ club.clubDetail?.purpose || '暫無宗旨' }}
                                </div>
                            </div>

                            <div class="mb-6">
                                <h3 class="text-h6 font-weight-bold text-primary mb-3">
                                    <v-icon icon="mdi-format-list-bulleted" start></v-icon>
                                    活動議程
                                </h3>
                                <div class="text-body-1 text-pre-wrap">{{ club.clubDetail?.agenda || '暫無議程' }}</div>
                            </div>

                            <v-row class="mt-2">
                                <v-col cols="12" md="6">
                                    <v-alert icon="mdi-alert-circle-outline" border="start" border-color="primary"
                                        class="h-100">
                                        <div class="text-subtitle-2 font-weight-bold mb-1">參加條件</div>
                                        <div class="text-body-2">{{ club.clubDetail?.requirement || '無特殊限制' }}</div>
                                    </v-alert>
                                </v-col>
                                <v-col cols="12" md="6">
                                    <v-alert icon="mdi-stairs" :color="getDifficultyColor(club.clubDetail?.diffultLevel)" variant="tonal" class="h-100">
                                        <div class="text-subtitle-2 font-weight-bold mb-1">難度等級</div>
                                        <div class="text-body-2">
                                            {{ getDifficultyLabel(club.clubDetail?.diffultLevel) }}
                                        </div>
                                    </v-alert>
                                </v-col>
                            </v-row>

                        </v-card-text>
                    </v-card>
                </v-col>
            </v-row>
        </template>
    </v-container>
</template>

<style scoped>
.text-pre-wrap {
    white-space: pre-wrap;
}
.gap-2 {
    gap: 8px;
}
</style>
