<template>
    <span>
        <el-upload action="#" :http-request="uploadAvatarImage" :show-file-list="false">
            <el-avatar :size=100 :src="avatarUrl" />
            <el-button slot="trigger">修改头像</el-button>
        </el-upload>
        <p>{{ userInfo.nickName }}</p>
        <div class="right-button-head">
            <el-button type="primary" @click="infoDialogVisible = true">修改信息</el-button>
            <el-button type="primary" @click="passWordDialogVisible = true">修改密码</el-button>
            <el-button @click="userUnregister" type="danger">注销</el-button>
        </div>
    </span>
    <el-descriptions v-if="userInfo" :column="3" border>
        <template #extra></template>
        <el-descriptions-item label="用户名" align="center">{{ userInfo.userName }}</el-descriptions-item>
        <el-descriptions-item label="昵称" align="center">{{ userInfo.nickName }}</el-descriptions-item>
        <el-descriptions-item label="性别" align="center">
            <span v-if="userInfo.gender === '0'">保密</span>
            <span v-else-if="userInfo.gender === '1'">男</span>
            <span v-else-if="userInfo.gender === '2'">女</span>
        </el-descriptions-item>
        <el-descriptions-item label="生日" align="center">{{ userInfo.birthday }}</el-descriptions-item>
        <el-descriptions-item label="手机号" align="center">{{ userInfo.phoneNumber }}</el-descriptions-item>
        <el-descriptions-item label="邮箱" align="center">{{ userInfo.emailAddress }}</el-descriptions-item>
        <el-descriptions-item label="个人介绍" align="center">{{ userInfo.biography }}</el-descriptions-item>
    </el-descriptions>

    <el-dialog v-model="infoDialogVisible" @close="infoDialogVisible = false">
        <el-form :model="infoForm" :rules="infoRules">
            <el-form-item label="昵称" prop="nickName">
                <el-input v-model="infoForm.nickName" autocomplete="off" />
            </el-form-item>
            <el-form-item label="性别">
                <el-select v-model="infoForm.gender">
                    <el-option :key="0" :value="0" label="保密" /> <!--此处的label前面不能打冒号-->
                    <el-option :key="1" :value="1" label="男" />
                    <el-option :key="2" :value="2" label="女" />
                </el-select>
            </el-form-item>
            <el-form-item label="生日">
                <el-col>
                    <el-date-picker v-model="infoForm.birthday" type="date" />
                </el-col>
            </el-form-item>
            <el-form-item label="个性签名" prop="biography">
                <el-input v-model="infoForm.biography" autocomplete="off" />
            </el-form-item>
            <el-form-item label="手机号" prop="phoneNumber">
                <el-input v-model="infoForm.phoneNumber" autocomplete="off" />
            </el-form-item>
            <el-form-item label="邮箱" prop="emailAddress">
                <el-input v-model="infoForm.emailAddress" autocomplete="off" />
            </el-form-item>
        </el-form>
        <div class="mid-button">
            <el-button @click="submitInfoModify" type="primary">提交</el-button>
            <el-button @click="infoDialogVisible = false">取消</el-button>
        </div>
    </el-dialog>

    <el-dialog v-model="passWordDialogVisible" @close="passWordDialogClose">
        <el-form :model="passWordForm" :rules="passWordRules">
            <el-form-item label="密码" prop="passWord">
                <el-input v-model="passWordForm.passWord" autocomplete="off" type="password" show-password />
            </el-form-item>
        </el-form>
        <div class="mid-button">
            <el-button @click="submitPassWordModify" type="primary">提交</el-button>
            <el-button @click="passWordDialogVisible = false">取消</el-button>
        </div>
    </el-dialog>

</template>


<script lang="ts" setup>
// TODO: 逻辑正确之后加入未登录的判断逻辑
import { ref, reactive, onMounted } from 'vue'
import { unregisterApi, getInfoApi, modifyPasswordApi, modifyUserInfoApi, getAvatarApi, modifyAvatarApi } from '../apis/apiUser';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../utils/stores';
import { useRoute, useRouter } from 'vue-router';
import { removeToken } from '../utils/funcs';
import { uploadImageApi, deleteImageApi } from '../apis/apiImage';

onMounted(() => {
    fetchUserInfo()
})

// 路由
const router = useRouter()

const userStoreObject = useUserStore()
const infoDialogVisible = ref(false)
const passWordDialogVisible = ref(false)

const avatarUrl = ref()
const avatarHash = ref()
const userName = userStoreObject.userName

async function fetchUserInfo() {
    try {
        const response = await getInfoApi(userName as string)
        const avatar = await getAvatarApi()
        for (var key in response) {
            userInfo.value[key] = response[key]
        }
        for (var key in infoForm.value) {   // 填充infoForm，方便修改
            infoForm.value[key] = userInfo.value[key]
        }

        avatarUrl.value = (avatar.data == null) ? "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" : avatar.data.imageUrl
        avatarHash.value = (avatar.data == null) ? null : avatar.data.imageHash
    } catch (error) {
        console.log(error)
        ElMessage.error("获取用户信息失败")
    }
}


