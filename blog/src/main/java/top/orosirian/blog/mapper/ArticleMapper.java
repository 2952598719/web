package top.orosirian.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper {

    void insertArticle(Long articleUid, Long userUid, String title,  String articleContent, Integer articleType);

    
}
