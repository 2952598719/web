<template>
    <div class="title">{{ articleData.title }}</div>
    <div class="author-container">
        <span class="author" @click="gotoUserPage(articleData.userName)">作者：{{ articleData.nickName }}</span>
    </div>
    <div id="preview" />
    
</template>


<script setup>
import { ref, onMounted } from 'vue';
import Vditor from 'vditor'
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import { getArticleApi } from '../apis/apiArticle';

onMounted(() => {
    renderMarkdown()
})

// 路由
const route = useRoute()
const router = useRouter()
function gotoUserPage(userName) {
    router.push("/user/" + userName)
}

// 文章
const articleUid = route.params.articleUid
const articleData = ref({
    userName: null,
    nickName: null,
    title: null
})
async function renderMarkdown() {
    try {
        const response = ref([])
        response.value = await getArticleApi(articleUid)
        if (response.value.code === 99999) {
            Vditor.preview(document.getElementById("preview"), 
                            response.value.data.articleContent, 
                            { hljs: { style: "github" }, }
                        );
            Object.assign(articleData.value, response.value.data)
        } else {
            router.push("/PageNotFound")
            // ElMessage.error("获取文章失败")
        }
    } catch (error) {
        console.log(error)
        ElMessage.error("获取文章失败")
    }
}

</script>



<style scoped>

.title {
    text-align: center;
    font-family: "微软雅黑";
    font-size: 30px;
}

#preview {
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

</style>