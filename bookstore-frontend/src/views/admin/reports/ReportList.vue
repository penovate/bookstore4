<template>
  <div class="list-page-wrapper">
    <div class="header-section mb-6 text-left">
      <h2 class="forest-main-title">檢舉列表</h2>
    </div>

    <v-row class="mb-4" align="center">
      <v-col cols="auto"> </v-col>

      <v-spacer></v-spacer>

      <v-col cols="12" md="4">
        <div class="d-flex align-center">
          <v-text-field
            v-model="search"
            label="搜尋檢舉人、書籍或原因..."
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            clearable
            bg-color="white"
            color="primary"
            class="rounded-lg flex-grow-1"
          ></v-text-field>
        </div>
      </v-col>
    </v-row>

    <v-card class="forest-card-table">
      <v-tabs v-model="tab" color="primary" align-tabs="start" class="px-4 border-b">
        <v-tab value="pending" class="font-weight-bold">
          <v-icon start>mdi-inbox-arrow-down</v-icon>
          待處理
          <v-badge
            v-if="pendingCount > 0"
            color="error"
            :content="pendingCount"
            inline
            class="ml-2"
          ></v-badge>
        </v-tab>
        <v-tab value="history" class="font-weight-bold">
          <v-icon start>mdi-history</v-icon>
          歷史紀錄
        </v-tab>
      </v-tabs>

      <v-data-table
        :headers="headers"
        :items="filteredReports"
        :search="search"
        :loading="loading"
        hover
        density="comfortable"
        class="forest-table-style"
      >
        <template v-slot:item.id="{ item: report }">
          <span class="font-weight-bold text-grey-darken-2">{{ report.id }}</span>
        </template>

        <template v-slot:item.bookTitle="{ item: report }">
          <span class="font-weight-bold text-primary">{{ report.bookTitle }}</span>
        </template>

        <template v-slot:item.reviewContent="{ item: report }">
          <div class="review-preview text-grey-darken-3" @click="openDetail(report)">
            {{ report.reviewContent }}
          </div>
        </template>

        <template v-slot:item.reason="{ item: report }">
          <v-chip
            size="small"
            :color="getReportColor(report.reason)"
            variant="tonal"
            class="font-weight-bold"
          >
            {{ getReportLabel(report.reason) }}
          </v-chip>
        </template>

        <template v-slot:item.status="{ item: report }">
          <div class="d-flex justify-center">
            <v-chip
              size="small"
              :color="getStatusColor(report.status)"
              label
              class="font-weight-bold text-caption"
            >
              {{ getStatusText(report.status) }}
            </v-chip>
          </div>
        </template>

        <template v-slot:item.actions="{ item: report }">
          <div class="d-flex justify-center align-center">
            <v-btn
              icon="mdi-eye-outline"
              variant="text"
              size="small"
              color="primary"
              @click="openDetail(report)"
              tooltip="查看詳情"
            ></v-btn>

            <template v-if="report.status === 0">
              <v-btn
                icon="mdi-check-circle-outline"
                variant="text"
                size="small"
                color="success"
                @click="processReport(report, false)"
                title="駁回檢舉"
              ></v-btn>
              <v-btn
                icon="mdi-delete-alert-outline"
                variant="text"
                size="small"
                color="error"
                @click="processReport(report, true)"
                title="成立檢舉"
              ></v-btn>
            </template>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-model="dialog" max-width="600px">
      <v-card class="rounded-lg">
        <v-card-title class="bg-primary text-white d-flex align-center px-6 py-4">
          <span class="font-weight-bold">檢舉詳情 #{{ selectedReport.id }}</span>
          <v-spacer></v-spacer>
          <v-btn
            icon="mdi-close"
            variant="text"
            density="comfortable"
            @click="dialog = false"
          ></v-btn>
        </v-card-title>

        <v-card-text class="pt-6 px-6">
          <v-table density="compact" class="detail-table mb-4">
            <tbody>
              <tr>
                <th class="label-col text-primary font-weight-bold">檢舉原因</th>
                <td>
                  <v-chip :color="getReportColor(selectedReport.reason)" size="small" label>
                    {{ getReportLabel(selectedReport.reason) }}
                  </v-chip>
                </td>
              </tr>
              <tr>
                <th class="label-col text-primary font-weight-bold">檢舉人</th>
                <td>{{ selectedReport.reporterName }}</td>
              </tr>
              <tr>
                <th class="label-col text-primary font-weight-bold">被檢舉人</th>
                <td class="text-error font-weight-bold">{{ selectedReport.reportedName }}</td>
              </tr>
              <tr>
                <th class="label-col text-primary font-weight-bold">書籍名稱</th>
                <td class="font-weight-bold text-grey-darken-3">{{ selectedReport.bookTitle }}</td>
              </tr>
              <tr>
                <th class="label-col text-primary font-weight-bold">檢舉時間</th>
                <td>{{ selectedReport.createdAt }}</td>
              </tr>
            </tbody>
          </v-table>

          <div class="text-subtitle-1 font-weight-bold text-primary mb-2">評價完整內容：</div>
          <div class="pa-4 bg-grey-lighten-4 rounded border text-body-2">
            {{ selectedReport.fullContent }}
          </div>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions class="pa-4 justify-end">
          <template v-if="selectedReport.status === 0">
            <v-btn
              variant="outlined"
              color="success"
              class="font-weight-bold"
              prepend-icon="mdi-check"
              @click="processReport(selectedReport, false)"
            >
              檢舉不成立
            </v-btn>
            <v-btn
              variant="flat"
              color="error"
              class="font-weight-bold ml-2"
              prepend-icon="mdi-gavel"
              @click="processReport(selectedReport, true)"
            >
              檢舉屬實
            </v-btn>
          </template>
          <template v-else>
            <span class="text-caption text-grey mr-3 font-weight-bold"
              >此案件已結案：{{ getStatusText(selectedReport.status) }}</span
            >
            <v-btn color="primary" variant="elevated" @click="dialog = false">關閉</v-btn>
          </template>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import Swal from 'sweetalert2'
