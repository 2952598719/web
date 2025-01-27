<template>
    <div class="collection-container">
        <!-- 左侧收藏夹列表 -->
        <div class="sidebar">
            <!-- 新增收藏夹按钮 -->
            <el-button type="primary" @click="dialogVisible = true" class="add-button">新增收藏夹</el-button>

            <!-- 收藏夹列表 -->
            <el-menu default-active="activeMenu" class="menu" @select="handleSelect">
                <el-menu-item v-for="collection in collections" :key="collection.collectionUid" :index="collection.collectionUid">
                    <span class="collection-name">{{ collection.collectionName }}</span>
                    <el-icon class="icon" @click.stop="openEditDialog(collection)"><EditPen /></el-icon>
                    <el-icon class="icon" @click.stop="confirmDelete(collection.collectionUid)"><Delete /></el-icon>
                </el-menu-item>
            </el-menu>
        </div>

        <!-- 右侧文章列表 -->
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

        <!-- 编辑收藏夹对话框 -->
        <el-dialog v-model="editDialogVisible" title="编辑收藏夹" @close="resetEditForm">
            <el-form :model="editForm">
                <el-form-item label="收藏夹名" prop="name">
                    <el-input v-model="editForm.collectionName" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="updateCollection">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 删除确认弹窗 -->
        <el-dialog v-model="deleteDialogVisible" title="确认删除" width="30%">
            <span>确定要删除这个收藏夹吗？</span>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="deleteDialogVisible = false">取消</el-button>
                    <el-button type="danger" @click="deleteCollection">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { EditPen, Delete } from '@element-plus/icons-vue'; // 引入图标组件
import ArticleList from '@/components/ArticleList.vue'; // 假设ArticleList组件位于此路径下
import type { Collection, CollectionForm } from '@/utils/infs';
import { createCollectionApi, getMyCollectionListApi, modifyCollectionApi, deleteCollectionApi } from '@/apis/apiCollection';

onMounted(() => {
    getMyCollectionList();
});

// 示例数据：用户收藏夹列表
const collections = ref<CollectionForm[]>([]);

// 当前选中的收藏夹UID
const selectedCollectionUid = ref<string>("");

// 控制新增收藏夹对话框的显示状态
const dialogVisible = ref(false);

// 控制编辑收藏夹对话框的显示状态
const editDialogVisible = ref(false);

// 控制删除确认弹窗的显示状态
const deleteDialogVisible = ref(false);

// 新增收藏夹表单数据
const createForm = ref<Collection>({
    collectionUid: '',
    collectionName: ''
});

// 编辑收藏夹表单数据
const editForm = ref<Collection>({
    collectionUid: '',
    collectionName: ''
});

// 待删除的收藏夹UID
const deleteCollectionUid = ref<string>("");

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

// 打开编辑对话框
function openEditDialog(collection: CollectionForm) {
    editForm.value = { ...collection };
    editDialogVisible.value = true;
}

// 更新收藏夹
async function updateCollection() {
    const response = await modifyCollectionApi(editForm.value.collectionUid, editForm.value)
    if(response.code == 99999) {
        ElMessage.success("收藏夹更新成功")
    } else {
        ElMessage.error("收藏夹更新失败")
    }
    // 清空表单并关闭对话框
    resetEditForm();
    editDialogVisible.value = false;
    getMyCollectionList();
}

// 确认删除收藏夹
function confirmDelete(collectionUid: string) {
    deleteCollectionUid.value = collectionUid;
    deleteDialogVisible.value = true;
}

// 删除收藏夹
async function deleteCollection() {
    const response = await deleteCollectionApi(deleteCollectionUid.value)
    if(response.code == 99999) {
        ElMessage.success("收藏夹删除成功")
    } else {
        ElMessage.error("收藏夹删除失败")
    }
    deleteDialogVisible.value = false;
    getMyCollectionList();
}

// 重置新增表单
function resetForm() {
    createForm.value.collectionName = '';
}

// 重置编辑表单
function resetEditForm() {
    editForm.value = {
        collectionUid: '',
        collectionName: ''
    };
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
    height: 100vh; /* 使容器占满整个视口高度 */
    padding: 20px;
    box-sizing: border-box;
    background-color: #f5f7fa; /* 背景颜色 */
}

.sidebar {
    width: 240px; /* 左侧宽度 */
    margin-right: 20px;
    background-color: #ffffff; /* 白色背景 */
    border-radius: 8px; /* 圆角 */
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 阴影 */
    padding: 20px;
    box-sizing: border-box;
}

.add-button {
    width: 100%; /* 按钮宽度 */
    margin-bottom: 20px; /* 按钮与菜单之间的间距 */
}

.menu {
    border-right: none; /* 移除菜单右侧边框 */
}

.article-list-container {
    flex: 1; /* 右侧文章列表占满剩余空间 */
    background-color: #ffffff; /* 白色背景 */
    border-radius: 8px; /* 圆角 */
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 阴影 */
    padding: 20px;
    box-sizing: border-box;
}

.icon {
    margin-left: 5px;
    cursor: pointer;
}

.icon:hover {
    color: #A1A3B4; /* 鼠标悬停时的图标颜色 */
    transition: color 0.3s;
}

.collection-name {
    margin-right: 80px;
}

/* 响应式布局 */
@media (max-width: 768px) {
    .collection-container {
        flex-direction: column;
    }

    .sidebar {
        width: 100%;
        margin-right: 0;
        margin-bottom: 20px;
    }

    .article-list-container {
        width: 100%;
    }
}
</style>