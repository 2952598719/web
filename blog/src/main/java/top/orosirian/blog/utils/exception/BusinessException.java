package top.orosirian.blog.utils.exception;

import top.orosirian.blog.utils.ResultCodeEnum;

public class BusinessException extends RuntimeException {
    
    private final int code;

    public BusinessException(ResultCodeEnum resultCodeEnum) {   // ResultCodeEnum在同一个包内，不需要import
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    public BusinessException(ResultCodeEnum resultCodeEnum, String message) {   // 自定义信息
        super(message);
        this.code = resultCodeEnum.getCode();
    }

    public int getCode() {
        return code;
    }

    
}
