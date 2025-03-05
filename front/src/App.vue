<template>
    <Header />

    <el-divider />
    <div class="content-main">
        <router-view />
        <div class="background-image" :style="{ backgroundImage: `url(${userStoreObject.background})` }"></div>
    </div>
    <Footer />

    <div class="hello">
        <div ref="live2dContentRef" id="live2d"></div>
    </div>

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
    position: relative;
    overflow-y: auto;
    /* 新增滚动条 */
}


.background-image {
    position: fixed;
    /* 改为固定定位 */
    top: 0;
    left: 0;
    width: 100vw;
    /* 使用视口单位 */
    height: 100vh;
    z-index: -1;
    opacity: 0.6;
    background-size: cover;
    background-position: center;
    background-attachment: fixed;
}
</style>
