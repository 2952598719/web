package top.orosirian.blog.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.vo.ArticleBriefVO;
import top.orosirian.blog.entity.vo.ArticleDetailVO;
import top.orosirian.blog.mapper.ArticleMapper;
import top.orosirian.blog.mapper.CollectionArticleMapper;
import top.orosirian.blog.mapper.CollectionMapper;
import top.orosirian.blog.mapper.TagArticleMapper;
import top.orosirian.blog.mapper.UserMapper;
import top.orosirian.blog.mapper.VoteMapper;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;

@Service
@Slf4j
public class ArticleService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private TagArticleMapper tagArticleMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionArticleMapper collectionArticleMapper;

    public void addArticle(Long userUid, String title, String articleContent, String tagStr) {
        Long articleUid = snowflake.nextId();
        articleMapper.insertArticle(articleUid, userUid, title, articleContent, 0);
        if (tagStr != null) {
            tagStr = tagStr.replace('，', ',');
            String[] tags = tagStr.split(",");
            for (String tag : tags) { // SQL语句使用的是INSERT IGNORE，当插入重复数据时不会报错
                tag = tag.trim();
                if (tag.equals(""))
                    continue;
                tagArticleMapper.insertTagArticle(articleUid, tag);
            }
        }
        log.info("用户{}发表文章{}成功", userUid, articleUid);
    }

    public void modifyArticle(Long articleUid, Long userUid, String title, String articleContent, String tagStr, String oldTagStr) {
        boolean isArticleExist = articleMapper.isArticleExist(articleUid);
        if (!isArticleExist) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_EXIST);
        }
        boolean isArticleBelong = articleMapper.isArticleBelong(articleUid, userUid);
        if (!isArticleBelong) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_BELONG);
        }

        articleMapper.updateArticle(articleUid, title, articleContent);
        // tagArticleMapper.deleteArticle(articleUid);

        // 将oldTagStr和TagStr基于逗号进行分割，将oldTagStr包含，tagStr没有的标签删除，反之则插入
        oldTagStr = oldTagStr.replace('，', ',');
        tagStr = tagStr.replace('，', ',');
        Set<String> oldTagSet = new HashSet<>(Arrays.asList(oldTagStr.split(",")));
        Set<String> tagSet = new HashSet<>(Arrays.asList(tagStr.split(",")));
        for (String oldTag : oldTagSet) {
            if (oldTag != "" && !tagSet.contains(oldTag)) {
                tagArticleMapper.deleteTagArticle(articleUid, oldTag.trim());
            }
        }
        for (String tag : tagSet) {
            if (tag != "" && !oldTagSet.contains(tag)) {
                tagArticleMapper.insertTagArticle(articleUid, tag.trim());
            }
        }
        log.info("用户{}修改文章{}成功", userUid, articleUid);
    }

    public void removeArticle(Long articleUid, Long userUid) {
        Long authorUid = articleMapper.selectAuthorUid(articleUid);
        if(!authorUid.equals(userUid)) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_BELONG, "文章并非当前用户所属，无法删除");
        }

        articleMapper.deleteArticle(articleUid);
        log.info("删除文章{}成功", articleUid);
    }

    public ArticleDetailVO searchArticle(Long articleUid) {
        boolean isArticleExist = articleMapper.isArticleExist(articleUid);
        if (!isArticleExist) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_EXIST, "文章不存在");
        }

        ArticleDetailVO article = articleMapper.selectArticle(articleUid);
        articleMapper.updateViewCount(articleUid);

        log.info("获取文章成功");
        return article;
    }

    public PageInfo<ArticleBriefVO> searchArticleList(Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectArticleList();
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        log.info("获取第{}页文章成功", currentPage);
        return pageInfo;
    }

    public PageInfo<ArticleBriefVO> searchTitleArticleList(Integer currentPage, Integer pageSize, String title) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectTitleArticleList(title);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        log.info("获取第{}页文章成功", currentPage);
        return pageInfo;
    }

    public PageInfo<ArticleBriefVO> searchUserArticleList(Integer currentPage, Integer pageSize, String userName) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);

        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectUserArticleList(userUid);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        log.info("获取用户{}第{}页文章成功", userUid, currentPage);
        return pageInfo;
    }

    public PageInfo<ArticleBriefVO> searchUserArticleList(Integer currentPage, Integer pageSize, Long userUid) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectUserArticleList(userUid);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        log.info("获取用户{}第{}页文章成功", userUid, currentPage);
        return pageInfo;
    }

    public Integer searchVoteType(Long articleUid, Long userUid) {
        Boolean voteType = voteMapper.checkVote(articleUid, userUid);
        log.info("获取文章{}点赞状态成功", articleUid);
        if(voteType == null) {
            return 0;
        } else if(voteType) {
            return 1;
        } else {
            return 2;
        }
    }
    
    public PageInfo<ArticleBriefVO> searchTagArticleList(Integer currentPage, Integer pageSize, String tagName) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectTagArticleList(tagName);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        log.info("获取Tag {}文章列表成功", tagName);
        return pageInfo;
    }

    public String[] searchArticleTag(Long articleUid) {
        boolean isArticleExist = articleMapper.isArticleExist(articleUid);
        if (!isArticleExist) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_EXIST);
        }

        String[] tags = tagArticleMapper.selectTagsByArticleUid(articleUid);
        log.info("获取文章{}的标签成功", articleUid);
        return tags;
    }

    public void collect(Long userUid, Long collectionUid, Long articleUid) {
        boolean isCollectionExists = collectionMapper.isCollectionExists(collectionUid);
        if (!isCollectionExists) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_EXIST);
        }
        boolean isCollectionBelong = collectionMapper.isCollectionBelong(collectionUid, userUid);
        if (!isCollectionBelong) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_BELONG_USER);
        }
        boolean isCollectionContain = collectionArticleMapper.isCollectionContain(collectionUid, articleUid);
        if (isCollectionContain) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_EXIST_ARTICLE);
        }

        collectionArticleMapper.collect(collectionUid, articleUid);
        log.info("文章{}已收藏至合集{}", articleUid, collectionUid);
    }

    public void uncollect(Long userUid, Long collectionUid, Long articleUid) {
        boolean isCollectionExists = collectionMapper.isCollectionExists(collectionUid);
        if (!isCollectionExists) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_EXIST);
        }
        boolean isCollectionBelong = collectionMapper.isCollectionBelong(collectionUid, userUid);
        if (!isCollectionBelong) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_BELONG_USER);
        }
        boolean isCollectionContain = collectionArticleMapper.isCollectionContain(collectionUid, articleUid);
        if (!isCollectionContain) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_EXIST_ARTICLE);
        }

        collectionArticleMapper.uncollect(collectionUid, articleUid);
        log.info("文章{}已从合集{}取消收藏", articleUid, collectionUid);
    }

    public boolean checkCollect(Long userUid, Long collectionUid, Long articleUid) {
        boolean isCollectionExists = collectionMapper.isCollectionExists(collectionUid);
        if (!isCollectionExists) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_EXIST);
        }
        boolean isCollectionBelong = collectionMapper.isCollectionBelong(collectionUid, userUid);
        if (!isCollectionBelong) {
            throw new BusinessException(ResultCodeEnum.COLLECTION_NOT_BELONG_USER);
        }

        boolean isCollectionContain = collectionArticleMapper.isCollectionContain(collectionUid, articleUid);
        log.info("{}获取{}收藏情况成功", userUid, articleUid);
        return isCollectionContain;
    }

    public PageInfo<ArticleBriefVO> searchCollectionArticleList(Integer currentPage, Integer pageSize, Long collectionUid) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectCollectionArticleList(collectionUid);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        log.info("获取合集文章列表成功");
        return pageInfo;
    }

}
