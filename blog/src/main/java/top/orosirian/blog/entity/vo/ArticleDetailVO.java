package top.orosirian.blog.entity.vo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetailVO {

    private String articleUid;

    private String userName;

    private String nickName;

    private String avatarUrl;

    private String title;
    
    private Integer likeNum;

    private Integer dislikeNum;

    private Long viewCount;

    private Integer commentCount;

    private String articleContent;

    private LocalDate createTime;

    private LocalDate updateTime;
    
}
