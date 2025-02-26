

export interface LoginForm {
    userName: string;
    passWord: string;
    emailAddress: string;
    code: string;
}

export interface RegisterForm extends LoginForm{
    nickName: string;
}

export interface EmailForm {
    emailAddress: string;
}

export interface ArticleForm {
    articleUid: string;
    userName: string;
    nickName: string;
    avatarUrl: string;
    title: string;
    likeNum: number;
    dislikeNum: number;
    viewCount: number;
    commentCount: number;
    articleContent: string;
    createTime: string;
    tagStr: string;
    oldTagStr: string;
    hasCollect: boolean;
}

export interface CommentForm {
    commentUid: string;
    articleUid: string;
    userName: string;
    nickName: string;
    avatarUrl: string;
    replyUid: string;
    commentContent: string;
    likeNum: number;
    dislikeNum: number;
    voteType: boolean;
    realVoteType: number;
    createTime: string;

    replyType: number;
    replyComment: CommentForm;
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
    avatarUrl: string;
    avatarHash: string;
}

export interface PassWordForm {
    passWord: string;
}

export interface CollectionForm {
    collectionUid: string;
    collectionName: string;
    selected: boolean;
}

export interface Collection {
    collectionUid: string;
    collectionName: string;
}