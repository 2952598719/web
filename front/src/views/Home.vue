<template>

    <ArticleList :type="'home'"/>

    <el-button v-if="userStoreObject.isLogin" class="fixed-button" type="primary" title="plus"
        @click="gotoArticleWrite()">+</el-button>
</template>


<script setup lang="ts">
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../utils/stores';

import ArticleList from '../components/ArticleList.vue';

const route = useRoute()
const router = useRouter()
// const page = ref<number>(parseInt(route.params.page as string))
// if (!page.value) {
//     page.value = 1
// }
const userStoreObject = useUserStore()
function gotoArticleWrite() {
    router.push("/write")
}

</script>


<style scoped>
.fixed-button {
    position: fixed;
    bottom: 50px;
    right: 50px;
    border-radius: 50%;
    width: 60px;
    height: 60px;
    z-index: 999;
    /* 字体相关 */
    line-height: 50px;
    font-size: 40px;
}
</style>