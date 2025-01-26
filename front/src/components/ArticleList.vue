<template>
    <div class="card-container">
        <el-card class="card" v-for="article in articles" :key="article.articleUid">
            <div class="article-meta-info">
                <p class="author" v-if="type != 'userPage' && type != 'manage'" @click="gotoUserPage(article.userName)">
                    <el-avatar :src="article.avatarUrl" />
                    <span>作者: {{ article.nickName }}</span>
                </p>
            </div>
            <h2>
                <el-link class="article-title" :href="`/article/${article.articleUid}`">{{ article.title }}</el-link>
            </h2>
            <div class="func-area" v-if="type=='manage'">
                <el-button @click="gotoModifyArticle(article.articleUid)" type="primary">修改</el-button>
                <el-button @click="deleteArticle(article.articleUid)" type="danger">删除</el-button>
            </div>
            <p class="date">发表日期：{{ article.createTime }}</p>
            <div class="statistics">
                <div class="icon-with-text">
                    <el-icon :size="20"> <CaretTop /> </el-icon>
                    <span class="icon-text">{{ article.likeNum }}</span>
                </div>
                <div class="icon-with-text">
                    <el-icon :size="20"> <CaretBottom /> </el-icon>
                    <span class="icon-text">{{ article.dislikeNum }}</span>
                </div>
                <div class="icon-with-text">
                    <el-icon :size="16"> <Comment /> </el-icon>
                    <span class="icon-text">{{ article.commentCount }}</span>
                </div>
                <div class="icon-with-text">
                    <el-icon :size="16"> <View /> </el-icon>
                    <span class="icon-text">{{ article.viewCount }}</span>
                </div>
            </div>
        </el-card>
    </div>
    <el-pagination layout="prev, pager, next" :total="articleCount" :page-size="pageSize" :current-page="currentPage"
        @current-change="handlePageChange" background hide-on-single-page />
</template>


<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import { CaretTop, CaretBottom, Comment, View } from '@element-plus/icons-vue';

import type { ArticleForm } from '@/utils/infs';
import { useUserStore } from '@/utils/stores';
import { getHomeArticleListApi, getManageArticleListApi, getUserPageArticleListApi, deleteArticleApi, getConditionArticleListApi } from '@/apis/apiArticle';

onMounted(() => {
    getArticleList()
})

// 路由
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
function gotoUserPage(userName: string) {
    router.push("/user/" + userName)
}
function gotoModifyArticle(articleUid: string) {
    router.push("/write/" + articleUid)
}

// 接收参数
const page = route.query.page ? Number(route.query.page) : 1;
const currentPage = ref(page);
const { type = '', condition = '' } = defineProps({type: String, condition: String})


// watch(() => route.params.userName, (newUserName) => {
//   if (newUserName) {
//     userName.value = newUserName as string;
//     getArticleList();
//   }
// }, { immediate: true });

watch(() => route.params.page, (newPage) => {
    currentPage.value = Number(newPage) || 1;
    getArticleList();
});

watch(() => [type, condition], async ([newType, newCondition]) => {
    if(newType === 'collection' && newCondition) {
        await getArticleList();
    }
}, { immediate: true });

// 文章获取
const userName = ref(route.params.userName as string)
const articles = ref<ArticleForm[]>([])
const pageSize = ref(10)
const articleCount = ref(0)

async function getArticleList() {
    try {
        const response = ref()
        if(type == 'home') {
            response.value = await getHomeArticleListApi(currentPage.value, pageSize.value);
        } else if(type == 'userPage') {
            response.value = await getUserPageArticleListApi(currentPage.value, pageSize.value, userName.value);
        } else if(type == "manage") {
            response.value = await getManageArticleListApi(currentPage.value, pageSize.value);
        } else if(type == "search" || type == "tag") {
            response.value = await getConditionArticleListApi(currentPage.value, pageSize.value, type, condition)
        } else if(type == "collection") {
            if(condition != "") {
                response.value = await getConditionArticleListApi(currentPage.value, pageSize.value, type, condition)
            } else {
                return;
            }
        }
        else {
            router.push("/PageNotFound")
        }
        
        articles.value = response.value.data.list
        articleCount.value = response.value.data.total
        for (const article of articles.value) {
            if(article.avatarUrl == null) {
                article.avatarUrl = "https://s2.loli.net/2024/07/04/GMyYpLlrDZO4dom.png"
            }
            if(article.likeNum == null) {
                article.likeNum = 0
            }
            if(article.dislikeNum == null) {
                article.dislikeNum = 0
            }
            if(article.commentCount == null) {
                article.commentCount = 0
            }
            if(article.viewCount == null) {
                article.viewCount = 0
            }
        }
    } catch (error) {
        console.error('错误信息:', error)
        ElMessage.error("获取文章失败")
    }
}
async function handlePageChange(newPage: number) {
    currentPage.value = newPage
    router.push({ query: { page: newPage } })
    await getArticleList()
}

async function deleteArticle(articleUid: string) {
    try {
        const response = await deleteArticleApi(articleUid)
        getArticleList()
        if (response.code === 99999) {
            ElMessage.success("删除成功")
        }
    } catch (error) {
        console.log(error)
        ElMessage.error('删除失败')
    } finally {

    }
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