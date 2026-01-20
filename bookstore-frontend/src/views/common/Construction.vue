<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
    router.go(-1);
};
</script>

<template>
    <v-container class="fill-height d-flex align-center justify-center">
        <v-card class="text-center pa-10 rounded-xl" elevation="0" max-width="600">
            <div class="mb-6">
                <v-icon icon="mdi-hammer-wrench" size="120" color="grey-lighten-2"
                    class="mb-4 construction-icon"></v-icon>
            </div>

            <h2 class="text-h3 font-weight-bold text-primary mb-2">功能建置中</h2>


            <div class="d-flex justify-center gap-4">
                <v-btn variant="outlined" color="primary" size="large" prepend-icon="mdi-arrow-left" @click="goBack">
                    返回上一頁
                </v-btn>
                <v-btn variant="flat" color="primary" size="large" prepend-icon="mdi-home" to="/home">
                    回首頁
                </v-btn>
            </div>
        </v-card>
    </v-container>
</template>

<style scoped>
.construction-icon {
    animation: swing 3s ease-in-out infinite transform-origin 50% 100%;
}

@keyframes swing {
    0% {
        transform: rotate(0deg);
    }

    20% {
        transform: rotate(15deg);
    }

    40% {
        transform: rotate(-10deg);
    }

    60% {
        transform: rotate(5deg);
    }

    80% {
        transform: rotate(-5deg);
    }

    100% {
        transform: rotate(0deg);
    }
}

.gap-4 {
    gap: 16px;
}
</style>
