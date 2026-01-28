<template>
  <div class="analysis-page-wrapper">
    <div class="header-section mb-6 text-left">
      <h2 class="forest-main-title">訂單數據分析</h2>
      <v-btn
        variant="text"
        color="secondary"
        prepend-icon="mdi-arrow-left"
        @click="router.push({ name: 'orderList' })"
        class="mt-2"
      >
        返回訂單列表
      </v-btn>
    </div>

    <!-- 篩選與日期區間 -->
    <v-card class="mb-6 pa-4 filter-card">
      <v-row align="center">
        <v-col cols="12" md="4">
          <v-select
            v-model="selectedPeriod"
            :items="periodOptions"
            label="選擇區間"
            variant="outlined"
            density="compact"
            hide-details
            @update:model-value="onPeriodChange"
          ></v-select>
        </v-col>
        <v-col cols="12" md="8" class="d-flex gap-2">
          <v-text-field
            v-model="startDate"
            label="開始日期"
            type="date"
            variant="outlined"
            density="compact"
            hide-details
            @change="fetchData"
          ></v-text-field>
          <v-text-field
            v-model="endDate"
            label="結束日期"
            type="date"
            class="ml-2"
            variant="outlined"
            density="compact"
            hide-details
            @change="fetchData"
          ></v-text-field>
        </v-col>
      </v-row>
    </v-card>

    <v-row class="mb-6">
      <!-- 左側：本期銷售總額 -->
      <!-- 左側：本期銷售概況 -->
      <v-col cols="12" md="4">
        <v-card class="revenue-card primary-gradient h-100" dark>
          <v-card-text class="d-flex flex-column h-100 position-relative" style="z-index: 1">
            <!-- 標題 -->
            <div class="d-flex align-center mb-6">
              <v-icon icon="mdi-chart-box-outline" class="mr-3" size="x-large"></v-icon>
              <div class="text-h5 font-weight-bold">本期銷售概況</div>
            </div>

            <!-- 銷售總額 -->
            <div class="mb-6">
              <div class="text-h5 font-weight-black">
                銷售總額
                <div class="text-h4 font-weight-black">
                  NT$ {{ formatNumber(salesOverview.totalRevenue) }}
                </div>
              </div>
            </div>

            <!-- 次要指標區塊 -->
            <div class="d-flex justify-space-between mt-auto">
              <div
                class="text-center pr-4 border-e opacity-80"
                style="flex: 1; border-color: rgba(255, 255, 255, 0.3) !important"
              >
                <div class="text-caption mb-1">活動訂單</div>
                <div class="text-h5 font-weight-bold">
                  {{ formatNumber(salesOverview.totalOrders) }} <span class="text-caption">筆</span>
                </div>
              </div>
              <div class="text-center pl-4" style="flex: 1">
                <div class="text-caption mb-1">書本銷量</div>
                <div class="text-h5 font-weight-bold">
                  {{ formatNumber(salesOverview.totalBooksSold) }}
                  <span class="text-caption">本</span>
                </div>
              </div>
            </div>

            <!-- 日期區間 -->
            <div
              class="text-caption opacity-60 mt-4 pt-3 border-t"
              style="border-color: rgba(255, 255, 255, 0.2) !important"
            >
              <v-icon icon="mdi-calendar-range" size="x-small" class="mr-1"></v-icon>
              {{ startDate }} ~ {{ endDate }}
            </div>
          </v-card-text>
          <!-- 裝飾背景圖示 -->
          <v-icon
            icon="mdi-chart-arc"
            size="180"
            class="position-absolute"
            style="right: -40px; top: -20px; opacity: 0.1"
          ></v-icon>
        </v-card>
      </v-col>

      <!-- 右側：年度銷售趨勢圖 -->
      <v-col cols="12" md="8">
        <v-card class="chart-card pa-4 h-100">
          <v-card-title class="font-weight-bold mb-2">近一年銷售趨勢</v-card-title>
          <div style="height: 250px; position: relative">
            <canvas id="yearlySalesChart"></canvas>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 前三名熱銷卡片 -->
    <div class="d-flex align-center mb-4">
      <h3 class="text-h4 font-weight-bold text-grey-darken-2">🏆 熱銷排行 (依本期區間統計)</h3>
    </div>
    <v-row v-if="topBooks.length > 0" class="mb-6">
      <v-col v-for="(book, index) in topBooks.slice(0, 3)" :key="book.bookName" cols="12" md="4">
        <v-card class="top-card" :class="'rank-' + (index + 1)">
          <v-card-text class="d-flex align-center">
            <div class="rank-badge mr-4">{{ index + 1 }}</div>
            <div>
              <div class="text-caption text-green-darken-4 font-weight-bold">暢銷排名 No.{{ index + 1 }}</div>
              <h3 class="text-h6 font-weight-bold text-truncate" style="max-width: 300px">
                {{ book.bookName }}
              </h3>
              <div class="text-h4 font-weight-black mt-1 num-highlight">
                {{ book.totalQuantity }} <span class="text-body-1">本</span>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 銷售長條圖 (書籍) -->
    <v-card class="chart-card pa-4">
      <v-card-title class="text-h6 font-weight-bold mb-4">書籍銷售統計圖表</v-card-title>
      <div style="height: 400px; position: relative">
        <!-- 畫布容器 -->
        <canvas id="salesChart"></canvas>
      </div>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import Chart from 'chart.js/auto'
