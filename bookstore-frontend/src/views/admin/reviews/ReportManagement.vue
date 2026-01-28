<template>
  <v-container fluid>
    <div class="d-flex align-center mb-4">
      <h2 class="text-h4 font-weight-bold text-primary">檢舉管理</h2>
      <v-spacer></v-spacer>
      <v-btn prepend-icon="mdi-refresh" variant="text" @click="fetchReports" :loading="loading">
        重新整理
      </v-btn>
    </div>

    <v-card border elevation="1">
      <v-data-table
        :headers="headers"
        :items="reports"
        :loading="loading"
        hover
        density="comfortable"
        no-data-text="目前沒有檢舉紀錄"
      >
        <!-- 狀態欄位 -->
        <template v-slot:item.status="{ item }">
          <v-chip :color="getStatusColor(item.status)" size="small" class="font-weight-medium">
            {{ getStatusText(item.status) }}
          </v-chip>
        </template>

        <!-- 檢舉時間 -->
        <template v-slot:item.createdAt="{ item }">
          {{ formatDate(item.createdAt) }}
        </template>

        <!-- 檢舉原因 -->
        <template v-slot:item.reason="{ item }">
          <v-chip size="x-small" label>{{ getReasonLabel(item.reason) }}</v-chip>
        </template>

        <!-- 操作按鈕 -->
        <template v-slot:item.actions="{ item }">
          <div v-if="item.status === 0" class="d-flex gap-2">
            <v-btn
              size="small"
              color="error"
              variant="flat"
              prepend-icon="mdi-gavel"
              class="mr-2"
              @click="processReport(item, 1)"
              :loading="processingId === item.id"
            >
              成立
              <v-tooltip activator="parent" location="top">確認違規，隱藏評論</v-tooltip>
            </v-btn>

            <v-btn
              size="small"
              color="grey-darken-1"
              variant="outlined"
              @click="processReport(item, 2)"
              :loading="processingId === item.id"
            >
              駁回
              <v-tooltip activator="parent" location="top">無違規，保留評論</v-tooltip>
            </v-btn>
          </div>
          <div v-else class="text-caption text-grey">已處理</div>
        </template>

        <!-- 評論內容摘要 (展開用) -->
        <template v-slot:expanded-row="{ columns, item }">
          <tr>
            <td :colspan="columns.length" class="pa-4 bg-grey-lighten-5">
              <div class="d-flex flex-column gap-2">
                <div><strong>書籍：</strong> {{ item.bookTitle }}</div>
                <div><strong>評論內容：</strong> {{ item.fullContent }}</div>
                <div><strong>檢舉說明：</strong> {{ item.reason }}</div>
              </div>
            </td>
          </tr>
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import reviewService from '@/api/reviewService.js'
import { REPORT_OPTIONS } from '@/utils/reportOptions.js'
import Swal from 'sweetalert2'

const reports = ref([])
const loading = ref(false)
const processingId = ref(null)

const headers = [
  { title: 'ID', key: 'id', width: '80px' },
  { title: '檢舉人', key: 'reporterName', width: '120px' },
  { title: '違規原因', key: 'reason', width: '140px' },
  { title: '評論摘要', key: 'reviewContent', sortable: false },
  { title: '狀態', key: 'status', width: '100px' },
  { title: '檢舉時間', key: 'createdAt', width: '160px' },
  { title: '操作', key: 'actions', sortable: false, align: 'end' },
]

const fetchReports = async () => {
  loading.value = true
  try {
    const response = await reviewService.getAdminReports()
    reports.value = response.data
  } catch (error) {
    console.error(error)
    Swal.fire({
      toast: true,
      position: 'top-end',
      icon: 'error',
      title: '讀取失敗',
      showConfirmButton: false,
      timer: 2000,
    })
  } finally {
    loading.value = false
  }
}

const processReport = async (item, status) => {
  // 再次確認
  const actionText = status === 1 ? '成立檢舉 (隱藏評論)' : '駁回檢舉 (保留評論)'
  const confirmColor = status === 1 ? '#d33' : '#757575'

  const { isConfirmed } = await Swal.fire({
    title: `確定要${status === 1 ? '通過' : '駁回'}嗎？`,
    text: actionText,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: confirmColor,
    confirmButtonText: '確定',
    cancelButtonText: '取消',
  })

  if (!isConfirmed) return

  processingId.value = item.id
  try {
    await reviewService.updateReportStatus(item.id, status)

    // 更新本地狀態
    item.status = status

    Swal.fire({
      toast: true,
      position: 'top-end',
      icon: 'success',
      title: '處理完成',
      showConfirmButton: false,
      timer: 1500,
    })
  } catch (error) {
    console.error(error)
    Swal.fire({ icon: 'error', title: '處理失敗', text: '請稍後再試' })
  } finally {
    processingId.value = null
  }
}

const getStatusColor = (status) => {
  switch (status) {
    case 0:
      return 'warning'
    case 1:
      return 'success'
    case 2:
      return 'grey'
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

const getReasonLabel = (val) => {
  const opt = REPORT_OPTIONS.find((o) => o.value === val)
  return opt ? opt.label : val
}

const formatDate = (dateArray) => {
  if (!dateArray) return ''
  return new Date(dateArray).toLocaleString('zh-TW', { hour12: false })
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.gap-2 {
  gap: 8px;
}
</style>
