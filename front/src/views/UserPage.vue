<template>
	<div class="app-container">
		<!-- 左边部分 -->
		<div class="sidebar">

			<div class="user-info-top">
				<el-avatar :src="userStoreObject.avatarUrl" />
				<div class="info-text">
					<p>{{ userInfo.nickName }}</p>
					<p>关注数：{{ followCondition.masterNum }}</p>
					<p>粉丝数：{{ followCondition.fanNum }}</p>
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
			<ArticleList :type="'userPage'"/>
		</div>
	</div>

</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import { checkFollowApi, followApi, getFanNumApi, getMasterNumApi, getUserInfoApi, unfollowApi } from '../apis/apiUser'
import { useUserStore } from '../utils/stores';
import ArticleList from '../components/ArticleList.vue';
import type { UserInfoFormDisplay } from '../utils/infs';

const route = useRoute()
const router = useRouter()
const userName = route.params.userName as string
const showFollowButton = ref(0) // 不显示为0，关注为1，取关为2
const followCondition = ref({
	'masterNum': 0,
	'fanNum': 0,
})

const userStoreObject = useUserStore()

const userInfo = ref<UserInfoFormDisplay>({
	'userName': '',
	'nickName': '',
	'gender': 0,
	'biography': '',
	'birthday': '',
	'phoneNumber': '',
	'emailAddress': '',
	'avatarHash': '',
})
const sexDict = {
	'0': '保密',
	'1': '男',
	'2': '女',
}
async function fetchUserInfo() {
	try {
		const response = await getUserInfoApi(userName)
		if (response.code === 20002) router.push("/pageNotFound")
		Object.assign(userInfo.value, response.data);

		followCondition.value.masterNum = await getMasterNumApi(userName)
		followCondition.value.fanNum = await getFanNumApi(userName)
	} catch (error) {
		console.log(error)
		ElMessage.error("获取用户信息失败")
	}
}


async function checkFollowButton() {
    try {
		if(userName === userStoreObject.userName) {
			showFollowButton.value = 0;
		} else {
			const isFollowed = await checkFollowApi(userName)
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
        const response = await followApi(userName)
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
        const response = await unfollowApi(userName)
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