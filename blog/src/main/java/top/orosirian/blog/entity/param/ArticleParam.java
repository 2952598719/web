package top.orosirian.blog.entity.param;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleParam {
    
    @NotBlank(message = "标题不能为空")
    @Length(max = 255, message = "标题长度不能超过255")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String articleContent;

}
