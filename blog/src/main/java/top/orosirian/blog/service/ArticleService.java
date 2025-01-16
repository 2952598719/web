package top.orosirian.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.vo.ArticleDetailVO;
import top.orosirian.blog.mapper.ArticleMapper;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;

@Service
@Slf4j
public class ArticleService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ArticleMapper articleMapper;

    public void addArticle(Long userUid, String title, String articleContent, Integer articleType) {
        Long articleUid = snowflake.nextId();
        articleMapper.insertArticle(articleUid, userUid, title, articleContent, articleType);
        log.info("用户{}发表文章{}成功", userUid, articleUid);
    }

    public void modifyArticle(Long articleUid, String title, String articleContent, Integer articleType) {
        articleMapper.updateArticle(articleUid, title, articleContent, articleType);
        log.info("修改文章{}成功", articleUid);
    }

    public ArticleDetailVO searchArticle(Long articleUid) {
        boolean isArticleExist = articleMapper.isArticleExist(articleUid);
        if (!isArticleExist) {
            throw new BusinessException(ResultCodeEnum.ARTICLE_NE);
        }

        ArticleDetailVO article = articleMapper.selectArticle(articleUid);

        log.info("获取文章成功");
        return article;
    }
    
}
