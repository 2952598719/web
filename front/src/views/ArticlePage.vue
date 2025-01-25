<template>
    <div class="title">{{ articleData.title }}</div>
    <div class="author-container">
        <el-avatar :src="articleData.avatarUrl" />
        <span class="author" @click="gotoUserPage(articleData.userName)">作者：{{ articleData.nickName }}</span>
    </div>

    <div class="functions" v-if="userStoreObject.isLogin && userStoreObject.userName != articleData.userName">
        <div class="icon-with-text" @click="handleLike">
            <el-icon v-if="voteType == 1" color="#409efc" :size="20">
                <CaretTop />
            </el-icon>
            <el-icon v-else :size="20">
                <CaretTop />
            </el-icon>
            <span class="icon-text">{{ articleData.likeNum }}</span>
        </div>
        <div class="icon-with-text" @click="handleDislike">
            <el-icon v-if="voteType == 2" color="#409efc" :size="20">
                <CaretBottom />
            </el-icon>
            <el-icon v-else :size="20">
                <CaretBottom />
            </el-icon>
            <span class="icon-text">{{ articleData.dislikeNum }}</span>
        </div>
        <!-- <div class="icon-with-text" @click="showCollectDialog = true">
            <el-icon v-if="articleData.hasCollect" color="#409efc" :size="20">
                <StarFilled />
            </el-icon>
            <el-icon v-else :size="20">
                <StarFilled />
            </el-icon>
        </div> -->
    </div>
    <p>&nbsp;</p>

    <div class="reader-container">
        <div id="preview" />
    </div>

    <div class="view-count">
        阅读量：{{ articleData.viewCount + 1 }}
    </div>

    <CommentList />

</template>


<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import Vditor from 'vditor'
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import { getArticleApi, getVoteTypeApi, likeApi, dislikeApi, cancelLikeApi, cancelDislikeApi } from '../apis/apiArticle';
import CommentList from '../components/CommentList.vue';
import { useUserStore } from '../utils/stores';
import type { ArticleForm } from '@/utils/infs';

onMounted(() => {
    renderMarkdown()
    getVoteType()
})

// 路由
const route = useRoute()
const router = useRouter()
const userStoreObject = useUserStore()
function gotoUserPage(userName: string) {
    router.push("/user/" + userName)
}

// 文章
const articleUid = route.params.articleUid as string
const articleData = ref<ArticleForm>({
    articleUid: '',
    userName: '',
    nickName: '',
    avatarUrl: '',
    title: '',
    likeNum: 0,
    dislikeNum: 0,
    viewCount: 0,
    commentCount: 0,
    articleContent: '',
    createTime: ''
})
const voteType = ref()

async function getVoteType() {
    if (userStoreObject.isLogin) {
        try {
            const response = ref()
            response.value = await getVoteTypeApi(articleUid)
            if (response.value.code === 99999) {
                voteType.value = response.value.data
            } else {
                ElMessage.error("获取点赞状态失败")
            }
        } catch (error) {
            console.log(error)
            ElMessage.error("获取点赞状态失败")
        }
    } else {
        voteType.value = 0
    }
}


async function renderMarkdown() {
    try {
        const response = ref()
        response.value = await getArticleApi(articleUid)
        if (response.value.code === 99999) {
            Object.assign(articleData.value, response.value.data);
            Vditor.preview(document.getElementById("preview") as HTMLDivElement,
                response.value.data.articleContent,
                { mode: "light", hljs: { style: "github" } }
            );
        } else {
            router.push("/PageNotFound")
            // ElMessage.error("获取文章失败")
        }
    } catch (error) {
        console.log(error)
        ElMessage.error("获取文章失败")
    }
}

async function handleLike() {
    try {
        if (voteType.value == 0) {
            await likeApi(articleUid)
            voteType.value = 1
            ElMessage.success("点赞成功")
            articleData.value.likeNum += 1
        } else if (voteType.value == 1) {
            await cancelLikeApi(articleUid)
            voteType.value = 0
            ElMessage.success("取消点赞成功")
            articleData.value.likeNum -= 1
        } else if (voteType.value == 2) {
            await cancelDislikeApi(articleUid)
            await likeApi(articleUid)
            voteType.value = 1
            ElMessage.success("点赞成功")
            articleData.value.dislikeNum -= 1
            articleData.value.likeNum += 1
        }
    } catch (error) {
        ElMessage.error("点赞失败")
    }
}
async function handleDislike() {
    try {
        if (voteType.value == 0) {
            await dislikeApi(articleUid)
            ElMessage.success("点踩成功")
            voteType.value = 2
            articleData.value.dislikeNum += 1
        } else if (voteType.value == 1) {
            await cancelLikeApi(articleUid)
            await dislikeApi(articleUid)
            ElMessage.success("点踩成功")
            voteType.value = 2
            articleData.value.likeNum -= 1
            articleData.value.dislikeNum += 1
        } else if (voteType.value == 2) {
            await cancelDislikeApi(articleUid)
            ElMessage.success("取消点踩成功")
            voteType.value = 0
            articleData.value.dislikeNum -= 1
        }
    } catch (error) {
        ElMessage.error("点踩失败")
    }
}

</script>



<style scoped>
.title {
    text-align: center;
    font-family: "微软雅黑";
    font-size: 30px;
}

.reader-container {
    overflow: hidden;
    margin-left: 25%;
    margin-right: 25%;
}

.author-container {
    text-align: center;
}

.author {
    cursor: pointer;
}

.functions {
    display: flex;
    justify-content: flex-end; /* 让内容在容器内靠右对齐 */
    user-select: none;
    padding-bottom: 20px;
    margin-bottom: 20px;
    margin-right: 25%;
    
}

.icon-with-text {
    display: inline-flex;
    align-items: center; /* 垂直居中对齐图标和文字 */
    margin-left: 10px; /* 添加一些间距 */
    cursor: pointer;
}

.icon-text {
    margin-left: 5px; /* 调整数字与图标的距离 */
    font-size: 12px; /* 调整字体大小 */
}

.view-count {
    margin-top: 50px;
    margin-left: 25%;
    margin-right: 25%;
}
</style>