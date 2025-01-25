package top.orosirian.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import top.orosirian.blog.entity.vo.ArticleBriefVO;
import top.orosirian.blog.entity.vo.ArticleDetailVO;

@Mapper
public interface ArticleMapper {

    void insertArticle(Long articleUid, Long userUid, String title,  String articleContent, Integer articleType);

    void updateArticle(Long articleUid, String title,  String articleContent);

    void deleteArticle(Long articleUid);

    ArticleDetailVO selectArticle(Long articleUid);

    List<ArticleBriefVO> selectArticleList();

    List<ArticleBriefVO> selectUserArticleList(Long userUid);

    Long selectAuthorUid(Long articleUid);

    boolean isArticleExist(Long articleUid);
    

    
}
