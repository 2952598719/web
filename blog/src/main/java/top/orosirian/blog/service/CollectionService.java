package top.orosirian.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.vo.CollectionVO;
import top.orosirian.blog.mapper.CollectionArticleMapper;
import top.orosirian.blog.mapper.CollectionMapper;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;

@Service
@Slf4j
public class CollectionService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionArticleMapper collectionArticleMapper;

    public void addCollection(Long userUid, String collectionName) {
        // userUid通过StpUtil获取，没有不存在的风险
        // collectionName可以重复，也无需验证
        Long collectionUid = snowflake.nextId();
        collectionMapper.insertCollection(collectionUid, userUid, collectionName);
    }

    public void removeCollection(Long userUid, Long collectionUid) {
        boolean isCollectionExists = collectionMapper.isCollectionExists(collectionUid);
        if (!isCollectionExists) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_EXIST);
        }
        boolean isCollectionBelong = collectionMapper.isCollectionBelong(collectionUid, userUid);
        if (!isCollectionBelong) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_BELONG_USER);
        }

        collectionMapper.deleteCollection(collectionUid);
        collectionArticleMapper.deleteCollection(collectionUid);
        
    }

    public void modifyCollection(Long userUid, Long collectionUid, String collectionName) {
        boolean isCollectionExists = collectionMapper.isCollectionExists(collectionUid);
        if (!isCollectionExists) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_EXIST);
        }
        boolean isCollectionBelong = collectionMapper.isCollectionBelong(collectionUid, userUid);
        if (!isCollectionBelong) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_BELONG_USER);
        }
        collectionMapper.updateCollection(collectionUid, collectionName);
    }

    public List<CollectionVO> searchMyCollectionList(Long userUid) {
        List<CollectionVO> list = collectionMapper.selectMyCollectionList(userUid);
        return list;
    }

    public List<CollectionVO> searchCollectionOfArticleList(Long userUid, Long articleUid) {
        List<CollectionVO> list = collectionMapper.selectMyCollectionList(userUid);
        for(CollectionVO collectionOfArticleVO: list) {
            Long collectionUid = Long.parseLong(collectionOfArticleVO.getCollectionUid());
            boolean selected = collectionArticleMapper.isCollectionContain(collectionUid, articleUid);
            collectionOfArticleVO.setSelected(selected);
        }
        return list;
    }

    public Long[] searchCollectionUidList(Long userUid) {
        Long[] collectionUidList = collectionMapper.selectCollectionUidList(userUid);
        return collectionUidList;
    }

}
