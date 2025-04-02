package top.orosirian.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.mapper.ArticleMapper;
import top.orosirian.blog.mapper.CommentMapper;
import top.orosirian.blog.mapper.NoticeMapper;
import top.orosirian.blog.mapper.VoteMapper;
import top.orosirian.blog.utils.RedisKeyConstants;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;

@Service
@Slf4j
public class VoteService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired 
    private CommentMapper commentMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public void vote(Long targetUid, Long userUid, boolean voteType) {
        boolean isTargetExist = articleMapper.isArticleExist(targetUid) || commentMapper.isCommentExist(targetUid);
        if(!isTargetExist) {
            throw new BusinessException(ResultCodeEnum.VOTE_TARGET_NOT_EXIST);
        }

        voteMapper.vote(targetUid, userUid, voteType);

        Long articleAuthorUid = articleMapper.selectAuthorUid(targetUid);
        Long commentAuthorUid = commentMapper.selectAuthorUid(targetUid);
        if(articleAuthorUid != null) {
            if(redisTemplate.hasKey(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, targetUid))) {
                if(voteType) {
                    redisTemplate.opsForHash().increment(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, targetUid), RedisKeyConstants.HASH_LIKE_KEY, 1);
                } else {
                    redisTemplate.opsForHash().increment(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, targetUid), RedisKeyConstants.HASH_DISLIKE_KEY, 1);
                }
            }
        }

        if(voteType == true && !userUid.equals(articleAuthorUid) && !userUid.equals(commentAuthorUid)) {
            Long noticeUid = noticeMapper.selectNoticeUid(userUid, targetUid);
            String notificationKey = "";
            if(articleAuthorUid != null) {
                if(noticeUid != null) {     // 如果告示已存在，就更新它的时间
                    noticeMapper.updateNotice(noticeUid, 1);
                } else {
                    noticeMapper.insertNotice(snowflake.nextId(), articleAuthorUid, userUid, targetUid, 1, targetUid);
                }
                notificationKey = String.format(RedisKeyConstants.NOTIFICATION_UNREAD_KEY, articleAuthorUid);
            } else if(commentAuthorUid != null) {
                Long articleUid = commentMapper.selectArticleUid(targetUid);
                if(noticeUid != null) {
                    noticeMapper.updateNotice(noticeUid, 2);
                } else {
                    noticeMapper.insertNotice(snowflake.nextId(), commentAuthorUid, userUid, targetUid, 2, articleUid);
                }
                notificationKey = String.format(RedisKeyConstants.NOTIFICATION_UNREAD_KEY, commentAuthorUid);
            }

            
            if(redisTemplate.hasKey(notificationKey)) {
                redisTemplate.opsForValue().increment(notificationKey);
            } else {
                redisTemplate.opsForValue().set(notificationKey, 1);
            }
        }
    }

    public void disvote(Long targetUid, Long userUid, boolean voteType) {
        boolean isTargetExist = articleMapper.isArticleExist(targetUid) || commentMapper.isCommentExist(targetUid);
        if(!isTargetExist) {
            throw new BusinessException(ResultCodeEnum.VOTE_TARGET_NOT_EXIST);
        }
        
        Long articleAuthorUid = articleMapper.selectAuthorUid(targetUid);
        if(articleAuthorUid != null) {
            if(redisTemplate.hasKey(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, targetUid))) {
                if(voteType) {
                    redisTemplate.opsForHash().increment(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, targetUid), RedisKeyConstants.HASH_LIKE_KEY, -1);
                } else {
                    redisTemplate.opsForHash().increment(String.format(RedisKeyConstants.ARTICLE_HASH_KEY, targetUid), RedisKeyConstants.HASH_DISLIKE_KEY, -1);
                }
            }
        }

        voteMapper.disvote(targetUid, userUid);
        
    }

    public boolean checkVote(Long targetUid, Long userUid) {
        Boolean isVoted = voteMapper.checkVote(targetUid, userUid);
        return isVoted;
    }

}