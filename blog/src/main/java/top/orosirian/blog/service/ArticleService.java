package top.orosirian.blog.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import top.orosirian.blog.mapper.VoteMapper;
import top.orosirian.blog.utils.RedisKeyConstants;
import top.orosirian.blog.utils.RedisRetryUtil;
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
    private VoteMapper voteMapper;

    @Autowired
    private TagArticleMapper tagArticleMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionArticleMapper collectionArticleMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    ObjectMapper objectMapper;

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
        redisTemplate.delete(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid));

        // 将oldTagStr和TagStr基于逗号进行分割，将oldTagStr包含，tagStr没有的标签删除，反之则插入
        oldTagStr = oldTagStr.replace('，', ',');
        tagStr = tagStr.replace('，', ',');
        Set<String> oldTagSet = new HashSet<>(Arrays.asList(oldTagStr.split(",")));
        Set<String> tagSet = new HashSet<>(Arrays.asList(tagStr.split(",")));
        for (String oldTag : oldTagSet) {
            if (oldTag.equals("") && !tagSet.contains(oldTag)) {
                tagArticleMapper.deleteTagArticle(articleUid, oldTag.trim());
            }
        }
        for (String tag : tagSet) {
            if (tag.equals("") && !oldTagSet.contains(tag)) {
                tagArticleMapper.insertTagArticle(articleUid, tag.trim());
            }
        }
    }

    public void removeArticle(Long articleUid, Long userUid) {
        Long authorUid = articleMapper.selectAuthorUid(articleUid);
        if(!authorUid.equals(userUid)) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_BELONG, "文章并非当前用户所属，无法删除");
        }

        articleMapper.deleteArticle(articleUid);
        redisTemplate.delete(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid));
        for (String tag : tagArticleMapper.selectTagsByArticleUid(articleUid)) {
            tagArticleMapper.deleteTagArticle(articleUid, tag);
        }
        collectionArticleMapper.deleteArticle(articleUid);
    }

    @SuppressWarnings("unchecked")
    public ArticleDetailVO searchArticle(Long articleUid) {
        String articleHashKey = String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid);

        // 1. 优先检查缓存
        if(redisTemplate.hasKey(articleHashKey)) {
            Map<Object, Object> redisData = redisTemplate.opsForHash().entries(articleHashKey);
            redisTemplate.opsForHash().increment(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid), RedisKeyConstants.HASH_VIEW_KEY, 1);
            articleMapper.updateViewCount(articleUid, 1L);
            ArticleDetailVO article = objectMapper.convertValue(redisData, ArticleDetailVO.class);
            log.info("缓存中获取{}", articleUid);
            return article;
        }

        // 2. 使用带超时的锁（避免永久阻塞）
        RLock lock = redissonClient.getLock(String.format(RedisKeyConstants.ARTICLE_LOCK_KEY, articleUid));
        try {
            // 设置锁超时时间为3秒，等待锁时间为0（立即失败）
            boolean locked = lock.tryLock(0, 3, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException(ResultCodeEnum.SERVER_BUSY);
            }

            // 3. 二次检查缓存（可能在等待锁期间已有其他线程加载）
            String cacheKey = String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid);
            if(redisTemplate.hasKey(cacheKey)) {
                Map<Object, Object> redisData = redisTemplate.opsForHash().entries(cacheKey);
                redisTemplate.opsForHash().increment(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid), RedisKeyConstants.HASH_VIEW_KEY, 1);
                articleMapper.updateViewCount(articleUid, 1L); 
                ArticleDetailVO article = objectMapper.convertValue(redisData, ArticleDetailVO.class);
                log.info("缓存中获取{}", articleUid);
                return article;
            }

            // 4. 数据库查询
            ArticleDetailVO article = articleMapper.selectArticle(articleUid);
            log.info("数据库中获取{}", articleUid);
            // if (article == null) {
            //     redisTemplate.opsForValue().set(cacheKey, "", 5, TimeUnit.MINUTES);
            //     throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_EXIST);
            // }

            // 5. 异步回填缓存（避免锁内执行Redis操作）
            CompletableFuture.runAsync(() -> {
                try {
                    RedisRetryUtil.retryWithBackoff(() -> {
                        int expireTime = 1 + ThreadLocalRandom.current().nextInt(5);
                        // 将article对象放入hash中，字段名为article的每个属性，值为其对应值
                        Map<String, Object> articleMap = objectMapper.convertValue(article, Map.class);
                        redisTemplate.opsForHash().putAll(articleHashKey, articleMap);
                        redisTemplate.opsForHash().increment(articleHashKey, RedisKeyConstants.HASH_VIEW_KEY, 1);
                        redisTemplate.expire(articleHashKey, expireTime, TimeUnit.HOURS);
                        log.info("缓存文章{}成功", articleUid);
                        return null;
                    }, 3, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                } // 最大重试3次，初始间隔2秒
            }).exceptionally(e -> {
                log.error("缓存回填最终失败，articleUid: {}", articleUid, e);
                return null;
            });
            return article;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ResultCodeEnum.SERVER_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public PageInfo<ArticleBriefVO> searchArticleList(Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectArticleList();
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public PageInfo<ArticleBriefVO> searchTitleArticleList(Integer currentPage, Integer pageSize, String title) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectTitleArticleList(title);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public PageInfo<ArticleBriefVO> searchUserArticleList(Integer currentPage, Integer pageSize, Long userUid) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectUserArticleList(userUid);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public PageInfo<ArticleBriefVO> searchMasterArticleList(Integer currentPage, Integer pageSize, Long userUid) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectMasterArticleList(userUid);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public Integer searchVoteType(Long articleUid, Long userUid) {
        Boolean voteType = voteMapper.checkVote(articleUid, userUid);
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
        return pageInfo;
    }

    public String[] searchArticleTag(Long articleUid) {
        boolean isArticleExist = articleMapper.isArticleExist(articleUid);
        if (!isArticleExist) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_EXIST);
        }
        String[] tags = tagArticleMapper.selectTagsByArticleUid(articleUid);
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
        
        return isCollectionContain;
    }

    public PageInfo<ArticleBriefVO> searchCollectionArticleList(Integer currentPage, Integer pageSize, Long collectionUid) {
        PageHelper.startPage(currentPage, pageSize);
        List<ArticleBriefVO> list = articleMapper.selectCollectionArticleList(collectionUid);
        PageInfo<ArticleBriefVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

}
