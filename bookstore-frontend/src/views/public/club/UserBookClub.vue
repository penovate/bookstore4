<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import bookClubService from '@/api/bookClubService.js';
import Swal from 'sweetalert2';

const router = useRouter();
const tab = ref('hosted'); // hosted | participated
const loading = ref(false);
const hostedClubs = ref([]);
const participatedClubs = ref([]); // Placeholder
const search = ref('');

const headers = [
    { title: '讀書會名稱', key: 'clubName', sortable: true },
    { title: '活動時間', key: 'eventDate', sortable: true },
    { title: '狀態', key: 'status', sortable: true },
    { title: '人數', key: 'participants', sortable: true },
    { title: '操作', key: 'actions', sortable: false },
];

const statusMap = {
    0: { text: '審核中', color: 'orange' },
    1: { text: '報名中', color: 'success' },
    2: { text: '已駁回', color: 'error' },
    3: { text: '已額滿', color: 'purple' },
    4: { text: '已截止', color: 'grey' },
    5: { text: '已結束', color: 'brown' },
    6: { text: '已取消', color: 'grey-darken-1' }
};

const statusFilter = ref(null);
const statusOptions = [
    { title: '全部', value: null },
    { title: '審核中', value: 0 },
    { title: '報名中', value: 1 },
    { title: '已結束', value: 5 },
];

const loadHostedClubs = async () => {
    loading.value = true;
    try {
        const response = await bookClubService.getMyHostedClubs();
        hostedClubs.value = response.data;
    } catch (error) {
        console.error('載入失敗', error);
        Swal.fire('錯誤', '無法載入讀書會資料', 'error');
    } finally {
        loading.value = false;
    }
};

const filteredHostedClubs = computed(() => {
    let data = hostedClubs.value;
    if (statusFilter.value !== null) {
        data = data.filter(item => item.status === statusFilter.value);
    }
    return data;
});

const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString();
};

const navigateToInsert = () => {
    router.push('/dev/user/bookclubs/insert');
};

onMounted(() => {
    loadHostedClubs();
});
</script>

<template>
    <v-container class="py-10" style="max-width: 1200px;">
        <div class="d-flex justify-space-between align-center mb-6">
            <h2 class="text-h4 font-weight-bold text-primary">讀書會專區</h2>
            <v-btn color="primary" prepend-icon="mdi-plus" elevation="2" @click="navigateToInsert">
                發起讀書會
            </v-btn>
        </div>

        <v-card class="rounded-lg elevation-2">
            <v-tabs v-model="tab" color="primary" align-tabs="start">
                <v-tab value="hosted">我發起的</v-tab>
                <v-tab value="participated">我參加的</v-tab>
            </v-tabs>

            <v-card-text>
                <v-window v-model="tab">
                    <!-- 我發起的 -->
                    <v-window-item value="hosted">
                        <v-row class="mb-4">
                            <v-col cols="12" sm="4">
                                <v-select v-model="statusFilter" :items="statusOptions" label="狀態篩選" variant="outlined"
                                    density="compact" hide-details></v-select>
                            </v-col>
                            <v-col cols="12" sm="4" offset-sm="4">
                                <v-text-field v-model="search" label="搜尋讀書會" prepend-inner-icon="mdi-magnify"
                                    variant="outlined" density="compact" hide-details></v-text-field>
                            </v-col>
                        </v-row>

                        <v-data-table :headers="headers" :items="filteredHostedClubs" :loading="loading"
                            :search="search" class="forest-table" hover>
                            <template v-slot:item.eventDate="{ item }">
                                {{ formatDate(item.eventDate) }}
                            </template>
                            <template v-slot:item.status="{ item }">
                                <v-chip :color="statusMap[item.status]?.color" size="small" class="font-weight-bold">
                                    {{ statusMap[item.status]?.text || '未知' }}
                                </v-chip>
                            </template>
                            <template v-slot:item.participants="{ item }">
                                {{ item.currentParticipants || 0 }} / {{ item.maxParticipants }}
                            </template>
                            <template v-slot:item.actions="{ item }">
                                <!-- 預留操作按鈕 -->
                                <v-btn v-if="item.status === 1 || item.status === 0" size="small" variant="text"
                                    color="primary">管理</v-btn>
                            </template>
                            <template v-slot:no-data>
                                <div class="text-center py-4 text-grey">目前沒有發起的讀書會</div>
                            </template>
                        </v-data-table>
                    </v-window-item>

                    <!-- 我參加的 -->
                    <v-window-item value="participated">
                        <div class="text-center py-10 text-grey">
                            <v-icon icon="mdi-account-group-outline" size="64" class="mb-4 opacity-50"></v-icon>
                            <h3 class="text-h6">目前尚無參與紀錄</h3>
                            <p>快去參加感興趣的讀書會吧！</p>
                            <!-- TODO: 待後端實作參加功能後串接 -->
                        </div>
                    </v-window-item>
                </v-window>
            </v-card-text>
        </v-card>
    </v-container>
</template>

<style scoped>
.text-primary {
    color: #2E5C43 !important;
}

.forest-table {
    --v-theme-primary: #2E5C43;
}
</style>
