import type { Collection, CollectionForm } from "@/utils/infs"
import { vanillaRequest, tokenRequest } from "./requests"

export async function followCollectionApi(collectionUid: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/collection/follow/' + collectionUid,
    })
    return response.data

}

export async function unfollowCollectionApi(collectionUid: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/collection/follow/' + collectionUid,
    })
    return response.data

}

export async function checkFollowCollectionApi(collectionUid: string) {

    const response = await tokenRequest({
        method: 'GET',
        url: '/collection/follow/' + collectionUid,
    })
    return response.data

}

export async function createCollectionApi(data: Collection) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/collection',
        data: data
    })
    return response.data

}


export async function getCollectionOfArticleListApi(articleUid: string) {

    const response = await tokenRequest({
        method: 'GET',
        url: '/collection/article/' + articleUid,
    })
    return response.data

}

export async function getMyCollectionListApi() {
    const response = await tokenRequest({
        method: 'GET',
        url: '/collection/self'
    })
    return response.data

}

export async function getCollectionListApi(collectionName: string, currentPage: number, pageSize: number) {

    const response = await vanillaRequest({
        method: 'GET',
        url: '/collection/search/' + collectionName,
        params: {
            currentPage: currentPage,
            pageSize: pageSize,
        },
    })
    return response.data

}

export async function getCollectionApi(collectionUid: string) {

    const response = await tokenRequest({
        method: 'GET',
        url: '/collection/single/' + collectionUid,
    })
    return response.data

}

export async function deleteCollectionApi(collectionUid: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/collection/' + collectionUid,
    })
    return response.data

}

export async function modifyCollectionApi(collectionUid: string, data: CollectionForm) {

    const response = await tokenRequest({
        method: 'PUT',
        url: '/collection/' + collectionUid,
        data: data,
    })
    return response.data

}

export async function collectApi(collectionUid: string, articleUid: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/article/collection/' + collectionUid + '/' + articleUid,

    })
    return response.data

}

export async function uncollectApi(collectionUid: string, articleUid: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/article/collection/' + collectionUid + '/' + articleUid,
    })
    return response.data

}

