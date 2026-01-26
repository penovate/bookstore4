<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import bookClubService from '@/api/bookClubService.js';
import { useUserStore } from '@/stores/userStore';
import Swal from 'sweetalert2';
// Import Registration Details Component (Will create next)
import RegistrationDetails from './RegistrationDetails.vue';

const router = useRouter();
const userStore = useUserStore();
const tab = ref('all'); // all | hosted | participated
const loading = ref(false);
const allClubs = ref([]); // 所有讀書會
const hostedClubs = ref([]); // 我發起的
const participatedClubs = ref([]); // 我參加的
const myRegistrationIds = ref(new Set()); // 我報名的讀書會ID集合
const search = ref('');

// Modal Control
const showDetailsModal = ref(false);
const selectedClubId = ref(null);

const headers = [
    { title: '讀書會名稱', key: 'clubName', sortable: true },
    { title: '活動時間', key: 'eventDate', sortable: true },
    { title: '狀態', key: 'status', sortable: true },
    { title: '人數', key: 'participants', sortable: true },
    { title: '地點', key: 'location', sortable: false },
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

// Helper: Check if user is the host
const isHost = (club) => {
    return userStore.userId && club.host && club.host.userId === userStore.userId;
};

// 載入我參加的讀書會 (用於判斷狀態)
const loadMyRegistrations = async () => {
    if (!userStore.isLoggedIn) return;
    try {
        const response = await bookClubService.getMyRegistrations();
        if (response.data) {
            // Backend returns List<ClubRegistrationsBean>
            // Filter out cancelled (status=2) if needed, but backend service logic suggests status=1 is active
            const activeRegs = response.data.filter(r => r.status === 1);
            participatedClubs.value = activeRegs.map(r => {
                // Enhance club object with registration info if needed
                return { ...r.bookClub, regStatus: r.status };
            });

            // Update Set for O(1) lookup
            myRegistrationIds.value = new Set(activeRegs.map(r => r.bookClub.clubId));
        }
    } catch (error) {
        console.error('載入報名資料失敗', error);
    }
};

// 載入所有讀書會
const loadAllClubs = async () => {
    loading.value = true;
    try {
        // 先載入報名狀態
        await loadMyRegistrations();

        const response = await bookClubService.getAllClubs();
        // 只顯示狀態為 1 (報名中) 的讀書會
        allClubs.value = response.data.filter(club => club.status === 1);
    } catch (error) {
        console.error('載入所有讀書會失敗', error);
        Swal.fire('錯誤', '無法載入所有讀書會資料', 'error');
    } finally {
        loading.value = false;
    }
};

// 載入我發起的讀書會
const loadHostedClubs = async () => {
    loading.value = true;
    try {
        const response = await bookClubService.getMyHostedClubs();
        hostedClubs.value = response.data;
    } catch (error) {
        console.error('載入我發起的讀書會失敗', error);
    } finally {
        loading.value = false;
    }
};

// 報名動作
const handleRegister = (club) => {
    Swal.fire({
        title: '確認報名',
        text: `確定要報名「${club.clubName}」嗎？`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '確定報名',
        cancelButtonText: '取消',
        confirmButtonColor: '#2E5C43'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                await bookClubService.register(club.clubId);
                Swal.fire('成功', '報名成功！', 'success');
                // Refresh data
                await loadAllClubs();
            } catch (error) {
                Swal.fire('失敗', error.response?.data?.message || '報名失敗', 'error');
            }
        }
    });
};

// 取消報名動作
const handleCancel = (club) => {
    Swal.fire({
        title: '取消報名',
        text: `確定要取消「${club.clubName}」的報名嗎？`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '確定取消',
        cancelButtonText: '保留',
        confirmButtonColor: '#d33'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                await bookClubService.cancelRegistration(club.clubId);
                Swal.fire('成功', '已取消報名', 'success');
                // Refresh data
                await loadAllClubs();
            } catch (error) {
                Swal.fire('失敗', error.response?.data?.message || '取消失敗', 'error');
            }
        }
    });
};

// 發起人檢視明細
const openDetails = (club) => {
    selectedClubId.value = club.clubId;
    showDetailsModal.value = true;
};

// 根據目前 Tab 決定顯示的資料
const currentClubs = computed(() => {
    if (tab.value === 'hosted') return hostedClubs.value;
    if (tab.value === 'participated') return participatedClubs.value;
    return allClubs.value;
});

