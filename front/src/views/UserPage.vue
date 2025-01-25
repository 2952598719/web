<template>
	<div class="app-container">
		<!-- 左边部分 -->
		<div class="sidebar">

			<div class="user-info-top">
				<el-avatar :src="userInfo.avatarUrl" />
				<div class="info-text">
					<p>{{ userInfo.nickName }}</p>
					<p @click="openFollowersDialog">关注数：{{ followCondition.masterNum }}</p>
					<p @click="openFansDialog">粉丝数：{{ followCondition.fanNum }}</p>
					<p>个性签名：{{ userInfo.biography }}</p>
				</div>
				<div class="buttons">
					<el-button v-if="showFollowButton === 1" @click="followUser" type="primary">关注</el-button>
					<el-button v-if="showFollowButton === 2" @click="unfollowUser" type="danger">取关</el-button>
				</div>
			</div>

			<el-descriptions v-if="userInfo" :column="1" border class="user-details">
				<el-descriptions-item label="用户名" align="center">{{ userInfo.userName }}</el-descriptions-item>
				<el-descriptions-item label="昵称" align="center">{{ userInfo.nickName }}</el-descriptions-item>
				<el-descriptions-item label="性别" align="center">
					<span v-if="userInfo.gender === 0">保密</span>
					<span v-else-if="userInfo.gender === 1">男</span>
					<span v-else-if="userInfo.gender === 2">女</span>
				</el-descriptions-item>
				<el-descriptions-item label="生日" align="center">{{ userInfo.birthday }}</el-descriptions-item>
				<el-descriptions-item label="手机号" align="center">{{ userInfo.phoneNumber }}</el-descriptions-item>
				<el-descriptions-item label="邮箱" align="center">{{ userInfo.emailAddress }}</el-descriptions-item>
			</el-descriptions>

		</div>

		<!-- 右边部分 -->
		<div class="content">
			<ArticleList :type="'userPage'" :key="keyValue"/>
		</div>
	</div>

	<el-dialog v-model="dialogVisible" @close="dialogVisible=false" :lock-scroll="false">
		<template #header>
			<span v-if="isFollowers">{{ userName }}的关注</span>
			<span v-else>{{ userName }}的粉丝</span>
		</template>
		<el-table :data="tableData" style="width: 100%" @row-click="handleRowClick">
			<el-table-column label="">
				<template #default="scope">
					<el-avatar :src="scope.row.avatarUrl"></el-avatar>
				</template>
			</el-table-column>
			<el-table-column prop="nickName" label=""></el-table-column>
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

function changeKey(){
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
	/* 水平排列 */
}

.sidebar {
	width: 33.33%;
	/* 左边占三分之一 */
	/* background-color: #f0f2f5; */
	border-color: #000000;
	/* 背景颜色可根据需要调整 */
	padding: 50px;
	box-sizing: border-box;
	overflow-y: auto;
	/* 如果内容超出，则可以滚动 */
}

.user-info-top {
	display: flex;
	align-items: center;
}

.info-text {
	margin-left: 15px;
}

.buttons {
	margin-top: 10px;
}

.user-details {
	margin-top: 20px;
}

.content {
	flex-grow: 2;
	/* 右边占三分之二 */
	padding: 20px;
	box-sizing: border-box;
}
</style>