import orderService from '@/api/orderService.js'

const router = useRouter()
// 書籍銷售相關
const salesData = ref([])
// 營收與概況相關
const salesOverview = ref({
  totalRevenue: 0,
  totalOrders: 0,
  totalBooksSold: 0,
})
// 年度趨勢相關
const annualSalesData = ref([])

// 日期區間狀態
const startDate = ref('') // YYYY-MM-DD
const endDate = ref('')
const selectedPeriod = ref('thisMonth')

let chartInstance = null
let yearlyChartInstance = null

const periodOptions = [
  { title: '本月', value: 'thisMonth' },
  { title: '上個月', value: 'lastMonth' },
  { title: '本年度', value: 'thisYear' },
  { title: '近 7 天', value: 'last7Days' },
  { title: '近 30 天', value: 'last30Days' },
  { title: '自訂', value: 'custom' },
]

const topBooks = computed(() => {
  return [...salesData.value].sort((a, b) => b.totalQuantity - a.totalQuantity)
})

const formatNumber = (num) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getYearFromDate = (dateStr) => {
  if (!dateStr) return new Date().getFullYear()
  return dateStr.split('-')[0]
}

// 設定日期區間 helper
const setDateRange = (period) => {
  const now = new Date()
  let start = new Date()
  let end = new Date()

  if (period === 'thisMonth') {
    start = new Date(now.getFullYear(), now.getMonth(), 1)
    end = new Date(now.getFullYear(), now.getMonth() + 1, 0)
  } else if (period === 'lastMonth') {
    start = new Date(now.getFullYear(), now.getMonth() - 1, 1)
    end = new Date(now.getFullYear(), now.getMonth(), 0)
  } else if (period === 'thisYear') {
    start = new Date(now.getFullYear(), 0, 1)
    end = new Date(now.getFullYear(), 11, 31)
  } else if (period === 'last7Days') {
    start.setDate(now.getDate() - 7)
  } else if (period === 'last30Days') {
    start.setDate(now.getDate() - 30)
  }

  const formatDate = (date) => date.toISOString().split('T')[0]

  if (period !== 'custom') {
    startDate.value = formatDate(start)
    endDate.value = formatDate(end)
  }
}

const onPeriodChange = (val) => {
  setDateRange(val)
  fetchData()
}

// 渲染書籍銷售圖表 (既有)
const renderChart = () => {
  const ctx = document.getElementById('salesChart')
  if (!ctx) return

  if (chartInstance) {
    chartInstance.destroy()
  }

  chartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: salesData.value.map((item) => item.bookName),
      datasets: [
        {
          label: '銷售數量',
          data: salesData.value.map((item) => item.totalQuantity),
          backgroundColor: 'rgba(46, 92, 67, 0.7)',
          borderColor: '#2E5C43',
          borderWidth: 1,
          borderRadius: 4,
          barPercentage: 0.7,
          categoryPercentage: 0.8,
        },
      ],
    },
    options: {
      indexAxis: 'y', // 改為水平長條圖
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false, // 隱藏圖例讓畫面更簡潔
        },
        tooltip: {
          backgroundColor: 'rgba(46, 92, 67, 0.9)',
          // ... styles kept simple
        },
      },
      scales: {
        x: {
          beginAtZero: true,
          grid: {
            color: '#f0f0f0',
            borderDash: [5, 5],
          },
        },
        y: {
          grid: {
            display: false,
          },
        },
      },
    },
  })
}

