<!-- src/views/NoticePage.vue -->
<template>
    <div class="notice-container">
        <!-- 通知列表 -->
        <div class="notice-list" v-infinite-scroll="loadMore" :infinite-scroll-disabled="loading">
            <!-- 骨架屏加载状态 -->
            <template v-if="loading && notices.length === 0">
                <el-skeleton v-for="i in 4" :key="i" animated class="notice-item-skeleton" />
            </template>

            <!-- 通知项 -->
            <el-card v-for="notice in notices" :key="notice.noticeUid" class="notice-item" shadow="hover">
                <div class="notice-content">
                    <!-- 类型图标 -->
                    <div class="notice-icon">
                        <el-icon :size="24">
                            <User v-if="notice.noticeType === 0" />
                            <Pointer v-if="notice.noticeType === 1 || notice.noticeType === 2" />
                            <Comment v-if="notice.noticeType === 3" />
                            <ChatDotRound v-if="notice.noticeType === 4" />
                        </el-icon>
                    </div>

                    <!-- 通知主体 -->
                    <div class="notice-main">
                        <span class="click" @click="handleClick(notice)">{{ getTypeText(notice) }}</span>

                        <!-- 时间显示 -->
                        <div class="notice-time">
                            <el-icon>
                                <Clock />
                            </el-icon>
                            {{ formatTime(notice.updateTime) }}
                        </div>
                    </div>
                </div>
            </el-card>

            <!-- 加载状态 -->
            <div v-if="loading" class="loading-more">
                <el-icon class="is-loading">
                    <Loading />
                </el-icon>
                加载中...
            </div>

            <!-- 无数据提示 -->
            <el-empty v-if="!loading && notices.length === 0" description="暂无通知" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted  } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Pointer, Comment, ChatDotRound, Clock, Loading } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getNoticeListApi } from '@/apis/apiUser'

import type { NoticeVO } from '../utils/infs'

const router = useRouter()

// 通知类型配置
const noticeTypes = [
    { value: 0, label: '关注', color: '#67C23A' },
    { value: 1, label: '文章点赞', color: '#409EFF' },
    { value: 2, label: '评论点赞', color: '#E6A23C' },
    { value: 3, label: '文章评论', color: '#909399' },
    { value: 4, label: '评论回复', color: '#F56C6C' }
]

// 响应式数据
const notices = ref<NoticeVO[]>([])
const loading = ref(false)
const pagination = reactive({
    page: 1,
    size: 10,
    total: 0
})

// 加载通知列表
const loadNotices = async (init = true) => {
    try {
        loading.value = true
        if (init) {
            pagination.page = 1
            notices.value = []
        }

        const res = await getNoticeListApi(pagination.page, pagination.size)
        pagination.total = res.total
        notices.value = res.data.list
        pagination.page++
    } finally {
        loading.value = false
    }
}

// 加载更多
const loadMore = () => {
    if (!loading.value && notices.value.length < pagination.total) {
        loadNotices(false)
    }
}

// 格式化时间显示
function formatTime(time: string) {
    return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 类型相关工具函数
function getTypeText(notice: NoticeVO) {
    if(notice.noticeType == 0) {
        return '用户 ' + notice.triggerName + ' 关注了你'
    } else if(notice.noticeType == 1) {
        return '用户 ' + notice.triggerName + ' 点赞了你的文章 ' + notice.subjectContent
    } else if(notice.noticeType == 2) {
        return '用户 ' + notice.triggerName + ' 点赞了你的评论 ' + notice.subjectContent
    } else if(notice.noticeType == 3) {
        return '用户 ' + notice.triggerName + ' 评论了你的文章 ' + notice.subjectContent
    } else {
        return '用户 ' + notice.triggerName + ' 回复了你的评论 ' + notice.subjectContent
    }
}

function handleClick(notice: NoticeVO) {
    if(notice.noticeType == 0) {
        router.push('/user/' + notice.triggerName)
    } else if(notice.noticeType == 1) {
        router.push('/article/' + notice.articleUid)
    } else if(notice.noticeType == 2) {
        router.push('/article/' + notice.articleUid)
    } else if(notice.noticeType == 3) {
        router.push('/article/' + notice.articleUid)
    } else {
        router.push('/article/' + notice.articleUid)
    }
}

// 初始化加载
onMounted(() => {
    loadNotices()
})
</script>

<style scoped>
.notice-container {
    background-color: white;
    border-radius: 25px;
    opacity: 0.9;
    max-width: 800px;
    margin: 20px auto;
    padding: 40px 40px;

    .filter-bar {
        margin-bottom: 20px;
        text-align: center;
    }

    .notice-list {
        min-height: 300px;
    }

    .notice-item {
        margin-bottom: 15px;
        transition: transform 0.2s;

        &:hover {
            transform: translateX(5px);
        }

        .notice-content {
            display: flex;
            align-items: center;
            padding: 12px;

            .notice-icon {
                margin-right: 16px;
            }

            .notice-main {
                flex: 1;

                .notice-header {
                    display: flex;
                    align-items: center;
                    margin-bottom: 8px;

                    .notice-type {
                        margin-right: 8px;
                    }

                    .trigger-name {
                        color: var(--el-color-primary);
                        cursor: pointer;

                        &:hover {
                            text-decoration: underline;
                        }
                    }
                }

                .preview-content {
                    color: #666;
                    margin-bottom: 8px;
                    cursor: pointer;
                    /* @include text-ellipsis(2); */
                }

                .notice-time {
                    font-size: 12px;
                    color: #999;
                    display: flex;
                    align-items: center;

                    .el-icon {
                        margin-right: 4px;
                    }
                }
            }
        }
    }

    .loading-more {
        text-align: center;
        padding: 15px;
        color: #999;
    }

    .notice-item-skeleton {
        margin-bottom: 15px;
        padding: 20px;
    }
}

.click {
    cursor: pointer;
}
</style>