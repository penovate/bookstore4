<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import bookClubService from '@/api/bookClubService.js';
import { statusMap } from '../../public/club/UserBookClubConfig.js';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const club = ref({});

// Reject Dialog
const showRejectDialog = ref(false);
const rejectionReason = ref('');

const API_BASE_URL = 'http://localhost:8080/bookstore';

const getImageUrl = (path) => {
    if (!path) return '/default-book-cover.png';
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
    const map = { 1: '樹苗級', 2: '樹幹級', 3: '巨木級' };
    return map[level] || '一般';
};

const getDifficultyColor = (level) => {
    const map = { 1: 'green', 2: 'orange', 3: 'red' };
    return map[level] || 'grey';
};

const loadClub = async () => {
    const clubId = route.params.id;
    if (!clubId) return;

    try {
        const response = await bookClubService.getClub(clubId);
        club.value = response.data;
        if (!club.value.clubDetail) {
            club.value.clubDetail = {};
        }
    } catch (error) {
        console.error('Failed to load club details:', error);
        Swal.fire('錯誤', '無法載入讀書會資料', 'error');
        router.push('/dev/admin/bookclubs');
    } finally {
        loading.value = false; // Fixed: Set loading to false regardless of success/fail
    }
};

const handleApprove = async () => {
    const result = await Swal.fire({
        title: '確定核准？',
        text: "此讀書會將開放報名",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#2E5C43',
        confirmButtonText: '確定核准'
    });

    if (result.isConfirmed) {
        try {
            loading.value = true;
            await bookClubService.approveClub(club.value.clubId);
            await Swal.fire('成功', '讀書會已核准', 'success');
            loadClub(); // Reload to show new status
        } catch (error) {
             Swal.fire('錯誤', '核准失敗', 'error');
        } finally {
            loading.value = false;
        }
    }
};

const openRejectDialog = () => {
    rejectionReason.value = '';
    showRejectDialog.value = true;
};

const confirmReject = async () => {
    if (!rejectionReason.value.trim()) {
        Swal.fire('提示', '請填寫駁回原因', 'warning');
        return;
    }

    try {
        loading.value = true;
        await bookClubService.rejectClub(club.value.clubId, rejectionReason.value);
        showRejectDialog.value = false;
        await Swal.fire('已駁回', '讀書會已駁回', 'success');
        loadClub(); // Reload
    } catch (error) {
        Swal.fire('錯誤', '駁回失敗', 'error');
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    loadClub();
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
                        審核讀書會: {{ club.clubName }}
                    </h1>
                    <div class="d-flex align-center gap-2">
                        <v-chip :color="getStatusInfo(club.status).color" class="mr-2" label size="large">
                            {{ getStatusInfo(club.status).text }}
                        </v-chip>
                        <v-chip variant="outlined" color="secondary">
                            {{ club.categoriesBean?.categoryName || '未分類' }}
                        </v-chip>
                    </div>
                </div>
                
                <!-- Action Buttons (Only for Pending Status usually) -->
                <div v-if="club.status === 0" class="d-flex gap-4">
                    <v-btn color="error" variant="outlined" size="large" prepend-icon="mdi-close" @click="openRejectDialog">
                        駁回申請
                    </v-btn>
                    <v-btn color="success" size="large" prepend-icon="mdi-check" @click="handleApprove" class="ml-4">
                        核准申請
                    </v-btn>
                </div>
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
                             <div class="text-subtitle-2 mb-2 mt-4">主辦會員</div>
                            <v-list-item class="px-0">
                                <template v-slot:prepend>
                                    <v-avatar color="primary" size="40">
                                        <span class="text-white">{{ club.host?.userName?.charAt(0) || 'U' }}</span>
                                    </v-avatar>
                                </template>
                                <v-list-item-title class="font-weight-bold">{{ club.host?.userName || '未知用戶' }}
                                </v-list-item-title>
                                <v-list-item-subtitle>ID: {{ club.host?.userId }}</v-list-item-subtitle>
                                <v-list-item-subtitle>{{ club.host?.email }}</v-list-item-subtitle>
                            </v-list-item>
                            
                            <v-divider class="my-3"></v-divider>
                            <div v-if="club.clubDetail?.proofPath" class="mt-2">
                                <v-btn block color="info" variant="tonal" prepend-icon="mdi-file-document" 
                                    :href="`${API_BASE_URL}${club.clubDetail?.proofPath}`" target="_blank">
                                    查看佐證資料
                                </v-btn>
                            </div>
                        </v-card-text>
                    </v-card>
                </v-col>

                <!-- Right Column: Club Details -->
                <v-col cols="12" md="8">
                    <v-card class="rounded-lg elevation-2 h-100">
                        <v-card-text class="pa-6">
                            <!-- Basic Info -->
                            <v-row class="mb-6">
                                <v-col cols="12" sm="6">
                                    <div class="text-caption text-grey">活動時間</div>
                                    <div class="text-subtitle-1 font-weight-medium">{{ formatDate(club.eventDate) }}</div>
                                </v-col>
                                <v-col cols="12" sm="6">
                                     <div class="text-caption text-grey">主辦地點</div>
                                    <div class="text-subtitle-1 font-weight-medium">{{ club.location || '線上/未定' }}</div>
                                </v-col>
                                <v-col cols="12" sm="6">
                                     <div class="text-caption text-grey">難度</div>
                                     <v-chip :color="getDifficultyColor(club.clubDetail?.diffultLevel)" size="small" class="mt-1">
                                        {{ getDifficultyLabel(club.clubDetail?.diffultLevel) }}
                                     </v-chip>
                                </v-col>
                            </v-row>

                            <v-divider class="mb-6"></v-divider>

                            <!-- Rich Content -->
                            <div class="mb-6">
                                <h3 class="text-h6 font-weight-bold text-primary mb-2">活動宗旨</h3>
                                <div class="text-body-1 pa-3 bg-grey-lighten-4 rounded">
                                    {{ club.clubDetail?.purpose || '暫無宗旨' }}
                                </div>
                            </div>

                            <div class="mb-6">
                                <h3 class="text-h6 font-weight-bold text-primary mb-2">活動議程</h3>
                                <div class="text-body-1 text-pre-wrap">{{ club.clubDetail?.agenda || '暫無議程' }}</div>
                            </div>
                            
                            <div v-if="club.rejectionReason" class="mb-6">
                                <v-alert type="error" variant="tonal" border="start" title="駁回原因">
                                    {{ club.rejectionReason }}
                                </v-alert>
                            </div>

                        </v-card-text>
                    </v-card>
                </v-col>
            </v-row>
        </template>
        
        <!-- Reject Dialog -->
        <v-dialog v-model="showRejectDialog" max-width="500px">
            <v-card>
                <v-card-title class="bg-error text-white">駁回申請</v-card-title>
                <v-card-text class="pt-4">
                    <v-textarea v-model="rejectionReason" label="請輸入駁回原因" 
                        hint="此原因將顯示給申請者" persistent-hint variant="outlined" rows="3" auto-grow></v-textarea>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="grey" variant="text" @click="showRejectDialog = false">取消</v-btn>
                    <v-btn color="error" @click="confirmReject">確認駁回</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        
    </v-container>
</template>

<style scoped>
.text-pre-wrap {
    white-space: pre-wrap;
}
.gap-2 { gap: 8px; }
.gap-4 { gap: 16px; }
</style>
