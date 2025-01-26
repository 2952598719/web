<template>
    <div class="collection-container">
        <!-- 新增收藏夹按钮 -->
        <el-button type="primary" @click="dialogVisible = true">新增收藏夹</el-button>

        <!-- 收藏夹列表 -->
        <el-menu default-active="activeMenu" class="menu" @select="handleSelect">
            <el-menu-item v-for="collection in collections" :key="collection.collectionUid" :index="collection.collectionUid">
                {{ collection.collectionName }}
            </el-menu-item>
        </el-menu>

        <!-- 文章列表组件 -->
        <div class="article-list-container">
            <ArticleList :type="'collection'" :condition="selectedCollectionUid" />
        </div>

        <!-- 新增收藏夹对话框 -->
        <el-dialog v-model="dialogVisible" title="新增收藏夹" @close="resetForm">
            <el-form :model="createForm">
                <el-form-item label="收藏夹名" prop="name">
                    <el-input v-model="createForm.collectionName" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="createCollection">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import ArticleList from '@/components/ArticleList.vue'; // 假设ArticleList组件位于此路径下
import type { Collection, CollectionForm } from '@/utils/infs';
import { createCollectionApi, getMyCollectionListApi } from '@/apis/apiCollection';

onMounted(() => {
    getMyCollectionList();
});

// 示例数据：用户收藏夹列表
const collections = ref<CollectionForm[]>([]);

// 当前选中的收藏夹UID
const selectedCollectionUid = ref<string>("");

// 控制新增收藏夹对话框的显示状态
const dialogVisible = ref(false);

// 新增收藏夹表单数据
const createForm = ref<Collection>({
    collectionName: ''
});

// 处理菜单选择事件，更新选中的收藏夹UID
function handleSelect(collectionUid: string) {
    selectedCollectionUid.value = collectionUid;
}

// 创建新的收藏夹
async function createCollection() {
    const response = await createCollectionApi(createForm.value)
    if(response.code == 99999) {
        ElMessage.success("收藏夹创建成功")
    } else {
        ElMessage.error("收藏夹创建失败")
    }
    // 清空表单并关闭对话框
    resetForm();
    dialogVisible.value = false;
    getMyCollectionList();
}

// 重置表单
function resetForm() {
    createForm.value.collectionName = '';
}

async function getMyCollectionList() {
    const response = await getMyCollectionListApi()
    if(response.code == 99999) {
        collections.value = response.data
    } else {
        ElMessage.error("获取收藏夹列表失败")
    }
}
</script>

<style scoped>
.collection-container {
    display: flex;
    height: 100%;
}

.menu {
    width: 200px;
    margin-right: 20px;
}

.article-list-container {
    flex: 1;
}


</style>