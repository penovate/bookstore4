<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import bookClubService from '@/api/bookClubService.js';
import bookService from '@/api/bookService.js';
import Swal from 'sweetalert2';

const router = useRouter();
const categories = ref([]);
const books = ref([]);
const loading = ref(false);
const termsDialog = ref(false);
const termsAgreed = ref(false);

const club = ref({
    clubName: '',
    categoryId: null,
    bookId: null,
    eventDate: '',
    deadline: '',
    location: '',
    maxParticipants: null,
    externalBookInfo: '',
});

const proposalFile = ref(null);
const proofFile = ref(null);

const rules = {
    required: value => !!value || '此欄位為必填',
    positive: value => value > 0 || '數值必須大於 0',
    dateFuture: value => {
        if (!value) return true;
        return new Date(value) > new Date() || '日期必須在未來';
    },
    deadlineBeforeEvent: () => {
        if (!club.value.eventDate || !club.value.deadline) return true;
        return new Date(club.value.deadline) < new Date(club.value.eventDate) || '截止日期必須在活動日期之前';
    }
};

const form = ref(null);

onMounted(async () => {
    try {
        const [catResponse, bookResponse] = await Promise.all([
            bookClubService.getClubCategories(),
            bookService.getAllBooks()
        ]);
        categories.value = catResponse.data;
        books.value = bookResponse.data;
    } catch (error) {
        console.error('Failed to load initial data:', error);
        Swal.fire('錯誤', '載入初始資料失敗', 'error');
    }
});

const submit = async () => {
    const { valid } = await form.value.validate();
    if (!valid) return;

    if (!termsAgreed.value) {
        Swal.fire('提示', '請先詳閱並同意讀書會活動規範', 'warning');
        return;
    }

    if (!proposalFile.value) {
        Swal.fire('提示', '請上傳活動企劃書', 'warning');
        return;
    }

    loading.value = true;

    const clubData = {
        clubName: club.value.clubName,
        location: club.value.location,
        eventDate: club.value.eventDate,
        deadline: club.value.deadline,
        maxParticipants: club.value.maxParticipants,
        externalBookInfo: club.value.externalBookInfo,
        categoriesBean: { categoryId: club.value.categoryId },
        book: club.value.bookId ? { bookId: club.value.bookId } : null
    };

    try {
        await bookClubService.createClub(clubData, proposalFile.value, proofFile.value);

        await Swal.fire({
            title: '申請成功！',
            text: '您的讀書會申請已送出，管理員審核通過後即可開始報名。',
            icon: 'success',
            confirmButtonColor: '#2E5C43'
        });

        router.push('/dev/user/bookclubs');
    } catch (error) {
        console.error(error);
        Swal.fire({
            icon: 'error',
            title: '申請失敗',
            text: error.message || '發生錯誤，請稍後再試。',
            confirmButtonColor: '#d33'
        });
    } finally {
        loading.value = false;
    }
};
</script>

