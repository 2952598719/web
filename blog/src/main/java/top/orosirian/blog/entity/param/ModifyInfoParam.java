package top.orosirian.blog.entity.param;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyInfoParam {

    @Length(max = 255, message = "昵称最长255位")   // 注意String不要用Min和Max注解，因为那是校验数字的
    private String nickName;

    @Range(min = 0, max = 2, message = "0为保密，1为男，2为女")
    private Integer gender;

    @Length(max = 255, message = "个性签名最长255位")
    private String biography;           // 对英语签名不友好，但目前也不知道有什么解决方法

    private LocalDate birthday;     // 数据库中生日是DATE而不是DATETIME，所以这里也使用LocalDate，不包含时分秒

    @Length(min = 11, max = 11, message = "国内手机号都是11位，国际手机号有机会再拓展")
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "手机号格式")
    private String phoneNumber;

    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "邮箱规范")
    private String emailAddress;
    
}
