package top.orosirian.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 合集文章表 Mapper 接口
 * </p>
 *
 * @author orosirian
 * @since 2024-06-14
 */
@Mapper
public interface CollectionArticleMapper {

    void collect(Long collectionUid, Long articleUid);

    void uncollect(Long collectionUid, Long articleUid);

    void deleteCollection(Long collectionUid);

    void deleteArticle(Long articleUid);

    Integer selectArticleNum(Long collectionUid);   // 获取合集中正常文章数量

    boolean isCollectionContain(Long collectionUid, Long articleUid);

}