// 渲染年度趨勢圖表 (改為近12個月)
const renderYearlyChart = () => {
  const ctx = document.getElementById('yearlySalesChart')
  if (!ctx) return

  if (yearlyChartInstance) {
    yearlyChartInstance.destroy()
  }

  // 準備近 12 個月的標籤 (YYYY-MM)
  const labels = []
  const today = new Date()
  for (let i = 11; i >= 0; i--) {
    const d = new Date(today.getFullYear(), today.getMonth() - i, 1)
    const y = d.getFullYear()
    const m = d.getMonth() + 1
    labels.push(`${y}-${String(m).padStart(2, '0')}`)
  }

  // 映射數據
  const revenueData = labels.map((label) => {
    const [y, m] = label.split('-').map(Number)
    // 後端資料包含 {year, month, revenue, orderCount}
    const found = annualSalesData.value.find((d) => d.year === y && d.month === m)
    return found ? found.revenue : 0
  })

  const orderCountData = labels.map((label) => {
    const [y, m] = label.split('-').map(Number)
    const found = annualSalesData.value.find((d) => d.year === y && d.month === m)
    return found ? found.orderCount : 0
  })

  // 顯示標籤簡化為 MM月 (或是 YYYY/MM)
  const displayLabels = labels.map((l) => {
    const m = l.split('-')[1]
    return `${parseInt(m)}月` // 簡潔顯示，Tooltip可顯示完整年份
  })

  yearlyChartInstance = new Chart(ctx, {
    type: 'bar', // 基礎類型
    data: {
      labels: displayLabels, // X軸標籤
      datasets: [
        {
          label: '月銷售額 (NT$)',
          data: revenueData,
          type: 'bar',
          backgroundColor: 'rgba(46, 92, 67, 0.6)',
          borderColor: '#2E5C43',
          borderWidth: 1,
          yAxisID: 'y',
          order: 2,
        },
        {
          label: '訂單數',
          data: orderCountData,
          type: 'line',
          borderColor: '#FF7043',
          backgroundColor: '#FF7043',
          borderWidth: 2,
          pointRadius: 3,
          tension: 0.3,
          yAxisID: 'y1',
          order: 1,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        mode: 'index',
        intersect: false,
      },
      plugins: {
        legend: {
          display: true,
          position: 'top',
        },
        tooltip: {
          callbacks: {
            title: (tooltipItems) => {
              // Tooltip 標題顯示完整年份月份
              const index = tooltipItems[0].dataIndex
              return labels[index]
            },
          },
        },
      },
      scales: {
        x: {
          beginAtZero: true,
        },
        y: {
          type: 'linear',
          display: true,
          position: 'left',
          title: {
            display: true,
            text: '銷售額',
          },
          grid: {
            drawOnChartArea: true,
          },
        },
        y1: {
          type: 'linear',
          display: true,
          position: 'right',
          title: {
            display: true,
            text: '訂單數',
          },
          grid: {
            drawOnChartArea: false, // 避免網格重疊
          },
        },
      },
    },
  })
}

const fetchOverviewData = async () => {
  try {
    const res = await orderService.getSalesOverview(startDate.value, endDate.value)
    salesOverview.value = res.data
  } catch (e) {
    console.error('Fetch overview failed', e)
  }
}

// 取得近12個月趨勢
const fetchTrendData = async () => {
  try {
    const res = await orderService.getRecentSalesTrends()
    annualSalesData.value = res.data
    nextTick(() => {
      renderYearlyChart()
    })
  } catch (e) {
    console.error('Fetch trend data failed', e)
  }
}

const fetchBookData = async () => {
  try {
    const response = await orderService.getSalesAnalysis(startDate.value, endDate.value)
    salesData.value = response.data
    nextTick(() => {
      renderChart()
    })
  } catch (error) {
    console.error('Fetch analysis data failed', error)
  }
}

const fetchData = () => {
  fetchBookData()
  fetchOverviewData()
  fetchTrendData()
}

onMounted(() => {
  // 預設本月
  setDateRange('thisMonth')
  fetchData()
})
</script>

<style scoped lang="scss">
.analysis-page-wrapper {
  padding: 20px;
  width: 100%;
}

.forest-main-title {
  color: #2e5c43;
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 0;
}

.filter-card {
  border-radius: 12px;
  border: 1px solid #eee;
}

.revenue-card {
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  color: white;
}

.primary-gradient {
  background: linear-gradient(135deg, #2e5c43 0%, #4a8f6d 100%);
}

.secondary-gradient {
  background: linear-gradient(135deg, #546e7a 0%, #78909c 100%);
}

.chart-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.top-card {
  border-radius: 12px;
  transition: transform 0.2s;
  border: 1px solid #eee;
  padding: 10px;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
  }

  &.rank-1 .rank-badge {
    background-color: #ffd700;
    color: #fff;
  }
  &.rank-2 .rank-badge {
    background-color: #c0c0c0;
    color: #fff;
  }
  &.rank-3 .rank-badge {
    background-color: #cd7f32;
    color: #fff;
  }
}

.rank-badge {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: 900;
  background-color: #eee;
}

.text-caption {
  font-size: 24px;
}
</style>