interface UploadParams {
    file: File;
}

async function uploadAvatarImage(params: UploadParams) {
    try {
        const avatar = await uploadImageApi(params.file)
        if (avatarHash.value != null) {
            await deleteImageApi(avatarHash.value)
        }
        const tempAvatarUrl = avatar.url
        const tempAvatarHash = avatar.hash
        await modifyAvatarApi(tempAvatarUrl, tempAvatarHash)
        avatarUrl.value = tempAvatarUrl
        avatarHash.value = tempAvatarHash
    } catch (error) {
        console.log(error)
        ElMessage.error("上传头像失败")
    }
}

// 以下两个，userInfo用来展示，infoForm用来修改
const userInfo = ref<{ [key: string]: string }>({
    'userName': '',
    'nickName': '',
    'gender': '',
    'biography': '',
    'birthday': '',
    'phoneNumber': '',
    'emailAddress': '',
})
const infoForm = ref<{ [key: string]: string }>({
    'nickName': '',
    'gender': '',
    'biography': '',
    'birthday': '',
    'phoneNumber': '',
    'emailAddress': '',
})
const passWordForm = ref<{ [key: string]: string }>({
    'passWord': '',
})

const submitInfoModify = async () => {
    try {
        const actualInfoForm: { [key: string]: string } = {}
        for (const key in infoForm.value) {  // 这里是个值得注意的地方。当传到后端的参数对象中，没有某个变量，那么就不会对它检验，导致空值进入数据库，因此需要在前端过滤掉
            if (infoForm.value[key] !== '') {
                actualInfoForm[key] = infoForm.value[key]
            }
        }
        const response = await modifyUserInfoApi(actualInfoForm)
        if (response.code === 99999) {
            ElMessage.success("修改信息成功")
        } else {
            ElMessage.error("修改信息失败，原因" + response.msg)
        }
    } catch (error) {
        console.log(error)
        ElMessage.error('修改信息失败')
    } finally {
        infoDialogVisible.value = false
        fetchUserInfo()
    }
}

const submitPassWordModify = async () => {
    try {
        if (passWordForm.value.passWord === '') {
            ElMessage.info("密码未修改")
            return
        }
        const actualpassWordForm: { [key: string]: string } = {}
        for (const key in passWordForm.value) {  // 这里是个值得注意的地方。当传到后端的参数对象中，没有某个变量，那么就不会对它检验，因此需要在前端过滤掉
            if (passWordForm.value[key] !== '') {
                actualpassWordForm[key] = passWordForm.value[key]
            }
        }
        const response = await modifyPasswordApi(actualpassWordForm)
        if (response.code === 99999) {
            ElMessage.success("修改密码成功")
        } else {
            ElMessage.error("修改密码失败，原因" + response.msg)
        }
    } catch (error) {
        console.log(error)
        ElMessage.error('修改密码失败')
    } finally {
        passWordDialogVisible.value = false
        // 修改密码后没必要重新加载表格
    }
}

const passWordDialogClose = () => {
    passWordDialogVisible.value = false
    passWordForm.value = { 'passWord': '' }
}

const userUnregister = async () => {
    try {
        const response = await unregisterApi()
        if (response.code === 99999) {
            removeToken()
            userStoreObject.logout()
            ElMessage.success("注销成功")
            router.push("/")
        }
    } catch (error) {
        console.log(error)
        ElMessage.error('注销失败')
    } finally {

    }
}


const infoRules = ref({
    nickName: [
        { max: 255, message: "昵称长度必须小于255", trigger: 'blur' },
    ],
    motto: [
        { max: 255, message: "个性签名长度必须小于255", trigger: 'blur' },
    ],
    phoneNumber: [
        // { len: 11, message: "请输入正确的手机号", trigger: 'blur' }
        { pattern: /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/, message: "请输入正确的手机号", trigger: 'blur' }
    ],
    emailAddress: [
        { pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/, message: "请输入正确的邮箱", trigger: 'blur' }
    ]
})

const passWordRules = ref({
    passWord: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, max: 20, message: "密码长度必须在8到20", trigger: 'blur' },
        { pattern: /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)[A-Za-z\d]+$/, message: "密码包含至少1个大写，1个小写，1个数字", trigger: 'blur' }
    ],
})
</script>


<style lang="css" scoped>
.cell-item {
    display: flex;
    align-items: center;
}

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

/* .left-button {
    float: left;
  } */

.mid-button {
    text-align: center;
}

.right-button {
    float: right;
}

.right-button-head {
    float: right;
    padding-bottom: 20px;
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

.collection-function {
    padding-bottom: 20px;
}
</style>