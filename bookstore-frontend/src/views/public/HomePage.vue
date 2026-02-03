<script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import bookService from '@/api/bookService';
  import BookCard from './books/BookCard.vue';
  import Swal from 'sweetalert2'

  const router = useRouter();
  const hotBooks = ref([]); 
  const featuredBooks = ref([]);

  const goToBooks = () => {
    router.push({ name: 'user-books' });
  };

  const fetchAllHomeBooks = async () => {
    try {
      const response = await bookService.getAllBooks();
      let allBooks = response.data.filter(book => book.onShelf === 1);

      for (let i = allBooks.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [allBooks[i], allBooks[j]] = [allBooks[j], allBooks[i]];
      }

      hotBooks.value = allBooks.slice(0, 4);     
      featuredBooks.value = allBooks.slice(4, 8); 
      
    } catch (error) {
      console.error('Fetching books failed:', error);
    }
  };

  onMounted(() => {
    fetchAllHomeBooks();

    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('logout')) {
      Swal.fire({
        icon: 'success',
        title: '登出成功',
        text: '期待與您再次相遇！',
        confirmButtonColor: '#2e5c43',
        timer: 2000,
        showConfirmButton: false,
        timerProgressBar: true, 
      });

      window.history.replaceState({}, document.title, window.location.pathname);
    }
  });
</script>
<template>
  <div class="home-wrapper">
    <v-parallax src="https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&q=80&w=2070" height="600">
      <div class="d-flex flex-column fill-height justify-center align-center text-white hero-overlay">
        <h1 class="text-h2 font-weight-black mb-4 hero-title">森林書屋</h1>
        <h4 class="text-h5 font-weight-light mb-8 hero-subtitle">在喧囂的城市中，為您找尋一隅靜謐的書香。</h4>
        <v-btn color="primary" size="x-large" class="rounded-pill px-10 login-btn-cute shadow-lg" elevation="4" @click="goToBooks">
          <v-icon start icon="mdi-compass-outline"></v-icon>
          與書相遇
        </v-btn>
      </div>
    </v-parallax>

    <v-container class="mt-12 pb-12">
      <div class="section-header mb-10">
        <span class="section-tag">POPULAR</span>
        <h2 class="text-h4 font-weight-black">熱門書籍</h2>
        <div class="header-line"></div>
      </div>

      <v-row>
        <v-col v-for="book in hotBooks" :key="book.bookId" cols="12" sm="6" md="3">
          <BookCard :book="book" />
        </v-col>
      </v-row>
    </v-container>

    <v-container class="py-12 bg-light-section rounded-xl mb-12">
      <div class="d-flex justify-space-between align-end mb-8">
        <div>
          <span class="text-primary font-weight-bold">PICK OF THE MONTH</span>
          <h2 class="text-h4 font-weight-black">本月精選</h2>
        </div>
        <v-btn variant="text" color="primary" @click="goToBooks" class="font-weight-bold">
          查看更多 <v-icon icon="mdi-arrow-right" class="ms-1"></v-icon>
        </v-btn>
      </div>

      <v-row>
        <v-col v-for="book in featuredBooks" :key="book.bookId" cols="12" sm="6" md="3">
          <v-card class="featured-book-wrapper rounded-xl" elevation="0" color="transparent">
             <BookCard :book="book" />
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<style scoped>
.section-header {
  text-align: center;
  position: relative;
}

.section-tag {
  display: block;
  letter-spacing: 4px;
  color: #2e5c43;
  font-size: 0.8rem;
  font-weight: 800;
  margin-bottom: 4px;
}

.header-line {
  width: 60px;
  height: 4px;
  background: #2e5c43;
  margin: 12px auto 0;
  border-radius: 2px;
}

.hero-overlay {
  background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.6));
}

.hero-title {
  text-shadow: 0 4px 10px rgba(0,0,0,0.3);
}

.bg-light-section {
  background-color: rgba(46, 92, 67, 0.03);
}

.featured-book-wrapper {
  transition: transform 0.3s ease;
}

.featured-book-wrapper:hover {
  transform: translateY(-5px);
}
</style>