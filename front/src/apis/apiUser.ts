import type { LoginForm, PassWordForm, RegisterForm, UserInfoForm } from "@/utils/infs";
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


export async function getUserInfoApi(userName: string) {  // 返回用户信息

    const response = await vanillaRequest({
        method: 'GET',
        url: '/user/info/' + userName,
    })
    return response.data

}

// 为什么form是{[key: string]: any}而不是UserInfoForm，因为传到后端的数据可能只有部分字段
export async function modifyUserInfoApi(form: {[key: string]: any}) {

    const response = await tokenRequest({
        method: 'PUT',
        url: '/user/info',
        data: form
    })
    return response.data

}

export async function modifyPasswordApi(form: PassWordForm) {

    const response = await tokenRequest({
        method: 'PUT',
        url: '/user/info/password',
        data: form,
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

// 关注部分
export async function followApi(masterName: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/user/follow/' + masterName,
        
    })
    return response.data

}

export async function unfollowApi(masterName: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/user/follow/' + masterName,
        
    })
    return response.data

}

export async function checkFollowApi(masterName: string) {

    const response = await tokenRequest({
        method: 'GET',
        url: '/user/follow/' + masterName,
        
    })
    return response.data.data

}

export async function getMasterNumApi(userName: string) {

    const response = await vanillaRequest({
        method: 'GET',
        url: '/user/info/' + userName + '/masterNum',
    })
    return response.data.data

}

// export async function getMasterListApi(userName: string) {

//     const response = await vanillaRequest({
//         method: 'GET',
//         url: '/user/info/' + userName + '/masterNum',
//     })
//     return response.data.data

// }

export async function getFanNumApi(userName: string) {

    const response = await vanillaRequest({
        method: 'GET',
        url: '/user/info/' + userName + '/fanNum',
    })
    return response.data.data

}

// export async function getFanListApi(userName: string) {

//     const response = await vanillaRequest({
//         method: 'GET',
//         url: '/user/info/' + userName + '/fanNum',
//     })
//     return response.data.data

// }

