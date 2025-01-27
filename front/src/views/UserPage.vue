<template>
	<div class="app-container">
		<!-- 左边部分 -->
		<div class="sidebar">
			<div class="user-info-top">
				<el-avatar :src="userInfo.avatarUrl" />
				<div class="info-text">
					<p>{{ userInfo.nickName }}</p>
					<div class="follow-info">
						<p @click="openFollowersDialog">关注数：{{ followCondition.masterNum }}</p>
						<p @click="openFansDialog">粉丝数：{{ followCondition.fanNum }}</p>
					</div>
					<p>简介：{{ userInfo.biography }}</p>
				</div>
				<div class="buttons">
					<el-button text type="primary" v-if="showFollowButton === 1" @click="followUser">关注</el-button>
					<el-button text type="danger" v-if="showFollowButton === 2" @click="unfollowUser">取关</el-button>
				</div>
			</div>

			<el-descriptions v-if="userInfo" :column="1" class="user-details">
				<el-descriptions-item label="用户名" align="left">{{ userInfo.userName }}</el-descriptions-item>
				<el-descriptions-item label="昵称" align="left">{{ userInfo.nickName }}</el-descriptions-item>
				<el-descriptions-item label="性别" align="left">
					<span v-if="userInfo.gender === 0">保密</span>
					<span v-else-if="userInfo.gender === 1">男</span>
					<span v-else-if="userInfo.gender === 2">女</span>
				</el-descriptions-item>
				<el-descriptions-item label="生日" align="left">{{ userInfo.birthday }}</el-descriptions-item>
				<el-descriptions-item label="手机号" align="left">{{ userInfo.phoneNumber }}</el-descriptions-item>
				<el-descriptions-item label="邮箱" align="left">{{ userInfo.emailAddress }}</el-descriptions-item>
			</el-descriptions>
		</div>

		<!-- 右边部分 -->
		<div class="content">
			<ArticleList :type="'userPage'" :key="keyValue" />
		</div>
	</div>

	<el-dialog v-model="dialogVisible" @close="dialogVisible = false" :lock-scroll="false">
		<template #header>
			<span v-if="isFollowers">{{ userName }}的关注</span>
			<span v-else>{{ userName }}的粉丝</span>
		</template>
		<el-table :data="tableData" style="width: 100%" @row-click="handleRowClick" :border="false" empty-text="无用户数据">
			<el-table-column label="" align="left">
				<template #default="scope">
					<el-avatar :src="scope.row.avatarUrl"></el-avatar>
				</template>
			</el-table-column>
			<el-table-column prop="nickName" label="" align="left"></el-table-column>
		</el-table>
		<el-pagination layout="prev, pager, next" :total="total" @current-change="handlePageChange"></el-pagination>
	</el-dialog>
</template>


<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import { checkFollowApi, followApi, getFanNumApi, getFanListApi, getMasterNumApi, getMasterListApi, getUserInfoApi, unfollowApi } from '../apis/apiUser'
import { useUserStore } from '../utils/stores';
import ArticleList from '../components/ArticleList.vue';
import type { UserInfoFormDisplay } from '../utils/infs';

const route = useRoute()
const router = useRouter()
const userName = ref(route.params.userName as string)
const showFollowButton = ref(0) // 不显示为0，关注为1，取关为2
const followCondition = ref({
	'masterNum': 0,
	'fanNum': 0,
})

const dialogVisible = ref(false);
const isFollowers = ref(true);
const tableData = ref([]);
const total = ref(0);
const currentPage = ref<number>(1);
const pageSize = ref<number>(1);
const keyValue = ref(1)

const userStoreObject = useUserStore()

const userInfo = ref<UserInfoFormDisplay>({
	'userName': '',
	'nickName': '',
	'gender': 0,
	'biography': '',
	'birthday': '',
	'phoneNumber': '',
	'emailAddress': '',
	'avatarUrl': '',
	'avatarHash': '',
})
async function fetchUserInfo() {
	try {
		const response = await getUserInfoApi(userName.value)
		if (response.code === 20002) router.push("/pageNotFound")
		Object.assign(userInfo.value, response.data);

		followCondition.value.masterNum = await getMasterNumApi(userName.value)
		followCondition.value.fanNum = await getFanNumApi(userName.value)
	} catch (error) {
		console.log(error)
		ElMessage.error("获取用户信息失败")
	}
}


async function checkFollowButton() {
	try {
		if (userName.value === userStoreObject.userName) {
			showFollowButton.value = 0;
		} else {
			const isFollowed = await checkFollowApi(userName.value)
			if (!isFollowed) showFollowButton.value = 1
			else showFollowButton.value = 2
		}
	} catch (error) {
		console.log(error)
		ElMessage.error("检查关注情况失败")
	}
}

