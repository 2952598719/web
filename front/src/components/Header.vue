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

        <template #extra>
            <div class="button-group">
                <el-button class="collect" link @click="gotoCollection" v-if="userStoreObject.isLogin">
                    <div class="icon-container">
                        <el-icon :size="30">
                            <Star />
                        </el-icon>
                        <span class="icon-text">收藏</span>
                    </div>
                </el-button>
                <el-button class="manage" link @click="gotoManage" v-if="userStoreObject.isLogin">
                    <div class="icon-container">
                        <el-icon :size="30">
                            <Document />
                        </el-icon>
                        <span class="icon-text">管理</span>
                    </div>
                </el-button>
                <el-button class="notice" link @click="gotoNotice" v-if="userStoreObject.isLogin">
                    <div class="icon-container">
                        <el-icon :size="30">
                            <Message />
                        </el-icon>
                        <span class="icon-text">通知</span>
                    </div>
                </el-button>
                <el-dropdown @command="handleBackground">
                    <el-button class="picture" link>
                        <div class="icon-container">
                            <el-icon :size="30">
                                <Picture />
                            </el-icon>
                            <span class="icon-text">背景</span>
                        </div>
                    </el-button>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="Default">Default</el-dropdown-item>
                            <el-dropdown-item command="Sea">Sea</el-dropdown-item>
                            <el-dropdown-item command="Sunny">Sunny</el-dropdown-item>
                            <el-dropdown-item command="Mount">Mount</el-dropdown-item>
                            <el-dropdown-item command="Space">Space</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
                <el-button v-if="!userStoreObject.isLogin" @click="dialogVisible = true">登录/注册</el-button>
                <el-dropdown v-else @command="handleDropdown">
                    <span @click="gotoUserPage" class="el-dropdown-link">
                        <el-avatar :size=40 :src="userStoreObject.avatarUrl" />
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="gotoCenter">个人中心</el-dropdown-item>
                            <el-dropdown-item command="logout">登出</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </template>
    </el-page-header>

    

    <el-dialog v-model="dialogVisible" @close="dialogClose" :lock-scroll="false">
        <el-tabs v-model="activeTab">
            <el-tab-pane label="登录" name="login">
                <el-form :model="loginForm" label-width="70px">

                    <el-form-item>
                        <!-- 登录方式选择器移动到表单项的label位置 -->
                        <template #label>
                            <el-dropdown @command="(command: 'username' | 'email') => loginMethod = command"
                                trigger="click">
                                <span class="label-dropdown">
                                    {{ loginMethod === 'username' ? '用户名' : '邮箱' }}
                                    <el-icon class="el-icon-right"><arrow-down /></el-icon>
                                </span>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item command="username">用户名</el-dropdown-item>
                                        <el-dropdown-item command="email">邮箱</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </template>

                        <!-- 保持原有输入框结构 -->
                        <el-input v-if="loginMethod === 'username'" v-model="loginForm.userName" autocomplete="off" />
                        <el-input v-else v-model="loginForm.emailAddress" autocomplete="off" />
                    </el-form-item>

                    <el-form-item :label="loginMethod === 'username' ? '密码' : '验证码'">
                        <template v-if="loginMethod === 'username'">
                            <el-input v-model="loginForm.passWord" type="password" show-password autocomplete="off" />
                        </template>
                        <template v-else>
                            <div class="code-input-container">
                                <el-input v-model="loginForm.code" autocomplete="off" />
                                <span class="send-code-text" @click="sendVerificationCode">
                                    {{ sendBtnText }}
                                </span>
                            </div>
                        </template>
                    </el-form-item>
                </el-form>
                <div class="dialog-footer">
                    <el-button @click="submitLogin" type="primary">登录</el-button>
                    <el-button @click="dialogClose">取消</el-button>
                </div>
            </el-tab-pane>

            <el-tab-pane label="注册" name="register">
                <el-form :model="registerForm" :rules="registerRules" label-width="70px">
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
                <div class="dialog-footer">
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
import { loginApi, emailLoginApi, logoutApi, registerApi, sendCodeApi } from '@/apis/apiUser';
import { removeToken, setToken } from '@/utils/funcs';
import { ElMessage } from 'element-plus';
import { House, ArrowLeft, Star, Document, Picture, ArrowDown, Message } from '@element-plus/icons-vue';
import type { LoginForm, RegisterForm } from '@/utils/infs';

const userStoreObject = useUserStore()
const route = useRoute()
const router = useRouter()
const routeName = computed(() => route.name)
const dialogVisible = ref(false)
const activeTab = ref('login')

// 新增登录相关状态
const loginMethod = ref<'username' | 'email'>('username');
const isSending = ref(false);
const countdown = ref(0);
const sendBtnText = ref('发送验证码');

