package top.orosirian.blog.entity.param;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionParam {

    @NotBlank(message = "合集名字不能为空")
    @Length(max = 255, message = "合集名字长度不能超过255")
    private String collectionName;
    
}
