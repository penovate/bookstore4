<template>
  <v-container>
    <v-card class="pa-6 forest-card-border elevation-3 rounded-xl">
      <h2 class="text-h4 font-weight-black mb-6" style="color: #2e5c43">
        <v-icon icon="mdi-clipboard-text-clock-outline" class="mr-2"></v-icon>
        管理員操作日誌
      </h2>

      <v-data-table :headers="headers" :items="logs" :loading="loading" class="forest-table" hover>
        <template v-slot:item.actionTime="{ item }">
          <span class="text-grey-darken-1">{{ new Date(item.actionTime).toLocaleString() }}</span>
        </template>

        <template v-slot:item.adminName="{ item }">
          <v-chip color="primary" variant="tonal" size="small" class="font-weight-bold">
            <v-icon start icon="mdi-account-tie" size="16"></v-icon>
            {{ item.adminName }}
          </v-chip>
        </template>

        <template v-slot:item.action="{ item }">
          <div class="py-2 text-body-2 font-weight-medium">
            {{ item.action }}
          </div>
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const logs = ref([])
const loading = ref(true)

const headers = [
  { title: '操作時間', key: 'actionTime', width: '25%', align: 'start' },
  { title: '執行者 (管理員)', key: 'adminName', width: '20%', align: 'start' },
  { title: '操作詳細內容', key: 'action', width: '55%', align: 'start' },
]

const fetchLogs = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/logs/list')
    logs.value = res.data
  } catch (error) {
    console.error('日誌讀取失敗', error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchLogs)
</script>

<style scoped>
.forest-card-border {
  border-top: 8px solid #2e5c43 !important;
}

:deep(.v-data-table .v-data-table__td) {
  white-space: normal !important;
  word-wrap: break-word;
}
</style>
