package top.orosirian.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.vo.CommentVO;
import top.orosirian.blog.mapper.ArticleMapper;
import top.orosirian.blog.mapper.CommentMapper;
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

    public void addComment(Long userUid, Long articleUid, Long replyUid, String commentContent) {
        boolean isArticleExists = articleMapper.isArticleExist(articleUid);
        if(!isArticleExists) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NOT_EXIST);
        }
        if(replyUid != 0) {
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
        log.info("用户{}发表评论{}成功", userUid, commentUid);
    }

    public void removeComment(Long userUid, Long commentUid) {
        boolean isCommentBelongToUser = commentMapper.isCommentBelongToUser(commentUid, userUid);
        if(!isCommentBelongToUser) {
            throw new BusinessException(ResultCodeEnum.COMMENT_NOT_BELONG_USER);
        }

        commentMapper.deleteComment(commentUid);
        log.info("用户{}已删除评论{}", userUid, commentUid);
    }

    public PageInfo<CommentVO> searchArticleComment(Long articleUid, Long userUid, Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<CommentVO> list = commentMapper.selectArticleCommentList(articleUid, userUid);
        PageInfo<CommentVO> pageInfo = new PageInfo<>(list);
        log.info("获取文章{}评论列表成功", articleUid);
        return pageInfo;
    }

    public CommentVO searchComment(Long commentUid) {
        CommentVO comment = commentMapper.selectComment(commentUid);
        log.info("获取评论{}成功", commentUid);
        return comment;
    }
    
}
