<template>
    <span>
        <div class="avatar-container">
            <el-upload action="#" :http-request="uploadAvatarImage" :show-file-list="false">
                <el-avatar :size=100 :src="avatarUrl" shape="square" />
            </el-upload>
        </div>
        <div class="right-button-head">
            <el-button color="#010101" @click="infoDialogVisible = true">修改信息</el-button>
            <el-button color="#1868b2" @click="passWordDialogVisible = true">修改密码</el-button>
            <el-button type="danger" @click="userUnregister">注销</el-button>
        </div>
    </span>

    <el-dialog v-model="infoDialogVisible" @close="infoDialogVisible = false" :lock-scroll="false">
        <el-form :model="infoForm" :rules="infoRules">
            <el-form-item label="昵称" prop="nickName">
                <el-input v-model="infoForm.nickName" autocomplete="off" />
            </el-form-item>
            <el-form-item label="性别">
                <el-select v-model="infoForm.gender">
                    <el-option :key="0" :value="0" label="保密" />
                    <el-option :key="1" :value="1" label="男" />
                    <el-option :key="2" :value="2" label="女" />
                </el-select>
            </el-form-item>
            <el-form-item label="生日">
                <el-col>
                    <el-date-picker v-model="infoForm.birthday" type="date" format="YYYY/MM/DD"
                        value-format="YYYY-MM-DD" :disabled-date="disabledDate" />
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

    <div class="info-cards">
        <el-card class="info-card">
            <div class="info-label">用户名</div>
            <div class="info-value">{{ userInfo.userName }}</div>
        </el-card>
        <el-card class="info-card">
            <div class="info-label">昵称</div>
            <div class="info-value">{{ userInfo.nickName }}</div>
        </el-card>
        <el-card class="info-card">
            <div class="info-label">性别</div>
            <div class="info-value">
                <span v-if="userInfo.gender === 0">保密</span>
                <span v-else-if="userInfo.gender === 1">男</span>
                <span v-else-if="userInfo.gender === 2">女</span>
            </div>
        </el-card>
        <el-card class="info-card">
            <div class="info-label">生日</div>
            <div class="info-value">{{ userInfo.birthday }}</div>
        </el-card>
        <el-card class="info-card">
            <div class="info-label">手机号</div>
            <div class="info-value">{{ userInfo.phoneNumber }}</div>
        </el-card>
        <el-card class="info-card">
            <div class="info-label">邮箱</div>
            <div class="info-value">{{ userInfo.emailAddress }}</div>
        </el-card>
        <el-card class="info-card">
            <div class="info-label">个人介绍</div>
            <div class="info-value">{{ userInfo.biography }}</div>
        </el-card>
    </div>

    <el-dialog v-model="passWordDialogVisible" @close="passWordDialogClose" :lock-scroll="false">
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
    'avatarUrl': '',
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
        if (!userStoreObject.isLogin) {
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
        if (response.success === false) {
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
/* 整体布局 */
.user-center-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    background-color: #f5f5f5;
    min-height: 100vh;
}

/* 头像区域 */
.avatar-container {
    display: flex;
    justify-content: center;
    margin-left: 46%;
    margin-right: 46%;
    padding-top: 5px;
    padding-bottom: 5px;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.avatar-container:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.el-avatar {
    border: 2px solid #409EFF;
    cursor: pointer;
}

/* 按钮区域 */
.right-button-head {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-bottom: 20px;
}

.el-button {
    border-radius: 5px;
    transition: background-color 0.3s ease, transform 0.3s ease;
}

.el-button:hover {
    transform: translateY(-2px);
}

.el-button--primary {
    background: linear-gradient(135deg, #409EFF, #66b1ff);
    border: none;
}

.el-button--danger {
    background: linear-gradient(135deg, #F56C6C, #f78989);
    border: none;
}

/* 描述信息区域 */
.description-container {
    width: 60%;
    background-color: #fff;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.el-descriptions {
    margin-top: 20px;
}

.el-descriptions-item__label {
    font-weight: bold;
    color: #333;
}

.el-descriptions-item__content {
    color: #666;
}

/* 对话框样式 */
.el-dialog {
    border-radius: 10px;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.el-form-item__label {
    font-weight: bold;
    color: #333;
}

.el-input,
.el-select,
.el-date-picker {
    width: 100%;
}

.mid-button {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .description-container {
        width: 90%;
    }

    .right-button-head {
        flex-direction: column;
        align-items: center;
    }
}

.info-cards {
    display: grid;
    grid-template-columns: repeat(3, minmax(200px, 1fr)); /* 每行至少 200px，自动适应列数 */
    gap: 20px; /* 卡片之间的间距 */
    padding: 20px;
}

.info-card {
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    padding: 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-between; /* 内容均匀分布 */
    height: auto; /* 高度自适应内容 */
}

.info-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.info-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
}

.info-value {
    font-size: 16px;
    color: #333;
    font-weight: bold;
}

/* 让最后一张卡片占满整行 */
.info-card:last-child:nth-child(3n + 1) {
    grid-column: 1 / -1; /* 占据所有列 */
}
</style>