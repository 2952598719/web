package top.orosirian.blog.entity.vo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO {

    private String userName;

    private String nickName;

    private Integer gender;

    private String biography;

    private LocalDate birthday;

    private String phoneNumber;

    private String emailAddress;

    private String avatarUrl;

    private String avatarHash;
    
}