async function followUser() {
	try {
		const response = await followApi(userName.value)
		if (response.code === 99999) {
			ElMessage.success("关注成功")
			showFollowButton.value = 2
			fetchUserInfo()
		}
	} catch (error) {
		console.log(error)
		ElMessage.error("关注失败")
	} finally {

	}
}

async function unfollowUser() {
	try {
		const response = await unfollowApi(userName.value)
		if (response.code === 99999) {
			ElMessage.success("取关成功")
			showFollowButton.value = 1
			fetchUserInfo()
		}
	} catch (error) {
		console.log(error)
		ElMessage.error("取关失败")
	} finally {

	}
}

async function fetchFollowList(isFollowers: boolean) {
	try {
		const api = isFollowers ? getMasterListApi : getFanListApi;
		const response = await api(userName.value, currentPage.value, pageSize.value);
		tableData.value = response.list;
		total.value = response.total;
	} catch (error) {
		console.error(error);
		ElMessage.error("加载失败");
	}
}

function openFollowersDialog() {
	isFollowers.value = true;
	fetchFollowList(true);
	dialogVisible.value = true;
}

function openFansDialog() {
	isFollowers.value = false;
	fetchFollowList(false);
	dialogVisible.value = true;
}

function handlePageChange(page: number) {
	currentPage.value = page;
	fetchFollowList(isFollowers.value);
}

function handleRowClick(row: any) {
	router.push("/user/" + row.userName)
}

function changeKey() {
	keyValue.value += 1
}

watch(() => route.params.userName, async (newUserName) => {
	if (newUserName) {
		userName.value = newUserName as string;
		dialogVisible.value = false
		await fetchUserInfo();
		checkFollowButton();
		changeKey()
	}
}, { immediate: true }); // 立即执行一次以初始化

onMounted(() => {
	fetchUserInfo();
	checkFollowButton();
})


</script>


<style scoped>
.app-container {
	display: flex;
	flex-direction: row;
	background-color: #ffffff;
	/* 背景颜色 */
}

.sidebar {
	margin-left: 50px;
	width: 33.33%;
	padding: 20px;
	box-sizing: border-box;
	background-color: #ffffff;
	/* 白色背景 */
	/* box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); */
	/* 添加阴影 */
	overflow-y: auto;
}

.user-info-top {
	display: flex;
	align-items: center;
	margin-bottom: 20px;
}

.user-info-top .el-avatar {
	width: 80px;
	height: 80px;
	margin-right: 20px;
}

.info-text {
	/* flex-grow: 1; */
	padding-right: 50px;
}

.info-text p {
	margin: 5px 0;
	font-size: 14px;
	color: #000000;
	/* 文字颜色 */
}

.follow-info {
	display: flex;
	gap: 10px;
	/* 关注数和粉丝数之间的间距 */
}

.follow-info p {
	cursor: pointer;
	/* 鼠标悬停时显示为手型 */
	color: rgba(0, 0, 0, 0.2);
	/* 统一设置为蓝色 */
	margin: 0;
	/* 移除默认的 margin */
	font-size: 14px;
	/* 统一字体大小 */
}

.follow-info p:hover {
	text-decoration: none;
	/* 鼠标悬停时无下划线 */
}

.buttons {
	margin-top: 10px;
}

.buttons .el-button {
	width: 100%;
	margin-top: 10px;
}

.user-details {
	margin-top: 20px;
}

.user-details .el-descriptions-item__label {
	font-weight: bold;
	color: #303133;
	/* 标签颜色 */
}

.user-details .el-descriptions-item__content {
	color: #606266;
	/* 内容颜色 */
}

.content {
	flex-grow: 2;
	padding: 20px;
	box-sizing: border-box;
	background-color: #ffffff;
	/* 白色背景 */
	/* box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); */
	/* 添加阴影 */
}

/* 表格无边框且左对齐 */
.el-table {
	border: none !important;
}

.el-table::before {
	height: 0 !important;
}

.el-table th,
.el-table td {
	border: none !important;
	text-align: left !important;
	/* 内容左对齐 */
}

/* 响应式布局 */
@media (max-width: 768px) {
	.app-container {
		flex-direction: column;
	}

	.sidebar {
		width: 100%;
		padding: 15px;
	}

	.content {
		width: 100%;
		padding: 15px;
	}

	.user-info-top {
		flex-direction: column;
		align-items: flex-start;
	}

	.user-info-top .el-avatar {
		margin-right: 0;
		margin-bottom: 10px;
	}

	.buttons .el-button {
		width: auto;
	}
}
</style>