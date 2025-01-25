package top.orosirian.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.vo.ArticleBriefVO;
import top.orosirian.blog.entity.vo.ArticleDetailVO;
import top.orosirian.blog.mapper.ArticleMapper;
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

    public void addArticle(Long userUid, String title, String articleContent) {
        Long articleUid = snowflake.nextId();
        articleMapper.insertArticle(articleUid, userUid, title, articleContent, 0);
        log.info("用户{}发表文章{}成功", userUid, articleUid);
    }

    public void modifyArticle(Long articleUid, String title, String articleContent) {
        articleMapper.updateArticle(articleUid, title, articleContent);
        log.info("修改文章{}成功", articleUid);
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
    
}
