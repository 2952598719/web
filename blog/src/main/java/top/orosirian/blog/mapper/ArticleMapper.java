package top.orosirian.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

import top.orosirian.blog.entity.vo.ArticleDetailVO;

@Mapper
public interface ArticleMapper {

    void insertArticle(Long articleUid, Long userUid, String title,  String articleContent, Integer articleType);

    void updateArticle(Long articleUid, String title,  String articleContent, Integer articleType);

    ArticleDetailVO selectArticle(Long articleUid);

    boolean isArticleExist(Long articleUid);

    
}
