package top.orosirian.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.vo.CommentVO;
import top.orosirian.blog.mapper.ArticleMapper;
import top.orosirian.blog.mapper.CommentMapper;
import top.orosirian.blog.mapper.NoticeMapper;
import top.orosirian.blog.utils.RedisKeyConstants;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;

@Service
@Slf4j
public class CommentService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void addComment(Long userUid, Long articleUid, Long replyUid, String commentContent) {
        boolean isArticleExists = articleMapper.isArticleExist(articleUid);
        if(!isArticleExists) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_EXIST);
        }
        if(!replyUid.equals(0L)) {
            boolean isCommentExists = commentMapper.isCommentExist(replyUid);
            if(!isCommentExists) {
                throw new BusinessException(ResultCodeEnum.COMMENT_NOT_EXIST);
            }
            boolean isCommentBelongToArticle = commentMapper.isCommentBelongToArticle(replyUid, articleUid);
            if(!isCommentBelongToArticle) {
                throw new BusinessException(ResultCodeEnum.COMMENT_NOT_BELONG_ARTICLE);
            }
        }
        
        Long commentUid = snowflake.nextId();
        commentMapper.insertComment(commentUid, userUid, articleUid, replyUid, commentContent);
        if(redisTemplate.hasKey(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid))) {
            redisTemplate.opsForHash().increment(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid), RedisKeyConstants.HASH_COMMENT_KEY, 1);
        }

        Long authorUid = (replyUid.equals(0L)) ? articleMapper.selectAuthorUid(articleUid) : commentMapper.selectAuthorUid(replyUid);
        int noticeType = (replyUid.equals(0L)) ? 3 : 4;
        String notificationKey = String.format(RedisKeyConstants.NOTIFICATION_UNREAD_KEY, authorUid);
        if(!authorUid.equals(userUid)) {
            noticeMapper.insertNotice(snowflake.nextId(), authorUid, userUid, commentUid, noticeType, articleUid);
            if(redisTemplate.hasKey(notificationKey)) {
                redisTemplate.opsForValue().increment(notificationKey);
            } else {
                redisTemplate.opsForValue().set(notificationKey, 1);
            }
        }
    }

    public void removeComment(Long userUid, Long commentUid) {
        boolean isCommentBelongToUser = commentMapper.isCommentBelongToUser(commentUid, userUid);
        if(!isCommentBelongToUser) {
            throw new BusinessException(ResultCodeEnum.COMMENT_NOT_BELONG_USER);
        }
        Long articleUid = commentMapper.selectArticleUid(commentUid);
        if(redisTemplate.hasKey(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid))) {
            redisTemplate.opsForHash().increment(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, articleUid), RedisKeyConstants.HASH_COMMENT_KEY, -1);
        }
        commentMapper.deleteComment(commentUid);
    }

    public PageInfo<CommentVO> searchArticleComment(Long articleUid, Long userUid, Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<CommentVO> list = commentMapper.selectArticleCommentList(articleUid, userUid);
        PageInfo<CommentVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public CommentVO searchComment(Long commentUid) {
        CommentVO comment = commentMapper.selectComment(commentUid);
        return comment;
    }
    
}
