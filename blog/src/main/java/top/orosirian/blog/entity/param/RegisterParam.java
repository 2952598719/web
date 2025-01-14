package top.orosirian.blog.entity.param;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterParam {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 8, max = 20, message = "用户名长度必须在8到20")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "用户名只能包含字母和数字")
    private String userName;

    @NotBlank(message = "密码不能为空")
    @Length(min = 8, max = 20, message = "密码长度必须在8到20")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$", message = "密码包含至少1个字母，1个数字")
    private String passWord;
    
    @NotBlank(message = "昵称不能为空")
    @Length(max = 255, message = "昵称长度必须小于255")
    private String nickName;
    
}
