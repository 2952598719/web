<template>
    <div class="card-container">
        <el-card class="card" v-for="article in articles" :key="article.articleUid" :lock-scroll="false">
            <div class="header">
                <div class="left">
                    <h2>
                        <el-link class="article-title" :href="`/article/${article.articleUid}`">{{ article.title }}</el-link>
                    </h2>
                </div>
                <div class="right">
                    <div class="article-meta-info" v-if="type != 'userPage' && type != 'manage'">
                        <div class="author" @click="gotoUserPage(article.userName)">
                            <el-avatar :src="article.avatarUrl" />
                            <span class="author-name">{{ article.nickName }}</span>
                        </div>
                    </div>
                    <div class="func-area" v-if="type=='manage'">
                        <el-button @click="gotoModifyArticle(article.articleUid)" text type="primary">修改</el-button>
                        <el-button @click="deleteArticle(article.articleUid)" text type="danger">删除</el-button>
                    </div>
                </div>
            </div>
            <div class="footer">
                <div class="left">
                    <div class="statistics">
                        <div class="icon-with-text">
                            <el-icon :size="16"> <CaretTop /> </el-icon>
                            <span class="icon-text">{{ article.likeNum }}</span>
                        </div>
                        <!-- <div class="icon-with-text">
                            <el-icon :size="20"> <CaretBottom /> </el-icon>
                            <span class="icon-text">{{ article.dislikeNum }}</span>
                        </div> -->
                        <div class="icon-with-text">
                            <el-icon :size="16"> <Comment /> </el-icon>
                            <span class="icon-text">{{ article.commentCount }}</span>
                        </div>
                        <div class="icon-with-text">
                            <el-icon :size="16"> <View /> </el-icon>
                            <span class="icon-text">{{ article.viewCount }}</span>
                        </div>
                    </div>
                </div>
                <div class="right">
                    <p class="date">发表日期：{{ article.createTime }}</p>
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
import { getHomeArticleListApi, getManageArticleListApi, getUserPageArticleListApi, deleteArticleApi, getConditionArticleListApi, getMasterArticleListApi } from '@/apis/apiArticle';

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

// 文章获取
const userName = ref(route.params.userName as string)
const articles = ref<ArticleForm[]>([])
const pageSize = ref(10)
const articleCount = ref(0)

watch(() => route.params.page, (newPage) => {
    currentPage.value = Number(newPage) || 1;
    getArticleList();
});

watch(() => [type, condition], async ([newType, newCondition]) => {
    if(newType === 'collection' && newCondition) {
        await getArticleList();
    } else if(newType == 'home' || newType == 'master') {
        await getArticleList();
    }
}, { immediate: true });

async function getArticleList() {
    try {
        const response = ref()
        if(type == 'home') {
            response.value = await getHomeArticleListApi(currentPage.value, pageSize.value);
        } else if(type == 'master') {
            response.value = await getMasterArticleListApi(currentPage.value, pageSize.value);
        }else if(type == 'userPage') {
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

.card {
    margin-bottom: 40px;
    margin-left: 25%;
    margin-right: 25%;
    padding-left: 20px;
    padding-right: 20px;
    /* width: 80%; */
    /* max-width: 800px; */
    border-radius: 8px;
    background-color: rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transition: box-shadow 0.3s ease;
}

.card:hover {
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.card-container {
    margin-top: 20px;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.left {
    flex: 1;
}

.right {
    margin-left: 20px;
}

.article-title {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    text-decoration: none;
}

.article-title:hover {
    color: #409EFF;
}

.article-meta-info {
    display: flex;
    align-items: center;
}

.func-area {
    margin-top: 15px;
    text-align: right;
}

.author {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
    color: #333;
    font-size: 14px;
}

.author:hover {
    color: #409EFF;
}

.el-avatar {
    margin-bottom: 5px; /* 头像和作者名之间的间距 */
}

.author-name {
    text-align: center;
    font-size: 12px;
    color: #666;
}

.footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 15px;
}

.statistics {
    display: flex;
    align-items: center;
    user-select: none;
}

/* .icon-with-text-top {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 8px;
    margin-bottom: 4px;
} */

.icon-with-text {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 10px;
}

.icon-text {
    /* margin-top: 2px; */
    font-size: 12px;
    color: #666;
}

.date {
    font-size: 14px;
    color: #666;
    margin: 0;
}

@media (max-width: 768px) {
    .card {
        width: 90%;
        margin-left: 5%;
        margin-right: 5%;
    }

    .header {
        flex-direction: column;
        align-items: flex-start;
    }

    .right {
        margin-left: 0;
        margin-top: 10px;
    }

    .article-title {
        font-size: 18px;
    }

    .author {
        font-size: 12px;
    }

    .date {
        font-size: 12px;
    }
}
</style>