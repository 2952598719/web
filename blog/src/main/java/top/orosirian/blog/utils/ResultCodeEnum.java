package top.orosirian.blog.utils;

public enum ResultCodeEnum {

    SUCCESS(99999, "成功"),
    // 1xxxx 通用
    UNKNOWN(10000, "未知错误"),
    PARAMETER_NOT_VALID(10001, "参数无效"),
    // 2xxxx 用户相关
    USERNAME_EXIST(20001, "用户名已存在"),  // 注册
    USERNAME_NOT_EXIST(20002, "用户名不存在"),  // 登录
    PASSWORD_WRONG(20003, "密码和用户名不对应"),
    USER_NOT_EXIST(20004, "用户不存在"),
    USER_FOLLOW_CONFLICT(20005, "不能关注自己"),
    // 3xxxx 文章相关
    ARTICLE_NOT_EXIST(30001, "文章不存在"),
    ARTICLE_NOT_BELONG(30002, "尝试修改不属于自己的文章")
    ;
    

    private final int code;
    private final String message;

    ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
