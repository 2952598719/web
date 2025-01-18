package top.orosirian.blog.entity.vo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicVO {

    private String userName;

    private String avatarUrl;
    
}