const loginForm = ref<LoginForm>({
    userName: '',
    passWord: '',
    emailAddress: '',
    code: '',
})
const registerForm = ref<RegisterForm>({
    userName: '',
    passWord: '',
    nickName: '',
    emailAddress: '',
    code: '',
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
    router.push("/articleList/manage")
}

function gotoNotice() {
    router.push("/notice")
}

function gotoCollection() {
    router.push("/collection")
}

function dialogClose() {
    dialogVisible.value = false
    activeTab.value = 'login'
    loginForm.value = { userName: '', passWord: '', emailAddress: '', code: '' }    // 有机会改成resetFields
    registerForm.value = { userName: '', passWord: '', nickName: '', emailAddress: '', code: '' }
}

async function handleDropdown(command: string | number | object) {
    if (command === "gotoCenter") {
        gotoCenter()
    } else if (command === "logout") {
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

async function sendVerificationCode() {
    if (!loginForm.value.emailAddress) {
        ElMessage.warning('请输入邮箱地址');
        return;
    }
    if (isSending.value) return;

    try {
        isSending.value = true;
        const response = (await sendCodeApi({ emailAddress: loginForm.value.emailAddress }));
        if(response.code === 99999) {
            ElMessage.success('验证码已发送');
            startCountdown();
        } else {
            ElMessage.error(response.msg)
        }    
    } catch (error) {
        ElMessage.error('发送验证码失败');
        isSending.value = false;
    }
}
function startCountdown() {
    countdown.value = 60;
    const timer = setInterval(() => {
        countdown.value--;
        sendBtnText.value = `${countdown.value}秒后重发`;
        if (countdown.value <= 0) {
            clearInterval(timer);
            sendBtnText.value = '发送验证码';
            isSending.value = false;
        }
    }, 1000);
}

async function handleBackground(type: string | number | object) {
    userStoreObject.saveBackground(type as string)
    userStoreObject.loadBackground()
}

async function submitLogin() {
    try {
        let response;
        if (loginMethod.value === 'username') {
            response = await loginApi({
                userName: loginForm.value.userName,
                passWord: loginForm.value.passWord,
                emailAddress: '',
                code: ''
            });
        } else {
            response = await emailLoginApi({
                userName: '',
                passWord: '',
                emailAddress: loginForm.value.emailAddress,
                code: loginForm.value.code
            });
        }

        if (response.code === 99999) {
            const token = response.data.tokenValue;
            if (token) {
                setToken(token);
                useUserStore().update();
                ElMessage.success('登录成功');
            } else {
                ElMessage.error('token为空');
            }
        } else {
            ElMessage.error(response.msg || '登录失败');
        }
    } catch (error) {
        console.error(error);
        ElMessage.error('登录失败');
    } finally {
        dialogVisible.value = false;
        loginForm.value = { userName: '', passWord: '', emailAddress: '', code: '' };
        router.replace("/");
    }
}


async function submitRegister(registerForm: RegisterForm) {
    try {
        const response = await registerApi(registerForm)
        if (response.code === 99999) {
            ElMessage.success("注册成功")
            submitLogin()
        } else {
            ElMessage.error("注册失败，原因：" + response.msg)
        }
    } catch (error) {
        console.log(error)
        ElMessage.error("注册失败")
    } finally {
        dialogVisible.value = false
        registerForm = { userName: '', passWord: '', nickName: '', emailAddress: '', code: '' }
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
.code-input-container {
    display: flex;
    gap: 10px;
    align-items: center;
}

.el-page-header {
    background-color: white;
    padding-top: 10px;
    padding-bottom: 10px;
    padding-left: 20px;
    padding-right: 50px;
}



.el-dropdown-link:focus-visible {
    outline: unset;
}

.button-group {
    display: flex;
    align-items: center;
    gap: 12px;
    /* 调整这个值来控制按钮之间的间距 */
}

.button-group .el-button {
    margin-left: 0 !important;
    margin-right: 0 !important;
}

.icon-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    /* 控制图标和文字之间的间距 */
}

.collect,
.manage,
.notice,
.picture {
    display: flex;
    justify-content: center;
    /* 水平居中 */
    align-items: center;
    /* 垂直居中 */
    padding: 0;
    /* 移除默认按钮内边距 */
    margin-left: 4px;
    margin-right: 4px;
}

.icon-text {
    /* margin-top: 2px; */
    /* 调整文字与图标的间距 */
    font-size: 12px;
    /* 设置字体大小 */
    /* margin-left: 0; */
    margin-right: 6px;
}

.icon-container {
    display: flex;
    flex-direction: column;
    /* 垂直排列 */
    align-items: center;
    /* 子元素水平居中 */
}

.dropdown-trigger {
    cursor: pointer;
    padding: 0 8px;
    display: flex;
    align-items: center;
    color: var(--el-color-primary);
}

.code-input-container {
    position: relative;
    width: 100%;
}

.send-code-text {
    position: absolute;
    right: 5%;
    top: 50%;
    transform: translateY(-50%);
    color: var(--el-color-primary);
    cursor: pointer;
    font-size: 14px;
}

.send-code-text[disabled] {
    cursor: not-allowed;
    color: var(--el-text-color-placeholder);
}

.dialog-footer {
    margin-top: 20px;
    text-align: center;
}

.el-form-item {
    margin-bottom: 22px;
}

.label-dropdown {
    padding-top: 10px;
    cursor: pointer;
    color: var(--el-text-color-regular);
    display: flex;
    align-items: center;
    /* gap: 4px; */
}

.label-dropdown:hover {
    color: var(--el-color-primary);
}

/* 修复Element默认label样式 */
.el-form-item .el-form-item__label {
    padding: 10px 0 0 0;
    line-height: 1;
}

.el-input {
    width: 98%;
}
</style>