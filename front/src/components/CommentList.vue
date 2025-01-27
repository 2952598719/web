<template>
    <div class="comment-area">
        <div class="comment-title">评论（{{ comments.length }}）</div>
        <el-card class="card" v-for="comment in comments" :key="comment.commentUid">
            <el-card class="inner-card" v-if="comment.replyType != 0">
                <div class="reply-comment" v-if="comment.replyType == 1">
                    <p>
                        <el-button @click="gotoUserPage(comment.replyComment.userName)" link>{{ comment.replyComment.nickName }}</el-button>
                        &nbsp;
                        <span class="date">{{ comment.createTime }}</span>
                    </p>
                    <div class="comment-detail" v-html="comment.replyComment.commentContent" />
                </div>
                <div v-else>
                    评论已删除
                </div>
            </el-card>
            <div class="commentor">
                <p class="author">
                    <el-button @click="gotoUserPage(comment.userName)" link>{{ comment.nickName }}</el-button>
                    &nbsp;
                    <span class="date">{{ comment.createTime }}</span>
                    <el-button class="delete-comment" v-if="comment.userName == userName"
                        @click="confirmDeleteComment(comment.commentUid)" type="danger" link>删除</el-button>
                </p>
                <div class="comment-detail" v-html="comment.commentContent" />
                <div class="functions" v-if="userStoreObject.isLogin">
                    <div class="icon-with-text" @click="handleLikeComment(comment)">
                        <el-icon v-if="comment.realVoteType == 1" color="#409efc" :size="20">
                            <CaretTop />
                        </el-icon>
                        <el-icon v-else :size="20">
                            <CaretTop />
                        </el-icon>
                        <span class="icon-text">{{ comment.likeNum }}</span>
                    </div>
                    <div class="icon-with-text" @click="handleDislikeComment(comment)">
                        <el-icon v-if="comment.realVoteType == 2" color="#409efc" :size="20">
                            <CaretBottom />
                        </el-icon>
                        <el-icon v-else :size="20">
                            <CaretBottom />
                        </el-icon>
                        <span class="icon-text">{{ comment.dislikeNum }}</span>
                    </div>
                    <div class="icon-with-text" v-if="userStoreObject.isLogin" @click="handleCommentComment(comment.commentUid, comment.nickName)">
                        <el-icon :size="16">
                            <Comment />
                        </el-icon>
                    </div>
                </div>
            </div>
        </el-card>
    </div>
    <el-pagination layout="prev, pager, next" :total="commentCount" :page-size="pageSize"
        :current-page="currentPage" @current-change="handlePageChange" background hide-on-single-page />
    <el-button v-if="userStoreObject.isLogin" class="fixed-button" type="primary" title="plus"
        @click="handleArticleComment">+</el-button>

    <el-drawer v-model="showDrawer" :title="drawerTitle" :direction="'btt'" :before-close="cancelComment" size="50%">
        <el-input class="comment-input" v-model="textarea" :rows="8" type="textarea" placeholder="请输入评论内容" />
        <div class="comment-buttons">
            <el-button type="primary" @click="submitComment">发表</el-button>
            <el-button type="default" @click="cancelComment">取消</el-button>
        </div>
    </el-drawer>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../utils/stores';
import { useRoute, useRouter } from 'vue-router';
import { getCommentListApi, postArticleCommentApi, postCommentCommentApi, deleteCommentApi, getCommentApi,
    likeCommentApi, cancelLikeCommentApi, dislikeCommentApi, cancelDislikeCommentApi
} from '../apis/apiComment';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { CommentForm } from '../utils/infs';

onMounted(() => {
    getComments(currentPage.value, pageSize.value)
})

// 路由
const route = useRoute()
const router = useRouter()
const articleUid = route.params.articleUid as string
function gotoUserPage(userName: string) {
    router.push("/user/" + userName)
}

// 个人信息
const userStoreObject = useUserStore()
const userName = userStoreObject.userName

// 获取评论
const textarea = ref('')
const commentCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const comments = ref<CommentForm[]>([])
const showDrawer = ref(false)
const drawerTitle = ref()
const commentType = ref(true)
const commentUid = ref()
const isLoading = ref(false)

async function getComments(currentPage: number, pageSize: number) {
    isLoading.value = true
    try {
        const response = (await getCommentListApi(currentPage, pageSize, articleUid)).data
        comments.value = response.list
        commentCount.value = response.total
        for (var i = 0; i <= comments.value.length - 1; i++) {
            if (comments.value[i].voteType == null) {
                comments.value[i].realVoteType = 0
            } else if (comments.value[i].voteType == true) {
                comments.value[i].realVoteType = 1
            } else {
                comments.value[i].realVoteType = 2
            }

            if(comments.value[i].replyUid != '0') {
                const response = await getCommentApi(comments.value[i].replyUid)
                if(response.data != null) {
                    comments.value[i].replyComment = response.data
                    comments.value[i].replyType = 1
                } else {
                    comments.value[i].replyType = 2
                }
            } else {
                comments.value[i].replyType = 0
            }
        }
    } catch (error) {
        console.log(error)
        ElMessage.error("获取评论失败")
    } finally {
        isLoading.value = false
    }
}

