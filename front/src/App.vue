<template>
    <Header/>
    <div class="content-main">
        <router-view />
        <div class="background-image" :style="{ backgroundImage: `url(${userStoreObject.background})` }"></div>
    </div>
    <Footer />

</template>

<script setup lang="ts">
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import { useUserStore } from './utils/stores';

const userStoreObject = useUserStore();
userStoreObject.loadBackground()

</script>

<style scoped>
.layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

.content-main {
    flex: 1;
    /* 关键属性：填充剩余空间 */
    position: relative;
    /* 创建定位上下文 */
    /* min-height: calc(100vh - 120px); */
    /* 根据 Header/Footer 实际高度调整 */
}

.background-image {
    position: absolute;
    /* 相对于 content-main 定位 */
    top: 0;
    left: 0;
    width: 100%;
    height: 110%;
    /* 继承父容器高度 */
    z-index: -1;
    opacity: 0.6;
    background-size: cover;
}

</style>
