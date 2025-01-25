<template>

    <el-page-header @back="gotoHome">
        <template #icon> <!--插槽1：图标-->
            <el-icon :size="20">
                <component :is="getIcon()" />
            </el-icon>
        </template>

        <template #title> <!--插槽2：页面标题-->
            {{ routeName }}
        </template>

        <template #extra> <!--插槽3：头像下拉框-->
            <el-button v-if="!userStoreObject.isLogin" @click="dialogVisible = true">登录/注册</el-button>
            <el-dropdown v-else @command="handleDropdown">
                <span @click="gotoUserPage" class="el-dropdown-link">
                    <el-avatar :size=40 :src="userStoreObject.avatarUrl" />
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="gotoCenter">个人中心</el-dropdown-item>
                        <el-dropdown-item command="gotoManage">文章管理</el-dropdown-item>
                        <el-dropdown-item command="logout">登出</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </template>
    </el-page-header>

    <el-divider />

    <el-dialog v-model="dialogVisible" @close="dialogClose" :lock-scroll="false">
        <el-tabs v-model="activeTab">
            <el-tab-pane label="登录" name="login">
                <el-form :model="loginForm">
                    <el-form-item label="用户名">
                        <el-input v-model="loginForm.userName" autocomplete="off" />
                    </el-form-item>
                    <el-form-item label="密码">
                        <el-input v-model="loginForm.passWord" autocomplete="off" type="password" show-password />
                    </el-form-item>
                </el-form>
                <div>
                    <el-button @click="submitLogin(loginForm)" type="primary">登录</el-button>
                    <el-button @click="dialogClose">取消</el-button>
                </div>
            </el-tab-pane>

            <el-tab-pane label="注册" name="register">
                <el-form :model="registerForm" :rules="registerRules">
                    <el-form-item label="用户名" prop="userName">
                        <el-input v-model="registerForm.userName" autocomplete="off" />
                    </el-form-item>
                    <el-form-item label="密码" prop="passWord">
                        <el-input v-model="registerForm.passWord" autocomplete="off" type="password" show-password />
                    </el-form-item>
                    <el-form-item label="昵称" prop="nickName">
                        <el-input v-model="registerForm.nickName" autocomplete="off" />
                    </el-form-item>
                </el-form>
                <div>
                    <el-button @click="submitRegister(registerForm)" type="primary">注册</el-button>
                    <el-button @click="dialogClose">取消</el-button>
                </div>
            </el-tab-pane>
        </el-tabs>
    </el-dialog>

</template>


<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/utils/stores';
import { loginApi, logoutApi, registerApi } from '@/apis/apiUser';
import { removeToken, setToken } from '@/utils/funcs';
import { ElMessage } from 'element-plus';
import { House, ArrowLeft } from '@element-plus/icons-vue';
import type { LoginForm, RegisterForm } from '@/utils/infs';

const userStoreObject = useUserStore()
const route = useRoute()
const router = useRouter()
const routeName = computed(() => route.name)
const dialogVisible = ref(false)
const activeTab = ref('login')

const loginForm = ref<LoginForm>({
    userName: '',
    passWord: '',
})
const registerForm = ref<RegisterForm>({
    userName: '',
    passWord: '',
    nickName: '',
})

function getIcon() {
    return route.name === 'Home' ? House : ArrowLeft;
}

function gotoHome() {
    router.push("/")
}

function gotoCenter() {
    router.push("/center")
}

function gotoUserPage() {
    router.push("/user/" + userStoreObject.userName)
}

function gotoManage() {
    router.push("/articleList/manage/a")
}

function dialogClose() {
    dialogVisible.value = false
    activeTab.value = 'login'
    loginForm.value = { userName: '', passWord: '' }    // 有机会改成resetFields
    registerForm.value = { userName: '', passWord: '', nickName: '' }
}

async function handleDropdown(command: string | number | object) {
    if(command === "gotoCenter") {
        gotoCenter()
    } else if(command === "gotoManage") {
        gotoManage()
    } else if(command === "logout") {
        try {
            await logoutApi()
            removeToken()
            userStoreObject.logout()
            ElMessage.success("登出成功")
            router.replace("/")
        } catch (error) {
            console.log(error)
            ElMessage.success("登出失败")
        }
    }
}

async function submitLogin(loginForm: LoginForm) {
    try {
        const response = await loginApi(loginForm)
        if (response.code === 99999) {
            const token = response.data.tokenValue
            if (token != null) {
                setToken(token)
                useUserStore().update()
                ElMessage.success('登陆成功')
            } else {
                ElMessage.error('token为空')
            }
        } else {
            ElMessage.error("用户名或密码错误")
        }
    } catch (error) {
        console.log(error)
        ElMessage.error('登陆失败')
    } finally {
        dialogVisible.value = false
        loginForm = { userName: '', passWord: '' }
        router.replace("/")
    }
}

async function submitRegister (registerForm: RegisterForm) {
    try {
        const response = await registerApi(registerForm)
        if (response.code === 99999) {
            ElMessage.success("注册成功")
        } else {
            ElMessage.error("注册失败，原因：" + response.msg)
        }
        submitLogin(registerForm)
    } catch (error) {
        console.log(error)
        ElMessage.error("注册失败")
    } finally {
        dialogVisible.value = false
        registerForm = { userName: '', passWord: '', nickName: '' }
        router.replace("/")
    }
}

const registerRules = ref({    // 登录无需验证
    userName: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 8, max: 20, message: "用户名长度必须在8到20", trigger: 'blur' },
        { pattern: /^[A-Za-z0-9]+$/, message: "用户名只能包含字母和数字", trigger: 'blur' }
    ],
    passWord: [ // 从后端复制正则表达式时要注意把//变成/
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, max: 20, message: "密码长度必须在8到20", trigger: 'blur' },
        { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]+$/, message: "密码包含至少1个字母，1个数字", trigger: 'blur' }
    ],
    nickName: [
        { required: true, message: '请输入昵称', trigger: 'blur' },
        { max: 255, message: "昵称长度必须小于255", trigger: 'blur' },
    ]
})

</script>


<style scoped>

.el-page-header {
    margin-top: 10px;
    padding-left: 20px;
    padding-right: 50px;
}

.el-divider {
    margin-top: 10px;
    margin-bottom: 10px;
}

.el-dropdown-link:focus-visible {
    outline: unset;
}

</style>