// 发表评论
async function submitComment() {
    try {
        if (textarea.value === '') {
            ElMessage.error("评论不能为空")
            return;
        }

        const commentContent = textarea.value.replace(/\n/g, '<br>')

        const response = commentType.value 
            ? await postArticleCommentApi(articleUid, commentContent)
            : await postCommentCommentApi(articleUid, commentUid.value, commentContent)

        if (response.code === 99999) {
            ElMessage.success("发表评论成功")
            cancelComment()
        } else {
            throw new Error("发表评论失败")
        }
    } catch (error) {
        console.log(error)
        ElMessage.error("发表评论失败")
    } finally {
        getComments(1, pageSize.value)
    }
}

async function handleArticleComment() {
    commentType.value = true
    showDrawer.value = true
    drawerTitle.value = "评论"
}

async function handleCommentComment(replyCommentUid: string, nickName: string) {
    commentType.value = false
    showDrawer.value = true
    commentUid.value = replyCommentUid
    drawerTitle.value = "回复 " + nickName
}

function cancelComment() {
    showDrawer.value = false
    textarea.value = ''
}

async function handlePageChange(newPage: number) {
    currentPage.value = newPage
    getComments(newPage, pageSize.value)
}

async function confirmDeleteComment(commentUid: string) {
    try {
        await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        await deleteThisComment(commentUid)
    } catch (error) {
        console.log(error)
    }
}

async function deleteThisComment(commentUid: string) {
    try {
        const response = await deleteCommentApi(commentUid)
        if (response.code === 99999) {
            ElMessage.success("成功删除评论")
            getComments(1, pageSize.value)
        } else {
            ElMessage.error("删除评论失败")
        }
    } catch (error) {
        ElMessage.error("删除评论失败")
        console.log(error)
    }
}

async function handleLikeComment(comment: CommentForm) {
    try {
        if (comment.realVoteType == 0) {
            await likeCommentApi(comment.commentUid)
            comment.realVoteType = 1
            comment.likeNum += 1
        } else if (comment.realVoteType == 1) {
            await cancelLikeCommentApi(comment.commentUid)
            comment.realVoteType = 0
            comment.likeNum -= 1
        } else if (comment.realVoteType == 2) {
            await cancelDislikeCommentApi(comment.commentUid)
            await likeCommentApi(comment.commentUid)
            comment.realVoteType = 1
            comment.dislikeNum -= 1
            comment.likeNum += 1
        }
    } catch (error) {
        console.log(error)
        ElMessage.error("操作失败")
    }
}

async function handleDislikeComment(comment: CommentForm) {
    try {
        if (comment.realVoteType == 0) {
            await dislikeCommentApi(comment.commentUid)
            comment.realVoteType = 2
            comment.dislikeNum += 1
        } else if (comment.realVoteType == 1) {
            await cancelLikeCommentApi(comment.commentUid)
            await dislikeCommentApi(comment.commentUid)
            comment.realVoteType = 2
            comment.likeNum -= 1
            comment.dislikeNum += 1
        } else if (comment.realVoteType == 2) {
            await cancelDislikeCommentApi(comment.commentUid)
            comment.realVoteType = 0
            comment.dislikeNum -= 1
        }
    } catch (error) {
        console.log(error)
        ElMessage.error("操作失败")
    }
}
</script>

<style scoped>
.functions {
    float: right;
    user-select: none;
    padding-bottom: 20px;
    margin-bottom: 20px;
}

.icon-with-text {
    position: relative;
    display: inline-block;
    margin-right: 10px;
    cursor: pointer;
}

.icon-text {
    position: absolute;
    bottom: -10px;
    left: 0;
    right: 0;
    text-align: center;
    font-size: 12px;
}

.comment-input {
    width: 100%;
}

.fixed-button {
    position: fixed;
    bottom: 50px;
    right: 50px;
    border-radius: 50%;
    width: 60px;
    height: 60px;
    z-index: 999;
    line-height: 50px;
    font-size: 40px;
}

.el-pagination {
    padding-top: 20px;
    justify-content: center;
}

.comment-buttons {
    padding-top: 20px;
    text-align: center;
}

.comment-title {
    font-family: "微软雅黑";
    font-size: 20px;
    padding-top: 20px;
    padding-bottom: 20px;
}

.delete-comment {
    float: right;
}

.card {
    margin-bottom: 50px;
}

.date {
    color: gray;
}

.comment-area {
    margin-left: 25%;
    margin-right: 25%;
}
</style>