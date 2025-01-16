package top.orosirian.blog.utils;

public enum ResultCodeEnum {

    SUCCESS(99999, "成功"),
    USER_USERNAME_AE(10001, "用户名已存在"),  // 注册
    USER_USERNAME_NE(10002, "用户名不存在"),  // 登录
    USER_PASSWORD_WRONG(10003, "密码和用户名不对应"),
    ARTICLE_NE(10004, "文章不存在"),
    PARAMETER_NOT_VALID(10100, "参数无效"),
    UNKNOWN(11111, "未知错误"),
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
