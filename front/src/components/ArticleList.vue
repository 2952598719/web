<template>
    <div class="card-container">
        <el-card class="card" v-for="article in articles" :key="article.articleUid">
            <h2>
                <el-link class="article-title" :href="`/article/${article.articleUid}`">{{ article.title }}</el-link>
            </h2>
            <div class="article-meta-info">
                <p class="author">
                    <el-button @click="gotoUserPage(article.userName)" link>作者: {{ article.nickName }}</el-button>
                </p>
                <p class="date">发表日期：{{ article.createTime }}</p>
            </div>
        </el-card>
    </div>
    <el-pagination layout="prev, pager, next" :total="articleCount" :page-size="pageSize" :current-page="currentPage"
        @current-change="handlePageChange" background hide-on-single-page />
</template>


<script lang="ts" setup>
import { ElMessage } from 'element-plus';
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { getArticleListApi } from '@/apis/apiArticle';
import type { ArticleForm } from '@/utils/infs';

onMounted(() => {
    getArticleList()
})



// 接收参数
const { page } = defineProps({page: Number})
const currentPage = ref(page || 1);

// 路由
const route = useRoute()
const router = useRouter()
function gotoUserPage(userName: string) {
    router.push("/user/" + userName)
}

watch(() => route.params.page, (newPage) => {
    currentPage.value = Number(newPage) || 1;
    getArticleList();
});

// 文章获取
const articles = ref<ArticleForm[]>([])
const pageSize = ref(2)
const articleCount = ref(0)

async function getArticleList() {
    try {
        const response = ref()
        const fetchParam = ref<string>('')
        response.value = await getArticleListApi(currentPage.value, pageSize.value, "home", fetchParam.value);
        articles.value = response.value.data.list
        articleCount.value = response.value.data.total
    } catch (error) {
        console.error('错误信息:', error)
        ElMessage.error("获取文章失败")
    }
}
function handlePageChange(newPage: number) {
    currentPage.value = newPage
    router.push("/page/" + newPage)
}


</script>


<style scoped>
.el-pagination {
    padding-top: 20px;
    justify-content: center;
}

.statistics {
    float: left;
    padding-bottom: 20px;
    user-select: none;
}

.icon-with-text {
    position: relative;
    display: inline-block;
    /* 使其可以相对定位 */
    margin-right: 10px;
    /* 添加一些间距 */
}

.icon-text {
    position: absolute;
    bottom: -10px;
    /* 调整数字与图标的距离 */
    left: 0;
    right: 0;
    text-align: center;
    font-size: 12px;
    /* 调整字体大小 */
}

.category {
    float: right;
    color: gray;
    cursor: pointer;
}

.card {
    margin-bottom: 20px;
    margin-left: 25%;
    margin-right: 25%;
}

.card-container {
    margin-top: 20px;
}


</style>