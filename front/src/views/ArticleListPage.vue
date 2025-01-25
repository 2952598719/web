<template>
    <div class="title">{{ title }}</div>

    <div class="search">
        <el-input v-model="searchContent" :prefix-icon="Search" @keyup.enter="gotoSearch">
            <template #append>
                <el-button :icon="Search" @click="gotoSearch" />
            </template>
        </el-input>
    </div>



    <!-- <div class="collection" v-if="listType === 8">
        <div class="right-button">
            <el-button v-if="followCollection" class="follow-button" @click="handleUnfollowCollection">取关</el-button>
            <el-button v-else class="follow-button" type="primary" @click="handleFollowCollection">关注</el-button>
        </div>
        <div v-if="collection" class="collection-intro">简介：{{ collection.collectionIntro }}</div>
    </div> -->
    
    <ArticleList :type="type" :key="keyValue"/>
</template>



<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';

import { useUserStore } from '../utils/stores';
import ArticleList from '../components/ArticleList.vue';


onMounted(initPage)

// 文章列表、搜索、合集、标签

const route = useRoute()
const router = useRouter()
const searchContent = ref()
const userStore = useUserStore()
const keyValue = ref(1)

function changeKey(){
	keyValue.value += 1
}

watch(() => route.params.condition, async (newCondition) => {
  if (newCondition) {
	changeKey()
  }
}, { immediate: true }); // 立即执行一次以初始化

const type = route.params.type as string
const condition = route.params.condition as string
const title = ref("")

function gotoSearch() {
    router.push("/articleList/search/" + searchContent.value)
}


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

.search {
    padding-top: 20px;
    padding-bottom: 40px;
    text-align: center;
    margin-left: 25%;
    margin-right: 25%;
}

</style>