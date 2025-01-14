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




