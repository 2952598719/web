package top.orosirian.blog.entity.param;

import org.hibernate.validator.constraints.Length;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPassWordParam {

    @Length(min = 8, max = 20, message = "密码长度必须在8到20")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$", message = "密码包含至少1个字母，1个数字")
    @Nullable   // 存疑
    private String passWord;
    
}
