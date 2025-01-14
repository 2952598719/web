

export interface LoginForm {
    userName: string;
    passWord: string;
}

export interface RegisterForm extends LoginForm{
    nickName: string;
}

export interface ArticleForm {
    title: string;
    articleContent: string;
}