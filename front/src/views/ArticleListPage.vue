<template>
    <div class="title">{{ title }}</div>
    <!-- <div class="collection" v-if="listType === 8">
        <div class="right-button">
            <el-button v-if="followCollection" class="follow-button" @click="handleUnfollowCollection">取关</el-button>
            <el-button v-else class="follow-button" type="primary" @click="handleFollowCollection">关注</el-button>
        </div>
        <div v-if="collection" class="collection-intro">简介：{{ collection.collectionIntro }}</div>
    </div> -->
    
    <ArticleList :type="type" />
</template>



<script lang="ts" setup>

import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useUserStore } from '../utils/stores';
import ArticleList from '../components/ArticleList.vue';


onMounted(initPage)

// 文章列表、搜索、合集、标签

const route = useRoute()
const router = useRouter()

const userStore = useUserStore()


const type = route.params.cond as string
const condition = route.params.condStr as string
const title = ref("")


async function initPage() {
    if(type == "manage") {
        const isLogin = userStore.isLogin
        if(!isLogin) {
            router.push("/")
        }
        title.value = "文章管理"
    } else if(type == "search") {
        
    } else if(type == "collection") {

    } else if(type == "tag") {

    } else {
        router.push("/PageNotFound")
    }
}

</script>


<style scoped>
.title {
    text-align: center;
    font-family: "微软雅黑";
    font-size: 30px;
    padding-bottom: 50px;
}

.right-button {
    float: right;
}
</style>