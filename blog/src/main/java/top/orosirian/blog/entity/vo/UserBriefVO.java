package top.orosirian.blog.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBriefVO {

    private String userName;

    private String nickName;

    private String avatarUrl;
    
}
