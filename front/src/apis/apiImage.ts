import axios from "axios";

// 这里没有指定目标网址，是因为在vite.config.ts中指定，开头为/img的请求会导向目标网址
export async function uploadImageApi(file: File) {
    const formData = new FormData();
    formData.append('smfile', file);

    const config = {
        headers: {
            'Authorization': 'hm8jhRJXWrXFiiJO65IE0xq5a8vXeTMc',
            'Content-Type': 'multipart/form-data',
        }
    };

    const response = await axios.post('/img/upload', formData, config);
    return response.data.data;
}

export async function deleteImageApi(hash: string) {
    const config = {
        headers: {
            'Authorization': 'hm8jhRJXWrXFiiJO65IE0xq5a8vXeTMc',
        }
    }
    const response = await axios.get('/img/delete/' + hash, config)
    return response.data
}