const filteredClubs = computed(() => {
    let data = currentClubs.value;
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

// 監聽 Tab 切換以載入對應資料
watch(tab, (newVal) => {
    if (newVal === 'all') {
        if (allClubs.value.length === 0 || myRegistrationIds.value.size === 0) loadAllClubs();
    } else if (newVal === 'hosted') {
        if (hostedClubs.value.length === 0) loadHostedClubs();
    } else if (newVal === 'participated') {
        loadMyRegistrations();
    }
});

onMounted(() => {
    // 預設載入目前 Tab 的資料
    if (tab.value === 'all') loadAllClubs();
    else if (tab.value === 'hosted') loadHostedClubs();
    else if (tab.value === 'participated') loadMyRegistrations();
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
                <v-tab value="all">所有讀書會</v-tab>
                <v-tab value="hosted">我發起的</v-tab>
                <v-tab value="participated">我參加的</v-tab>
            </v-tabs>

            <v-card-text>
                <!-- 共用篩選與搜尋列 -->
                <v-row class="mb-4">
                    <v-col cols="12" sm="4">
                        <v-select v-model="statusFilter" :items="statusOptions" label="狀態篩選" variant="outlined"
                            density="compact" hide-details></v-select>
                    </v-col>
                    <v-col cols="12" sm="4" offset-sm="4">
                        <v-text-field v-model="search" label="搜尋讀書會" prepend-inner-icon="mdi-magnify" variant="outlined"
                            density="compact" hide-details></v-text-field>
                    </v-col>
                </v-row>

                <v-window v-model="tab">
                    <!-- 所有讀書會 -->
                    <v-window-item value="all">
                        <v-data-table :headers="headers" :items="filteredClubs" :loading="loading" :search="search"
                            class="forest-table" hover>
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
                                <!-- 
                                    1. 如果是 Host -> 顯示管理 (或跳轉)
                                    2. 如果已報名 -> 顯示已報名/取消 (Danger Color)
                                    3. 如果未報名 -> 顯示報名 (Primary Color)
                                -->
                                <template v-if="isHost(item)">
                                    <v-chip color="info" size="small" variant="outlined" class="mr-2">我是發起人</v-chip>
                                </template>
                                <template v-else-if="myRegistrationIds.has(item.clubId)">
                                    <v-btn size="small" color="error" variant="flat" @click="handleCancel(item)">
                                        已報名 (取消)
                                    </v-btn>
                                </template>
                                <template v-else>
                                    <v-btn size="small" color="primary" variant="elevated" @click="handleRegister(item)"
                                        :disabled="item.currentParticipants >= item.maxParticipants">
                                        {{ item.currentParticipants >= item.maxParticipants ? '已額滿' : '報名' }}
                                    </v-btn>
                                </template>
                            </template>
                            <template v-slot:no-data>
                                <div class="text-center py-4 text-grey">目前沒有可報名的讀書會</div>
                            </template>
                        </v-data-table>
                    </v-window-item>

                    <!-- 我發起的 -->
                    <v-window-item value="hosted">
                        <v-data-table :headers="headers" :items="filteredClubs" :loading="loading" :search="search"
                            class="forest-table" hover>
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
                                <v-btn size="small" variant="text" color="primary" @click="openDetails(item)">
                                    <v-icon start icon="mdi-clipboard-list"></v-icon>
                                    報名明細
                                </v-btn>
                            </template>
                            <template v-slot:no-data>
                                <div class="text-center py-4 text-grey">目前沒有發起的讀書會</div>
                            </template>
                        </v-data-table>
                    </v-window-item>

                    <!-- 我參加的 -->
                    <v-window-item value="participated">
                        <v-data-table :headers="headers" :items="filteredClubs" :loading="loading" :search="search"
                            class="forest-table" hover>
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
                                <v-btn size="small" color="error" variant="text" @click="handleCancel(item)">
                                    取消報名
                                </v-btn>
                            </template>
                            <template v-slot:no-data>
                                <div class="text-center py-10 text-grey">
                                    <v-icon icon="mdi-account-group-outline" size="64" class="mb-4 opacity-50"></v-icon>
                                    <h3 class="text-h6">目前尚無參與紀錄</h3>
                                    <p>快去參加感興趣的讀書會吧！</p>
                                </div>
                            </template>
                        </v-data-table>
                    </v-window-item>
                </v-window>
            </v-card-text>
        </v-card>

        <!-- Registration Details Modal -->
        <RegistrationDetails v-if="showDetailsModal" v-model="showDetailsModal" :club-id="selectedClubId" />

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
