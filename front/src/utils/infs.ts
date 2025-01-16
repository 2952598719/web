

export interface LoginForm {
    userName: string;
    passWord: string;
}

export interface RegisterForm extends LoginForm{
    nickName: string;
}

export interface ArticleForm {
    articleUid: string;
    userName: string;
    nickName: string;
    title: string;
    articleContent: string;
    createTime: string;
}