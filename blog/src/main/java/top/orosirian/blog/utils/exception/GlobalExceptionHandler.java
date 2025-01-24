package top.orosirian.blog.utils.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.util.SaResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.utils.ResultCodeEnum;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows   // 处理writeValueAsString抛出的异常
    public String formatException(Exception e, String comprehensibleMessage, String exceptionMessage, boolean stackRequired) {
        StringBuilder sb = new StringBuilder();
        sb.append("(异常)\n")
            .append("<类型>").append(e.getClass()).append("\n")
            .append("<说明>").append(comprehensibleMessage).append("\n")
            .append("<详述>").append(exceptionMessage).append("\n");
            // .append("<信息>").append(exceptionMessage != null ? exceptionMessage : e.getMessage()).append("\n");
        if(stackRequired) {
            sb.append("<堆栈>\n");
            for(StackTraceElement ste : e.getStackTrace()) {
                sb.append(objectMapper.writeValueAsString(ste)).append("\n");
            }
        }
        return sb.toString();
    }

    
    /**
     * 异常类型
     */

     // 按理说这里是得返回400, 403之类的错误状态码，但是前端axios死活要在console中打印错误，不知道怎么调整。所以目前先都返回200

    // 400：ResultCodeEnum中写的一些乱七八糟的错误
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public SaResult handleBusinessException(BusinessException e) {
        String comprehensibleMessage = "ResultCodeEnum中一些乱七八糟的错误";
        log.warn(formatException(e, comprehensibleMessage, e.getMessage(), false));
        return SaResult.ok().setCode(e.getCode()).setMsg(e.getMessage());
    }

    //403：没有所请求接口的权限
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NotRoleException.class)
    public SaResult handleNotRoleException(NotRoleException e) {
        String comprehensibleMessage = "权限不足";
        log.warn(formatException(e, comprehensibleMessage, e.getMessage(), false));
        return SaResult.ok().setCode(e.getCode()).setMsg(e.getMessage());
    }

    // 403：登录，权限相关问题
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NotLoginException.class)
    public SaResult handleNotLoginException(NotLoginException e) {
        String comprehensibleMessage = "token失效";
        log.warn(formatException(e, comprehensibleMessage, e.getMessage(), false));
        return SaResult.ok().setCode(e.getCode()).setMsg(e.getMessage());
    }

    // 422：处理参数校验问题，例如密码不符合正则要求
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SaResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String comprehensibleMessage = "参数格式不正确，没有通过校验";
        log.warn(formatException(e, comprehensibleMessage, e.getMessage(), false));
    
        String[] realMessage = e.getLocalizedMessage().split("default message ");
        return SaResult.ok().setCode(ResultCodeEnum.PARAMETER_NOT_VALID.getCode()).setMsg(realMessage[realMessage.length - 1]);
    }

    // 404：路径参数格式不对，MethodArgumentTypeMismatchException
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public SaResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String comprehensibleMessage = "路径参数格式不正确，没有通过校验";
        log.warn(formatException(e, comprehensibleMessage, e.getMessage(), false));
    
        String[] realMessage = e.getLocalizedMessage().split("default message ");
        return SaResult.ok().setCode(ResultCodeEnum.PARAMETER_NOT_VALID.getCode()).setMsg(realMessage[realMessage.length - 1]);
    }


    // 400：兜底
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public SaResult handleException(Exception e) {
        String comprehensibleMessage = "未知错误";
        log.warn(formatException(e, comprehensibleMessage, null, true));
        return SaResult.error().setCode(ResultCodeEnum.UNKNOWN.getCode()).setMsg("服务器内部错误");
    }



    
}
