<!-- <template>
    <span>
        <el-avatar src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
        <p>{{ userInfo.nickName }}</p>
        <p>关注数：{{ followCondition.masterNum }}</p>
        <p>粉丝数：{{ followCondition.fanNum }}</p>
        <p>个性签名：{{ userInfo.motto }}</p>
        <el-button v-if="showFollowButton === 1" @click="followUser" type="primary">关注</el-button>
        <el-button v-if="showFollowButton === 2" @click="unfollowUser" type="danger">取关</el-button>
    </span>

    <el-descriptions v-if="userInfo" :column="1" border>
        <el-descriptions-item label="用户名" align="center">{{ userInfo.userName }}</el-descriptions-item>
        <el-descriptions-item label="昵称" align="center">{{ userInfo.nickName }}</el-descriptions-item>
        <el-descriptions-item label="性别" align="center">{{ userInfo.sex }}</el-descriptions-item>
        <el-descriptions-item label="生日" align="center">{{ userInfo.birthday }}</el-descriptions-item>
        <el-descriptions-item label="手机号" align="center">{{ userInfo.phoneNumber }}</el-descriptions-item>
        <el-descriptions-item label="邮箱" align="center">{{ userInfo.emailAddress }}</el-descriptions-item>
        <el-descriptions-item label="学校" align="center">{{ userInfo.school }}</el-descriptions-item>
    </el-descriptions>

    <el-tabs v-model="activeName" class="demo-tabs">
        <el-tab-pane label="文章" name="article">
            <ArticleList :listType="4"/>
        </el-tab-pane>
        <el-tab-pane label="合集" name="collection">
            <CollectionList />
        </el-tab-pane>
    </el-tabs>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { followApi, unfollowApi, checkFollowApi, getUserInfoApi, getMasterNumApi, getFanNumApi } from '../apis/apiUser'
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getUserName } from '../utils/stores';
import ArticleList from '../components/lists/ArticleList.vue';
import CollectionList from '../components/lists/CollectionList.vue';

const route = useRoute()
const router = useRouter()
const userName = route.params.userName
const showFollowButton = ref(0) // 不显示为0，关注为1，取关为2
const followCondition = ref({
    'masterNum': 0,
    'fanNum': 0,
})

const activeName = ref('article')


const userInfo = ref({
    'userName': '',
    'avatarPicUid': '',
    'bgPicUid': '',
    'nickName': '',
    'sex': '',
    'birthday': '',
    'motto': '',
    'phoneNumber': '',
    'emailAddress': '',
    'school': '',
    'masterNum': 0,
    'fanNum': 0,
})
const sexDict = {
    '0': '保密',
    '1': '男',
    '2': '女',
}
async function fetchUserInfo() {
    try {
        const response = await getUserInfoApi(userName)
        if (response === "未找到用户") router.push("/pageNotFound")
        for (var key in response) {
            if (key === 'sex') {
                userInfo.value[key] = sexDict[response[key]]
            } else {
                userInfo.value[key] = response[key]
            }
        }

        followCondition.value.masterNum = await getMasterNumApi(userName)
        followCondition.value.fanNum = await getFanNumApi(userName)
    } catch (error) {
        console.log(error)
        ElMessage.error("获取用户信息失败")
    }
}


async function checkFollowButton() {
    try {
        if (userName === getUserName()) {
            showFollowButton.value = 0
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

const followUser = async () => {
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

const unfollowUser = async () => {
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
.functions {
    float: left;
    padding-bottom: 20px;
    user-select: none;
}

.icon-with-text {
    position: relative;
    display: inline-block;
    /* 使其可以相对定位 */
    margin-right: 10px;
    /* 添加一些间距 */
}

.icon-text {
    position: absolute;
    bottom: -10px;
    /* 调整数字与图标的距离 */
    left: 0;
    right: 0;
    text-align: center;
    font-size: 12px;
    /* 调整字体大小 */
}

.category {
    float: right;
    color: gray;
    cursor: pointer;
}

.el-pagination {
    padding-top: 20px;
    justify-content: center;
}

.card {
    margin-bottom: 20px;
}
</style> -->