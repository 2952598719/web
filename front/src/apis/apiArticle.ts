import type { ArticleForm } from "@/utils/infs"
import { vanillaRequest, tokenRequest } from "./requests"

export async function postArticleApi(data: ArticleForm) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/article',
        data: data
    })
    return response.data

}

export async function putArticleApi(articleUid: string, data: ArticleForm) {

    const response = await tokenRequest({
        method: 'PUT',
        url: '/article/' + articleUid,
        data: data
    })
    return response.data

}

export async function deleteArticleApi(articleUid: string) {
    const response = await tokenRequest({
        method: 'DELETE',
        url: '/article/' + articleUid
    })
    return response.data
}

export async function getArticleApi(articleUid: string) {

    const response = await vanillaRequest({
        method: 'GET',
        url: '/article/' + articleUid,
    })
    return response.data

}

export async function getHomeArticleListApi(currentPage: number, pageSize: number) {
    const response = await vanillaRequest({
        method: 'GET',
        url: '/articles/home',
        params: {
            currentPage: currentPage,
            pageSize: pageSize,
            searchParam: ""
        },
    })
    return response.data
}

export async function getUserPageArticleListApi(currentPage: number, pageSize: number, userName: string) {
    const response = await vanillaRequest({
        method: 'GET',
        url: '/articles/userPage',
        params: {
            currentPage: currentPage,
            pageSize: pageSize,
            userName: userName,
        },
    })
    return response.data
}

export async function getManageArticleListApi(currentPage: number, pageSize: number) {
    const response = await tokenRequest({
        method: 'GET',
        url: '/articles/manage',
        params: {
            currentPage: currentPage,
            pageSize: pageSize,
        },
    })
    return response.data
}

export async function getTitleArticleListApi(currentPage: number, pageSize: number, title: string) {
    const response = await vanillaRequest({
        method: 'GET',
        url: '/articles/search/' + title,
        params: {
            currentPage: currentPage,
            pageSize: pageSize,
        },
    })
    return response.data
}

export async function getVoteTypeApi(articleUid: string) {
    const response = await tokenRequest({
        method: 'GET',
        url: '/article/vote/' + articleUid,
    })
    return response.data
}

export async function likeApi(articleUid: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/like/' + articleUid,
    })
    return response.data

}
    
export async function dislikeApi(articleUid: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/dislike/' + articleUid,
    })
    return response.data

}

export async function cancelLikeApi(articleUid: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/like/' + articleUid,
    })
    return response.data

}

export async function cancelDislikeApi(articleUid: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/dislike/' + articleUid,
    })
    return response.data

}
