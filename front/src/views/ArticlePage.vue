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
        <div class="icon-with-text" @click="handleCollection">
            <el-icon v-if="articleData.hasCollect" color="#409efc" :size="20">
                <StarFilled />
            </el-icon>
            <el-icon v-else :size="20">
                <StarFilled />
            </el-icon>
        </div>
    </div>

    <div class="reader-container">
        <div id="preview" />
    </div>

    <div class="view-count">
        阅读量：{{ articleData.viewCount + 1 }}
    </div>

    <div class="tag-area">
        <el-button v-for="tag in tags" text round bg @click="gotoTag(tag)">{{ tag }}</el-button>
    </div>

    <CommentList />

    <el-dialog v-model="showCollectDialog" :lock-scroll="false" :title="'收藏'" @close="showCollectDialog = false">
        <el-checkbox v-for="collection in collectionList" v-model="collection.selected" border>
            {{ collection.collectionName }}
        </el-checkbox>
        <div class="mid-button">
            <el-button type="primary" @click="submitCollect">提交</el-button>
            <el-button @click="showCollectDialog = false">取消</el-button>
        </div>
    </el-dialog>

</template>


<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import Vditor from 'vditor'
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import { getArticleApi, getVoteTypeApi, likeApi, dislikeApi, cancelLikeApi, cancelDislikeApi, getArticleTagApi } from '../apis/apiArticle';
import CommentList from '../components/CommentList.vue';
import { useUserStore } from '../utils/stores';
import type { CollectionForm, ArticleForm } from '@/utils/infs';
import { collectApi, getCollectionOfArticleListApi, uncollectApi } from '@/apis/apiCollection';

onMounted(() => {
    renderMarkdown()
    getVoteType()
})

// 路由
const route = useRoute()
const router = useRouter()
const tags = ref<string[]>([])
const userStoreObject = useUserStore()

const collectionList = ref<CollectionForm[]>([])
const showCollectDialog = ref(false)
const originList = ref<boolean[]>([])
function gotoUserPage(userName: string) {
    router.push("/user/" + userName)
}

function gotoTag(tagName: string) {
    router.push("/articleList/tag/" + tagName)
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
    createTime: '',
    tagStr: '',
    oldTagStr: '',
    hasCollect: false,
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
        tags.value = (await getArticleTagApi(articleUid)).data.split(", ")
        if (tags.value.length == 1 && tags.value[0] == "") {
            tags.value = []
        }
        if (response.value.code === 99999) {
            Object.assign(articleData.value, response.value.data);
            Vditor.preview(document.getElementById("preview") as HTMLDivElement,
                response.value.data.articleContent,
                { mode: "light", icon: "ant"  }
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

async function getCollections() {
    try {
        const response = await getCollectionOfArticleListApi(articleUid)
        collectionList.value = response.data
        originList.value = []   // 清空originList
        articleData.value.hasCollect = false
        collectionList.value.forEach((data) => {
            originList.value.push(data.selected)
            if (data.selected) {
                articleData.value.hasCollect = true
            }
        })
    } catch (error) {
        console.log(error)
        ElMessage.error("获取合集失败")
    }
}

async function submitCollect() {
    for (var i = 0; i <= collectionList.value.length - 1; i++) {
        if (originList.value[i] && !collectionList.value[i].selected) {
            try {
                await uncollectApi(collectionList.value[i].collectionUid, articleUid); // 等待操作完成
            } catch (error) {
                console.log(error);
                ElMessage.error("取消收藏失败")
            }
        } else if (!originList.value[i] && collectionList.value[i].selected) {
            try {
                await collectApi(collectionList.value[i].collectionUid, articleUid); // 等待操作完成
            } catch (error) {
                console.log(error);
                ElMessage.error("收藏失败")
            }
        }
    }
    ElMessage.success("修改收藏成功")
    await getCollections(); // 等待获取收藏情况完成
    showCollectDialog.value = false
}

async function handleCollection() {
    await getCollections()
    showCollectDialog.value = true
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
    margin-top: 50px;
    margin-bottom: 25px;
    border: 1px solid rgba(0, 0, 0, 0.2);
    /* 添加边框，1px 宽度，黑色 */
    border-radius: 10px;
    /* 添加圆角，10px 的圆角半径 */
    padding: 30px;
    /* 可选：添加内边距，使内容与边框之间有一定的距离 */
    background-color: white;
}

.author-container {
    display: flex;
    /* 使用 flex 布局 */
    align-items: center;
    /* 垂直居中 */
    justify-content: center;
    /* 水平居中（可选，根据需求调整） */
    text-align: left;
    /* 文本左对齐 */
    margin-top: 20px;
    gap: 10px;
    /* 头像和作者名字之间的间距 */
}

.author {
    cursor: pointer;
    font-size: 16px;
    /* 调整字体大小 */
    margin-left: 10px;
    /* 如果需要额外的间距 */
}

.functions {
    display: flex;
    justify-content: flex-end;
    /* 让内容在容器内靠右对齐 */
    user-select: none;
    padding-bottom: 20px;
    margin-bottom: 20px;
    margin-right: 25%;

}

.icon-with-text {
    display: inline-flex;
    align-items: center;
    /* 垂直居中对齐图标和文字 */
    margin-left: 10px;
    /* 添加一些间距 */
    cursor: pointer;
}

.icon-text {
    margin-left: 5px;
    /* 调整数字与图标的距离 */
    font-size: 12px;
    /* 调整字体大小 */
}

.view-count {
    margin-top: 50px;
    margin-left: 25%;
    margin-right: 25%;
}

.tag-area {
    margin-top: 20px;
    margin-left: 5%;
    margin-right: 25%;
}

.mid-button {
    text-align: center;
    padding-top: 10px;
}

a {
    text-decoration: none;
}

a:hover {
    text-decoration: underline;
}
</style>