<template>
    <span>
        <div class="avatar-container">
            <el-upload action="#" :http-request="uploadAvatarImage" :show-file-list="false">
                <el-avatar :size=100 :src="avatarUrl" shape="square"/>
                <!-- <el-button slot="trigger">修改头像</el-button> -->
            </el-upload>
        </div>
        <div class="right-button-head">
            <el-button type="primary" @click="infoDialogVisible = true">修改信息</el-button>
            <el-button type="primary" @click="passWordDialogVisible = true">修改密码</el-button>
            <el-button @click="userUnregister" type="danger">注销</el-button>
        </div>
    </span>

    <div class="description-container">
        <el-descriptions v-if="userInfo" :column="1" border>
            <template #extra></template>
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
            <el-descriptions-item label="个人介绍" align="center">{{ userInfo.biography }}</el-descriptions-item>
        </el-descriptions>
    </div>
    

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
                    <el-date-picker v-model="infoForm.birthday" 
                                    type="date" 
                                    format="YYYY/MM/DD" 
                                    value-format="YYYY-MM-DD"
                                    :disabled-date="disabledDate"/>
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
import { unregisterApi, getUserInfoApi, modifyPasswordApi, modifyUserInfoApi, modifyAvatarApi } from '../apis/apiUser';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../utils/stores';
import { useRoute, useRouter } from 'vue-router';
import { removeToken } from '../utils/funcs';
import type { PassWordForm, UserInfoForm, UserInfoFormDisplay } from '@/utils/infs';
import { deleteImageApi, uploadImageApi } from '@/apis/apiImage';

onMounted(() => {
    fetchUserInfo()
})

// 路由
const router = useRouter()

const userStoreObject = useUserStore()
const infoDialogVisible = ref(false)
const passWordDialogVisible = ref(false)

const userName = ref(userStoreObject.userName)
const avatarUrl = ref(userStoreObject.avatarUrl)

// 以下两个，userInfo用来展示，infoForm用来修改
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
const infoForm = ref<UserInfoForm>({
    'nickName': '',
    'gender': 0,
    'biography': '',
    'birthday': '',
    'phoneNumber': '',
    'emailAddress': '',
})
const passWordForm = ref<PassWordForm>({
    'passWord': '',
})


async function fetchUserInfo() {
    try {
        if(!userStoreObject.isLogin) {
            router.push("/")
        }
        const response = await getUserInfoApi(userName.value as string)
        Object.assign(userInfo.value, response.data);
        Object.assign(infoForm.value, userInfo.value);
    } catch (error) {
        console.log(error)
        ElMessage.error("获取用户信息失败")
    }
}

async function updateAvatar() {
    await userStoreObject.update()
    avatarUrl.value = userStoreObject.avatarUrl
}

async function submitInfoModify() {
    try {
        const actualInfoForm: Partial<Record<keyof UserInfoForm, string | number>> = {};
        for (const key in infoForm.value) {     // 遍历 infoForm 中的所有属性
            const value = infoForm.value[key as keyof UserInfoForm];
            if (value !== '') {
                actualInfoForm[key as keyof UserInfoForm] = value;
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

async function submitPassWordModify() {
    try {
        if (passWordForm.value.passWord === '') {
            ElMessage.info("密码未修改")
            return
        }
        const response = await modifyPasswordApi(passWordForm.value)
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

async function userUnregister() {
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

async function uploadAvatarImage(params: any) {
    try {
        const response = await uploadImageApi(params.file)
        if(response.success === false) {
            ElMessage.error("上传头像失败")
            return
        } else {
            if (userInfo.value.avatarHash != '') {
                await deleteImageApi(userInfo.value.avatarHash)
            }
            const tempAvatarUrl = response.data.url
            const tempAvatarHash = response.data.hash
            await modifyAvatarApi(tempAvatarUrl, tempAvatarHash)
            ElMessage.success("上传头像成功")
            updateAvatar()
        }
    } catch (error) {
        console.log(error)
        ElMessage.error("上传头像失败")
    }
}


function disabledDate(date: Date) {
    return date.getTime() > Date.now();
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
// 把正则从后端拿到前端时，要把中间双斜杠去掉
const passWordRules = ref({
    passWord: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, max: 20, message: "密码长度必须在8到20", trigger: 'blur' },
        { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]+$/, message: "密码包含至少1个字母，1个数字", trigger: 'blur' }
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

.avatar-container {
    display: flex;
    justify-content: center;
    padding-top: 20px;
}

.description-container {
    margin-left: 25%;
    margin-right: 25%;
}

</style>