import type { LoginForm, RegisterForm } from "@/utils/infs";
import { vanillaRequest, tokenRequest } from "./requests"

export async function registerApi(form: RegisterForm) {
    const response = await vanillaRequest({
        method: 'POST',
        url: '/user/register',
        data: form,
    })
    return response.data
}

export async function loginApi(form: LoginForm) {
    const response = await vanillaRequest({
        method: 'POST',
        url: '/user/login',
        data: form,
    })
    return response.data
}

export async function logoutApi() {
    await tokenRequest({
        method: 'POST',
        url: '/user/logout',
    })
}

export async function checkLoginApi() {     // 如果token有效，则返回{ username, avatarUrl }，否则返回null
    const response = await tokenRequest({
        method: 'GET',
        url: '/user/checkLogin',
    })
    return response.data
}
