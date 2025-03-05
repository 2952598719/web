package top.orosirian.blog.entity.vo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type") // 自定义类型属性名
@JsonSubTypes({@Type(value = ArticleDetailVO.class, name = "article")})
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
