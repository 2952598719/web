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

export async function getArticleApi(articleUid: string) {

    const response = await vanillaRequest({
        method: 'GET',
        url: '/article/' + articleUid,
    })
    return response.data

}

export async function getArticleListApi(currentPage: number, pageSize: number, searchType: string, searchParam: string) {
    const response = await vanillaRequest({
        method: 'GET',
        url: '/articles/' + searchType,
        params: {
            currentPage: currentPage,
            pageSize: pageSize,
            // searchParam: searchParam
        },
    })
    return response.data
}


