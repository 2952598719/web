import axios from 'axios'
import { getToken } from '../utils/funcs'

axios.defaults.headers['Content-Type'] = "application/json;charset=utf-8"

// const vanillaRequest = axios.create({
//     baseURL: "http://localhost:8081/api/blog",
// })

// const tokenRequest = axios.create({
//     baseURL: "http://localhost:8081/api/blog",
// })

const vanillaRequest = axios.create({
    baseURL: "http://orosirian.top:8081/api/blog",
})

const tokenRequest = axios.create({
    baseURL: "http://orosirian.top:8081/api/blog",
})

tokenRequest.interceptors.request.use(   // 发送请求前的拦截器
    (config) => {     // 比如request.get({...})：config就是大括号部分，可能包含url, data之类的参数
        const token = getToken()
        if(token != null) {
            config.headers['Authorization'] = 'Bearer ' + token
        }
        return config
    },
    (error) => {
        console.error('错误信息:', error)
        return Promise.reject(error)   // Promise用来传递到处理链的后面，类似于抛出异常
    }
)

export { vanillaRequest, tokenRequest }