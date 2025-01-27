<template>

    <div class="input-div">
        <el-input class="input-class" v-model="title" show-word-limit maxlength="100" placeholder="请输入标题">
            <template #prepend><el-button :icon="Edit" /></template>
        </el-input>
        <div class="buttons">
            <el-button type="primary" @click="submit">发布</el-button>
        </div>
    </div>

    <el-input class="tag-area" v-model="tagStr" style="width: 500px" placeholder="请输入标签，以逗号分隔" />

    <div id="vditor"></div>

</template>


<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import Vditor from 'vditor'
import 'vditor/dist/index.css';
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import { Edit } from '@element-plus/icons-vue';

import { uploadImageApi } from '../apis/apiImage';
import { postArticleApi, putArticleApi, getArticleApi, getArticleTagApi } from '../apis/apiArticle';
import { useUserStore } from '../utils/stores';
import type { ArticleForm } from '@/utils/infs';

onMounted(async () => {
    if(userStoreObject.isLogin) {
        vditor.value = new Vditor('vditor', {
            height: '500px',
            // width: '50vw'
            mode: 'sv',
            upload: {
                handler: async (files) => {
                    return insertImage(files)
                }
            },
            after() {
                if (articleUid != null) {
                    getThisArticle(articleUid)
                    getArticleTag(articleUid)
                } else {
                    vditor.value.setValue('\n')
                }
            }
        })
    } else {
        router.push("/PageNotFound")
    }

    
})

// 路由
const route = useRoute()
const router = useRouter()
const articleUid = route.params.articleUid as string

// vditor
const vditor = ref()

// 其他信息
const title = ref('')
const oldTagStr = ref('')
const tagStr = ref('')
const userStoreObject = useUserStore()

// 函数
// 实在没办法弄成居中
async function insertImage(files: File[]) {
    try {
        const response = await uploadImageApi(files[0]);
        vditor.value.insertValue(`![图片](${response.url})`);
        ElMessage.success("图片上传成功");
        return response.url; // 返回图片 URL
    } catch (error) {
        ElMessage.error("图片上传失败");
        console.error('错误信息:', error);
        throw error; // 抛出错误，以便 Vditor 可以处理。没有这一句，返回类型上就会报错：函数缺少结束 return 语句，返回类型不包括 "undefined"
    }
}
async function getArticleTag(articleUid: string) {
    try {
        const articleTagResponse = await getArticleTagApi(articleUid)
        if (articleTagResponse.code === 99999) {
            oldTagStr.value = articleTagResponse.data
            tagStr.value = oldTagStr.value
        } else {
            console.error('Error fetching article');
        }
    } catch (error) {
        console.error('Errorfetching article:', error);
    }
}
async function getThisArticle(articleUid: string) {
    try {
        const articleResponse = await getArticleApi(articleUid)
        if (articleResponse.code === 99999) {
            const article = articleResponse.data
            title.value = article.title
            vditor.value.setValue(article.articleContent)
        } else {
            console.error('Error fetching article');
        }
    } catch (error) {
        console.error('Error fetching article:', error);
    }
}

async function submit() {
    try {
        if (title.value === '') {
            ElMessage.error("标题不能为空")
            return;
        }
        const content = vditor.value.getValue()
        if (content === "\n") {    // 注意，vditor为空时的值是换行
            ElMessage.error("文章内容不能为空")
            return;
        }
        const data = ref<ArticleForm>({
            title: title.value,
            articleContent: content,
            tagStr: tagStr.value,
            oldTagStr: oldTagStr.value,
            articleUid: '',
            userName: '',
            nickName: '',
            avatarUrl: '',
            likeNum: 0,
            dislikeNum: 0,
            viewCount: 0,
            commentCount: 0,
            createTime: '',
            hasCollect: false
        })

        const submitResponse = ref()
        if (articleUid == null) {
            submitResponse.value = await postArticleApi(data.value)
        } else {
            console.log(data.value)
            submitResponse.value = await putArticleApi(articleUid, data.value)
        }
        if (submitResponse.value.code === 99999) {
            ElMessage.success("成功发表文章")
            vditor.value.clearCache()
            router.push("/")
        } else {
            ElMessage.error("发表文章失败")
        }
    } catch (error) {
        console.error('Error adding article:', error);
        ElMessage.error("发表文章失败")
    }
}

</script>


<style>

.input-div {
    width: 1000px;
    padding-left: 20%;
    padding-right: 20%;
    padding-top: 20px;
    padding-bottom: 20px;
    display: flex;
}

.input-class {
    flex-grow: 1; /* 输入框占满剩余空间 */
    height: 32px;
    margin-right: 20px; /* 根据需要添加右边距，以便与按钮之间有间隔 */
}

.buttons {
    text-align: center;
}

.tag-area {
    text-align: center;
    width: 1000px;
    padding-left: 20%;
    /* padding-right: 20%; */
    display: flex;
}

</style>