import reviewService from '@/api/reviewService.js'
import { getReportLabel, getReportColor } from '@/utils/reportOptions.js'

// --- 資料與狀態 ---
const tab = ref('pending')
const search = ref('')
const loading = ref(false)
const dialog = ref(false)
const selectedReport = ref({})

const headers = [
  { title: '編號', key: 'id', sortable: true, width: '90px', align: 'start' },
  { title: '檢舉人', key: 'reporterName', width: '120px', align: 'center' },
  { title: '被檢舉人', key: 'reportedName', width: '120px' },
  { title: '書籍', key: 'bookTitle', sortable: true, align: 'start' },
  { title: '評價摘要', key: 'reviewContent', sortable: false, align: 'start' },
  { title: '檢舉原因', key: 'reason', sortable: false, width: '120px', align: 'center' },
  { title: '狀態', key: 'status', width: '100px', align: 'center' },
  { title: '審核', key: 'actions', sortable: false, align: 'center', width: '120px' },
]

// 假資料
const reports = ref([])

// 計算屬性
const filteredReports = computed(() => {
  return tab.value === 'pending'
    ? reports.value.filter((r) => r.status === 0) // 0 待處理
    : reports.value.filter((r) => r.status !== 0) // 1、2 已結案
})

const pendingCount = computed(() => {
  if (!reports.value) {
    return 0
  }
  return reports.value.filter((r) => r.status === 0).length
})

// 方法

// 獲取所有檢舉列表
const fetchReports = async () => {
  loading.value = true
  try {
    const response = await reviewService.getAdminReports()
    reports.value = response.data
  } catch (error) {
    console.error('獲取檢舉列表失敗:', error)
    Swal.fire({
      icon: 'error',
      title: '讀取失敗',
      text: '無法載入檢舉資料，請檢查伺服器連線',
      confirmButtonColor: '#2E5C43',
    })
  } finally {
    loading.value = false
  }
}

const openDetail = (item) => {
  selectedReport.value = item
  dialog.value = true
}

const processReport = async (report, isSustain) => {
  const actionText = isSustain ? '成立檢舉並下架評價' : '駁回檢舉並保留評價'
  const confirmColor = isSustain ? '#d32f2f' : '#2E5C43'

  const newStatus = isSustain ? 1 : 2

  const result = await Swal.fire({
    title: `確定要${actionText}嗎？`,
    text: isSustain ? '該評價將在前台隱藏' : '該檢舉將被標記為已處理',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: confirmColor,
    cancelButtonColor: '#aaa',
    confirmButtonText: '確定',
    cancelButtonText: '取消',
  })

  if (result.isConfirmed) {
    try {
      loading.value = true

      await reviewService.updateReportStatus(report.id, newStatus)

      // 更新前端畫面
      const index = reports.value.findIndex((r) => r.id === report.id)
      if (index !== -1) {
        reports.value[index].status = newStatus
      }

      Swal.fire({
        title: '已更新',
        text: '操作成功',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false,
      })

      dialog.value = false
    } catch (error) {
      console.error('更新檢舉狀態失敗:', error)
      Swal.fire('錯誤', '更新失敗，請稍後再試', 'error')
    } finally {
      loading.value = false
    }
  }
}

const getStatusColor = (status) => {
  switch (status) {
    case 0:
      return 'warning'
    case 1:
      return 'error'
    case 2:
      return 'success'
    default:
      return 'grey'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0:
      return '待處理'
    case 1:
      return '已成立'
    case 2:
      return '已駁回'
    default:
      return '未知'
  }
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
/* 頁面容器 */
.list-page-wrapper {
  padding: 0;
  width: 100%;
}

/* 標題樣式 */
.forest-main-title {
  color: #2e5c43;
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 0;
}

/* 卡片與表格樣式 */
.forest-card-table {
  background-color: white !important;
  border-radius: 12px !important;
  border-top: 5px solid #2e5c43 !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05) !important;
}

.forest-table-style {
  :deep(.v-data-table-header) {
    background-color: #f9fbe7 !important;
  }
  :deep(.v-data-table-header__content) {
    font-weight: 800;
    color: #2e5c43;
  }
}

/* 內容預覽樣式 */
.review-preview {
  max-width: 250px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  transition: color 0.2s;
}

.review-preview:hover {
  color: #2e5c43 !important;
  text-decoration: underline;
}

/* Dialog 內表格樣式 */
.detail-table th {
  vertical-align: top;
  padding-top: 10px !important;
  width: 100px;
}
</style>
<style>
/* 強制讓 SweetAlert 浮在 Vuetify Dialog (通常是 2400) 之上 */
div:where(.swal2-container).swal2-center > .swal2-popup {
  z-index: 9999 !important;
}
.swal2-container {
  z-index: 9999 !important;
}
</style>