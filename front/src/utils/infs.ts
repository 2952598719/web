

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

export interface UserInfoForm {
    nickName: string;
    gender: number;
    biography: string;
    birthday: string;
    phoneNumber: string;
    emailAddress: string;
}

export interface UserInfoFormDisplay extends UserInfoForm {
    userName: string;
    avatarHash: string;
}

export interface PassWordForm {
    passWord: string;
}

