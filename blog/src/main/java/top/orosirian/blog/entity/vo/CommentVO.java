package top.orosirian.blog.entity.vo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {

    private String commentUid;

    private String userName;

    private String nickName;
    
    private String avatarUrl;

    private String replyUid;

    private String commentContent;

    private Integer likeNum;

    private Integer dislikeNum;

    private Boolean voteType;

    private LocalDate createTime;
    
}
