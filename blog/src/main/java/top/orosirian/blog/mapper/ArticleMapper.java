package top.orosirian.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import top.orosirian.blog.entity.vo.ArticleBriefVO;
import top.orosirian.blog.entity.vo.ArticleDetailVO;

@Mapper
public interface ArticleMapper {

    void insertArticle(Long articleUid, Long userUid, String title,  String articleContent, Integer articleType);

    void updateArticle(Long articleUid, String title,  String articleContent);

    void updateViewCount(Long articleUid, Long increment);

    void deleteArticle(Long articleUid);

    ArticleDetailVO selectArticle(Long articleUid);

    List<ArticleBriefVO> selectArticleList();

    List<ArticleBriefVO> selectTitleArticleList(String title);

    List<ArticleBriefVO> selectUserArticleList(Long userUid);

    List<ArticleBriefVO> selectTagArticleList(String tagName);

    List<ArticleBriefVO> selectCollectionArticleList(Long collectionUid);
    
    List<ArticleBriefVO> selectMasterArticleList(Long userUid);

    String selectArticleTitle(Long articleUid);

    Long selectAuthorUid(Long articleUid);

    boolean isArticleExist(Long articleUid);

    boolean isArticleBelong(Long articleUid, Long userUid);
    
    

    
}