<template>
    <v-container class="py-10" style="max-width: 900px;">
        <v-btn variant="text" prepend-icon="mdi-arrow-left" class="mb-4" @click="router.back()">返回列表</v-btn>
        <h2 class="text-h4 font-weight-bold text-primary mb-6">發起讀書會</h2>

        <v-card class="rounded-lg elevation-2 pa-6">
            <v-alert type="info" variant="tonal" class="mb-6" border="start">
                歡迎發起讀書會！請詳細填寫以下資訊，我們將在收到申請後的三個工作天內進行審核。
            </v-alert>

            <v-form ref="form" @submit.prevent="submit">
                <v-row>
                    <v-col cols="12">
                        <v-text-field v-model="club.clubName" label="讀書會名稱" :rules="[rules.required]" variant="outlined"
                            color="primary"></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                        <v-select v-model="club.categoryId" :items="categories" item-title="categoryName"
                            item-value="categoryId" label="分類" :rules="[rules.required]" variant="outlined"
                            color="primary"></v-select>
                    </v-col>

                    <v-col cols="12" md="6">
                        <v-autocomplete v-model="club.bookId" :items="books" item-title="bookName" item-value="bookId"
                            label="選讀書籍 (可搜尋)" variant="outlined" color="primary" clearable></v-autocomplete>
                    </v-col>

                    <v-col cols="12">
                        <v-text-field v-model="club.externalBookInfo" label="外部書籍資訊 (若非站內書籍請在此說明)" variant="outlined"
                            color="primary"></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                        <v-text-field v-model="club.eventDate" label="活動日期時間" type="datetime-local"
                            :rules="[rules.required, rules.dateFuture]" variant="outlined"
                            color="primary"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="6">
                        <v-text-field v-model="club.deadline" label="報名截止時間" type="datetime-local"
                            :rules="[rules.required, rules.dateFuture, rules.deadlineBeforeEvent]" variant="outlined"
                            color="primary"></v-text-field>
                    </v-col>

                    <v-col cols="12" md="8">
                        <v-text-field v-model="club.location" label="活動地點 / 線上連結" :rules="[rules.required]"
                            variant="outlined" color="primary"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="4">
                        <v-text-field v-model.number="club.maxParticipants" label="人數上限" type="number"
                            :rules="[rules.required, rules.positive]" variant="outlined" color="primary"></v-text-field>
                    </v-col>

                    <v-col cols="12">
                        <v-file-input v-model="proposalFile" label="上傳活動企劃書 (PDF/Word)"
                            prepend-icon="mdi-file-document-outline" variant="outlined" :rules="[rules.required]"
                            show-size hint="請上傳詳細的讀書會流程與內容規劃"></v-file-input>
                    </v-col>
                </v-row>

                <v-divider class="my-6"></v-divider>

                <div class="d-flex align-center mb-6">
                    <v-checkbox v-model="termsAgreed" color="primary" hide-details class="mr-2">
                        <template v-slot:label>
                            <div>
                                我已閱讀並同意
                                <a href="#" @click.prevent="termsDialog = true" class="text-primary font-weight-bold">
                                    讀書會活動規範與條款
                                </a>
                            </div>
                        </template>
                    </v-checkbox>
                </div>

                <v-btn type="submit" color="primary" size="large" block :loading="loading" :disabled="!termsAgreed">
                    提交申請
                </v-btn>
            </v-form>
        </v-card>

        <!-- 條款 Dialog -->
        <v-dialog v-model="termsDialog" max-width="600px" scrollable>
            <v-card>
                <v-card-title class="text-h5 font-weight-bold pa-4 bg-primary text-white">
                    讀書會活動規範
                </v-card-title>
                <v-card-text class="pa-4" style="max-height: 400px;">
                    <ol class="pl-4">
                        <li class="mb-2">發起人需確保活動內容不違反法律法規及社會善良風俗。</li>
                        <li class="mb-2">活動期間需尊重所有參與者，禁止任何形式的騷擾與歧視。</li>
                        <li class="mb-2">如需取消活動，請至少於活動前三天通知所有參與者並取消活動。</li>
                        <li class="mb-2">為了維護良好的閱讀環境，請準時開始與結束活動。</li>
                        <li class="mb-2">平台僅提供媒合服務，不介入線下活動的糾紛處理。</li>
                        <li class="mb-2">嚴禁利用讀書會進行推銷、直銷或其他商業營利行為。</li>
                    </ol>
                </v-card-text>
                <v-card-actions class="pa-4 pt-0">
                    <v-spacer></v-spacer>
                    <v-btn color="primary" variant="text" @click="termsDialog = false">關閉</v-btn>
                    <v-btn color="primary" variant="elevated" @click="termsDialog = false; termsAgreed = true">
                        我同意
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<style scoped>
.text-primary {
    color: #2E5C43 !important;
}

.bg-primary {
    background-color: #2E5C43 !important;
}
</style>
