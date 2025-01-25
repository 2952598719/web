package top.orosirian.blog.entity.param;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentParam {

    @NotBlank(message = "评论内容不能为空")
    private String commentContent;
    
}
