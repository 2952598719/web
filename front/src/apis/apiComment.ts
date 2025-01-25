import { vanillaRequest, tokenRequest } from "./requests"

export async function postArticleCommentApi(articleUid: string, commentContent: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/comment/' + articleUid,
        
        data: {
            commentContent: commentContent,
        }
    })
    return response.data

}

export async function postCommentCommentApi(articleUid: string, commentUid: string, commentContent: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/comment/' + articleUid + '/' + commentUid,
        
        data: {
            commentContent: commentContent,
        }
    })
    return response.data

}

export async function deleteCommentApi(commentUid: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/comment/' + commentUid,
        
    })
    return response.data

}

export async function getCommentListApi(currentPage: number, pageSize: number, articleUid: string) {

    const response = await tokenRequest({
        method: 'GET',
        url: '/comment/article/' + articleUid,
        
        params: {
            currentPage: currentPage,
            pageSize: pageSize,
        },
    })
    return response.data

}

export async function getCommentApi(commentUid: string) {

    const response = await tokenRequest({
        method: 'GET',
        url: '/comment/' + commentUid,
        
    })
    return response.data

}


export async function likeCommentApi(commentUid: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/like/' + commentUid,
        
    })
    return response.data

}

export async function dislikeCommentApi(commentUid: string) {

    const response = await tokenRequest({
        method: 'POST',
        url: '/dislike/' + commentUid,
        
    })
    return response.data

}

export async function cancelLikeCommentApi(commentUid: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/like/' + commentUid,
        
    })
    return response.data

}

export async function cancelDislikeCommentApi(commentUid: string) {

    const response = await tokenRequest({
        method: 'DELETE',
        url: '/dislike/' + commentUid,
        
    })
    return response.data

}
