<template>

    <div class="search">
        <el-input v-model="searchContent" :prefix-icon="Search" @keyup.enter="gotoSearch">
            <template #append>
                <el-button :icon="Search" @click="gotoSearch" />
            </template>
        </el-input>
    </div>

    <ArticleList :type="'home'"/>

    <el-button v-if="userStoreObject.isLogin" class="fixed-button" type="primary" title="plus"
        @click="gotoArticleWrite()">+</el-button>
</template>


<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router'
import { UserFilled, Search } from '@element-plus/icons-vue';

import ArticleList from '../components/ArticleList.vue';
import { useUserStore } from '../utils/stores';

const route = useRoute()
const router = useRouter()

const searchContent = ref()


const userStoreObject = useUserStore()
function gotoArticleWrite() {
    router.push("/write")
}

function gotoSearch() {
    router.push("/articleList/search/" + searchContent.value)
}

</script>


<style scoped>

.search {
    padding-top: 20px;
    padding-bottom: 40px;
    text-align: center;
}

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

.search {
    padding-top: 20px;
    padding-bottom: 40px;
    text-align: center;
    margin-left: 25%;
    margin-right: 25%;
}

</style>