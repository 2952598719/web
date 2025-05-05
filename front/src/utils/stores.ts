import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getToken, setToken, getBackground, setBackground } from './funcs';
import { checkLoginApi } from '@/apis/apiUser';

async function processUserBasicInfo(): Promise<{ userName: string; avatarUrl: string } | null> {
    const token = getToken();
    if (!token) {
        return null;
    } else {
        try {
            const response = await checkLoginApi();
            if (response != null) {
                const userName = response.data.userName;
                const avatarUrl = response.data.avatarUrl;
                return { userName, avatarUrl };
            } else {
                console.error('获取用户信息失败');
                setToken('');
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
    const background = ref<string | null>(null);

    // Getter（计算属性）
    const isLogin = ref(false);

    const backgroundOptions = [
        { label: 'Default', url: '' },
        { label: 'Sea', url: 'https://s2.loli.net/2025/02/24/z5QBjPo6LZiWxrv.jpg' },
        { label: 'Sunny', url: 'https://s2.loli.net/2025/02/24/1Nlzr962LwYRSd3.jpg' },
        { label: 'Mount', url: 'https://s2.loli.net/2025/02/25/YnaIE7ky4QHfVFX.png' },
        { label: 'Space', url: 'https://s2.loli.net/2025/02/24/EeMXsYCbaNRB24r.jpg' },
    ];
    const backgroundMap = Object.fromEntries(
        backgroundOptions.map(option => [option.label, option.url])
    );

    // Actions
    async function update() {
        try {
            const userBasicInfo = await processUserBasicInfo();
            if (userBasicInfo != null) {
                userName.value = userBasicInfo.userName;
                avatarUrl.value = userBasicInfo.avatarUrl;
                isLogin.value = true;
            } else {
                userName.value = null;
                avatarUrl.value = "";
                isLogin.value = false
            }
        } catch (error) {
            userName.value = null;
            avatarUrl.value = "";
        }
    };

    function logout() {
        userName.value = null;  // isLogin的值通过这一条连带修改
        avatarUrl.value = null;
        isLogin.value = false;
    }

    function loadBackground() {
        try {
            const savedBg = getBackground() || '';
            background.value = savedBg ? backgroundMap[savedBg] : backgroundMap['Mount'];
        } catch (e) {
            console.error('读取本地存储失败:', e);
        }
    };
    
    function saveBackground(type: string) {
        try {
            setBackground(type);
        } catch (e) {
            console.error('保存到本地存储失败:', e);
        }
    };

    return {
        userName, avatarUrl, isLogin, background, update, logout, loadBackground, saveBackground
    };
});