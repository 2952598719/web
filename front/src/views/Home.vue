<template>

    <div class="search">
        <el-input v-model="searchContent" :prefix-icon="Search" @keyup.enter="gotoSearch">
            <template #append>
                <el-button :icon="Search" @click="gotoSearch" />
            </template>
        </el-input>
    </div>

    <div class="tabs-container">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="推荐" name="home"></el-tab-pane>
            <el-tab-pane label="关注" name="master"></el-tab-pane>
        </el-tabs>
    </div>

    <ArticleList :type="activeTab"/>

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
const activeTab = ref('home');

function handleTabChange(tabName: string) {
    activeTab.value = tabName;
}


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

.tabs-container {
    margin-bottom: 20px; /* 确保 tabs 和搜索框之间有间距 */
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