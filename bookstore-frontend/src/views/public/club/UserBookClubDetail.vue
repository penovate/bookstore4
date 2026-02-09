<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import bookClubService from '@/api/bookClubService.js';
import Swal from 'sweetalert2';
import BackPageButton from '@/components/BackPageButton.vue';
import BookClubDetailCard from '@/components/BookClubDetailCard.vue';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const club = ref({});

onMounted(async () => {
    const clubId = route.params.id;
    if (!clubId) {
        Swal.fire('錯誤', '無效的讀書會ID', 'error');
        router.push('/dev/user/bookclubs');
        return;
    }

    try {
        const response = await bookClubService.getClub(clubId);
        club.value = response.data;
        // Ensure clubDetail exists for template safety
        if (!club.value.clubDetail) {
            club.value.clubDetail = {};
        }
    } catch (error) {
        console.error('Failed to load club details:', error);
        Swal.fire('錯誤', '無法載入讀書會資料', 'error');
        router.push('/dev/user/bookclubs');
    } finally {
        loading.value = false;
    }
});
</script>

<template>
    <v-container class="py-10">
        <BookClubDetailCard :club="club" :loading="loading">
            <template #back-button>
                <BackPageButton />
            </template>
        </BookClubDetailCard>
    </v-container>
</template>
