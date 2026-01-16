<template>
  <div class="home-wrapper">
    <v-parallax
      src="https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&q=80&w=2070"
      height="500"
    >
      <div
        class="d-flex flex-column fill-height justify-center align-center text-white hero-overlay"
      >
        <h1 class="text-h2 font-weight-black mb-4">森林書屋 Forest Books</h1>
        <h4 class="text-h5 font-weight-light mb-6">在喧囂的城市中，為您找尋一隅靜謐的書香。</h4>
        <v-btn color="primary" size="x-large" class="rounded-pill px-8" elevation="4">
          立即探索
        </v-btn>
      </div>
    </v-parallax>

    <v-container class="mt-12">
      <div class="d-flex align-center mb-8">
        <v-divider></v-divider>
        <h2 class="mx-4 text-h4 font-weight-bold" style="color: #2e5c43">熱門分類</h2>
        <v-divider></v-divider>
      </div>
      <v-row>
        <v-col v-for="cat in categories" :key="cat.title" cols="12" sm="6" md="3">
          <v-hover v-slot="{ isHovering, props }">
            <v-card
              v-bind="props"
              class="category-card rounded-xl text-center pa-6"
              :elevation="isHovering ? 8 : 2"
              :color="isHovering ? '#E8F5E9' : 'white'"
            >
              <v-icon :icon="cat.icon" size="48" color="primary" class="mb-4"></v-icon>
              <h3 class="text-h6 font-weight-bold">{{ cat.title }}</h3>
            </v-card>
          </v-hover>
        </v-col>
      </v-row>
    </v-container>

    <v-container class="my-12">
      <div class="d-flex justify-space-between align-center mb-6">
        <h2 class="text-h4 font-weight-bold" style="color: #2e5c43">本月精選</h2>
        <v-btn variant="text" color="primary"
          >查看更多 <v-icon icon="mdi-chevron-right"></v-icon
        ></v-btn>
      </div>

      <v-row>
        <v-col v-for="i in 4" :key="i" cols="12" sm="6" md="3">
          <v-card class="book-card rounded-lg" elevation="1">
            <v-img
              src="https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=1974"
              height="300"
              cover
            ></v-img>
            <v-card-title class="pt-4 font-weight-bold">範例書名 2026</v-card-title>
            <v-card-subtitle>作者名稱</v-card-subtitle>
            <v-card-text class="d-flex justify-space-between align-center">
              <span class="text-h6 text-primary font-weight-black">$ 380</span>
              <v-btn icon="mdi-cart-plus" variant="tonal" color="primary" size="small"></v-btn>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup>
const categories = [
  { title: '文學小說', icon: 'mdi-book-open-page-variant', color: '#C8E6C9' },
  { title: '商業理財', icon: 'mdi-chart-line', color: '#F1F8E9' },
  { title: '人文社科', icon: 'mdi-account-group', color: '#DCEDC8' },
  { title: '藝術設計', icon: 'mdi-palette', color: '#E8F5E9' },
]
</script>

<style scoped>
.hero-overlay {
  background: rgba(0, 0, 0, 0.4);
}
.category-card {
  transition: all 0.3s ease;
  cursor: pointer;
}
.book-card {
  transition: transform 0.3s;
}
.book-card:hover {
  transform: translateY(-5px);
}
</style>
