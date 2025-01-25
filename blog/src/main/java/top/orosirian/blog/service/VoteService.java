package top.orosirian.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.mapper.ArticleMapper;
import top.orosirian.blog.mapper.CommentMapper;
import top.orosirian.blog.mapper.VoteMapper;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;

@Service
@Slf4j
public class VoteService {

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired 
    private CommentMapper commentMapper;


    public void vote(Long targetUid, Long userUid, boolean voteType) {
        boolean isTargetExist = articleMapper.isArticleExist(targetUid) || commentMapper.isCommentExist(targetUid);
        if(!isTargetExist) {
            throw new BusinessException(ResultCodeEnum.VOTE_TARGET_NOT_EXIST);
        }
        // boolean isVoted = voteMapper.checkVote(targetUid, userUid, voteType);
        // if(isVoted) {
        //     throw new BusinessException(ResultCodeEnum.VOTE_ALREADY_VOTED);
        // }

        voteMapper.vote(targetUid, userUid, voteType);
        log.info("用户{}赞踩{}成功", userUid, targetUid);
    }

    public void disvote(Long targetUid, Long userUid, boolean voteType) {
        boolean isTargetExist = articleMapper.isArticleExist(targetUid) || commentMapper.isCommentExist(targetUid);
        if(!isTargetExist) {
            throw new BusinessException(ResultCodeEnum.VOTE_TARGET_NOT_EXIST);
        }
        // boolean isVoted = voteMapper.checkVote(targetUid, userUid, voteType);
        // if(!isVoted) {
        //     throw new BusinessException(ResultCodeEnum.VOTE_NOT_VOTE);
        // }

        voteMapper.disvote(targetUid, userUid);
        log.info("用户{}取消赞踩{}成功", userUid, targetUid);
    }

    public boolean checkVote(Long targetUid, Long userUid) {
        Boolean isVoted = voteMapper.checkVote(targetUid, userUid);
        log.info("获取用户{}赞踩{}情况成功", userUid, targetUid);
        return isVoted;
    }

}