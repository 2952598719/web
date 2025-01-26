package top.orosirian.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import top.orosirian.blog.entity.vo.CollectionVO;

/**
 * <p>
 * 合集表 Mapper 接口
 * </p>
 *
 * @author orosirian
 * @since 2024-06-14
 */
@Mapper
public interface CollectionMapper {

    
    void insertCollection(Long collectionUid, Long userUid, String collectionName);

    void deleteCollection(Long collectionUid);

    void updateCollection(Long collectionUid, String collectionName);

    List<CollectionVO> selectUserCollectionList(Long userUid);

    List<CollectionVO> selectMyCollectionList(Long userUid);

    List<CollectionVO> selectFollowCollectionList(Long userUid);

    CollectionVO selectCollection(Long collectionUid);

    Long[] selectCollectionUidList(Long userUid);

    List<CollectionVO> selectCollectionOfArticleList(Long userUid);

    List<CollectionVO> selectCollectionList(String collectionName);

    boolean isCollectionExists(Long collectionUid);

    boolean isCollectionBelong(Long collectionUid, Long userUid);

}
