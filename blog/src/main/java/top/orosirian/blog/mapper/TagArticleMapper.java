package top.orosirian.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagArticleMapper {

    void insertTagArticle(Long articleUid, String tagName);

    void deleteArticle(Long articleUid);

    void deleteTagArticle(Long articleUid, String tagName);

    String[] selectTagsByArticleUid(Long articleUid);

}
