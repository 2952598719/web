package top.orosirian.blog.utils;

public class RedisKeyConstants {
    // main key
    public static final String ARTICLE_HASH_KEY = "article:hash:%s";
    public static final String ARTICLE_LOCK_KEY = "article:lock:%s";
    public static final String EMAIL_CODE_KEY = "email:code:%s";
    public static final String EMAIL_ERROR_KEY = "email:error:%s";
    public static final String NOTIFICATION_UNREAD_KEY = "notification:unread:%s";
    // hash key
    public static final String HASH_VIEW_KEY = "viewCount";
    public static final String HASH_COMMENT_KEY = "commentCount";
    public static final String HASH_LIKE_KEY = "likeNum";
    public static final String HASH_DISLIKE_KEY = "dislikeNum";
}