import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getToken, setToken } from './funcs';
import { checkLoginApi } from '@/apis/apiUser';

async function processUserBasicInfo(): Promise<{ userName: string; avatarUrl: string } | null> {
    const token = getToken();
    if (!token) {
        console.log('token不存在');
        return null;
    } else {
        try {
            const response = await checkLoginApi();
            if (response != null) {
                const userName = response.data.userName;
                const avatarUrl = response.data.avatarUrl;
                return { userName, avatarUrl };
            } else {
                return null;
            }
        } catch(error) {
            console.error('获取用户信息失败: ', error);
            setToken('');
            return null;
        }
    }
}

export const useUserStore = defineStore('auth', () => {
    // State
    const userName = ref<string | null>(null);
    const avatarUrl = ref<string | null>(null);

    // Getter（计算属性）
    const isLogin = computed(() => !!userName.value);

    // Actions
    async function update() {
        try {
            const userBasicInfo = await processUserBasicInfo();
            if (userBasicInfo) {
                userName.value = userBasicInfo.userName;
                avatarUrl.value = userBasicInfo.avatarUrl;
            }
        } catch (error) {
            userName.value = null;
            avatarUrl.value = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";
        }
    };

    function logout() {
        userName.value = null;  // isLogin的值通过这一条连带修改
        avatarUrl.value = null;
    }

    return {
        userName, avatarUrl, isLogin, update, logout
    };
});