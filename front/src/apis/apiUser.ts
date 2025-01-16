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

export async function unregisterApi() {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/user/unregister',
        
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


export async function getInfoApi(userName: string) {  // 返回用户信息

    const response = await vanillaRequest({
        method: 'GET',
        url: '/user/info/' + userName,
    })
    if (response.data.code === 10005) return "未找到用户"
    else return response.data.data

}


export async function modifyUserInfoApi(data: any) {

    const response = await tokenRequest({
        method: 'PUT',
        url: '/user/info',
        
        data: data
    })
    return response.data

}

export async function modifyPasswordApi(data: any) {

    const response = await tokenRequest({
        method: 'PUT',
        url: '/user/info/password',
        
        data: data,
    })
    return response.data

}


export async function getAvatarApi() {

    const response = await tokenRequest({
        method: 'GET',
        url: '/user/avatar',
        
    })
    return response.data

}


export async function modifyAvatarApi(avatarUrl: string, avatarHash: string) {

    const response = await tokenRequest({
        method: 'PUT',
        url: '/user/info/avatar',
        
        params: {
            avatarUrl: avatarUrl,
            avatarHash: avatarHash,
        }
    })
    return response.data

}

