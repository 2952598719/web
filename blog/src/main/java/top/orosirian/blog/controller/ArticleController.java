package top.orosirian.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import jakarta.validation.Valid;
import top.orosirian.blog.entity.param.ArticleParam;
import top.orosirian.blog.entity.vo.ArticleBriefVO;
import top.orosirian.blog.entity.vo.ArticleDetailVO;
import top.orosirian.blog.service.ArticleService;
import top.orosirian.blog.utils.ResultCodeEnum;

@Validated
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/article")
    @SaCheckLogin
    public SaResult postArticle(@RequestBody @Valid ArticleParam articleParam) {
        Long userUid = StpUtil.getLoginIdAsLong();
        articleService.addArticle(userUid, articleParam.getTitle(), articleParam.getArticleContent(), articleParam.getTagStr());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "发表文章成功", null);
    }

    @PutMapping("/article/{articleUid}")
    @SaCheckLogin
    public SaResult putArticle(@PathVariable @Valid Long articleUid, @RequestBody @Valid ArticleParam articleParam) {
        Long userUid = StpUtil.getLoginIdAsLong();
        articleService.modifyArticle(articleUid, userUid, articleParam.getTitle(), articleParam.getArticleContent(), articleParam.getTagStr(), articleParam.getOldTagStr());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "修改文章成功", null);
    }

    @DeleteMapping("/article/{articleUid}")
    @SaCheckLogin
    public SaResult deleteArticle(@PathVariable @Valid Long articleUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        articleService.removeArticle(articleUid, userUid);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "删除文章成功", null);
    }

    @GetMapping("/article/{articleUid}")
    public SaResult getArticle(@PathVariable @Valid Long articleUid) {
        ArticleDetailVO article = articleService.searchArticle(articleUid);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "获取文章成功", article);  
    }

    @GetMapping("/articles/home")
    public SaResult getHomeArticleList(@RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        PageInfo<ArticleBriefVO> result = articleService.searchArticleList(currentPage, pageSize);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "搜索文章列表成功", result);
    }

    @GetMapping("/articles/master")
    @SaCheckLogin
    public SaResult getMasterArticleList(@RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        Long userUid = StpUtil.getLoginIdAsLong();
        PageInfo<ArticleBriefVO> result = articleService.searchMasterArticleList(currentPage, pageSize, userUid);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "搜索关注者文章列表成功", result);
    }

    @GetMapping("/articles/userPage")
    public SaResult getUserArticleList(@RequestParam Integer currentPage, @RequestParam Integer pageSize, @RequestParam String userName) {
        PageInfo<ArticleBriefVO> result = articleService.searchUserArticleList(currentPage, pageSize, userName);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "搜索文章列表成功", result);
    }

    @GetMapping("/articles/manage")
    @SaCheckLogin
    public SaResult getManageArticleList(@RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        Long userUid = StpUtil.getLoginIdAsLong();
        PageInfo<ArticleBriefVO> result = articleService.searchUserArticleList(currentPage, pageSize, userUid);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "搜索文章列表成功", result);
    }

    @GetMapping("/articles/search/{title}")
    public SaResult getSearchArticleList(@RequestParam Integer currentPage, @RequestParam Integer pageSize, @PathVariable @Valid String title) {
        PageInfo<ArticleBriefVO> result = articleService.searchTitleArticleList(currentPage, pageSize, title);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "搜索文章列表成功", result);
    }

    @GetMapping("/articles/tag/{tagName}")
    public SaResult getTagArticleList(@RequestParam Integer currentPage, @RequestParam Integer pageSize, @PathVariable @Valid String tagName) {
        PageInfo<ArticleBriefVO> result = articleService.searchTagArticleList(currentPage, pageSize, tagName);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "按标签搜索文章列表成功", result);
    }

    @GetMapping("/article/vote/{articleUid}")
    @SaCheckLogin
    public SaResult getVoteType(@PathVariable @Valid Long articleUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        Integer voteType = articleService.searchVoteType(articleUid, userUid);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "获取点赞状态成功", voteType);
    }

    @GetMapping("/article/tag/{articleUid}")
    public SaResult getArticleTags(@PathVariable String articleUid) {
        String tags = String.join(", ", articleService.searchArticleTag(Long.parseLong(articleUid)));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "获取文章标签列表成功", tags);  
    }

    /**
     * 合集：收藏，取消收藏，是否收藏，获取收藏夹内文章
     */ 
    @PostMapping("/article/collection/{collectionUid}/{articleUid}")
    @SaCheckLogin
    public SaResult collect(@PathVariable String collectionUid, @PathVariable String articleUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        articleService.collect(userUid, Long.parseLong(collectionUid), Long.parseLong(articleUid));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "文章收藏成功", null);
    }

    @DeleteMapping("/article/collection/{collectionUid}/{articleUid}")
    @SaCheckLogin
    public SaResult uncollect(@PathVariable String collectionUid, @PathVariable String articleUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        articleService.uncollect(userUid, Long.parseLong(collectionUid), Long.parseLong(articleUid));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "文章取消收藏成功", null);
    }

    @GetMapping("/article/collection/{collectionUid}/{articleUid}")
    @SaCheckLogin
    public SaResult checkCollect(@PathVariable String collectionUid, @PathVariable String articleUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        boolean isCollect = articleService.checkCollect(userUid, Long.parseLong(collectionUid), Long.parseLong(articleUid));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "文章收藏成功", isCollect);
    }

    @GetMapping("/articles/collection/{collectionUid}")
    public SaResult getCollectionArticleList(@PathVariable String collectionUid, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        PageInfo<ArticleBriefVO> result = articleService.searchCollectionArticleList(currentPage, pageSize, Long.parseLong(collectionUid));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "搜索收藏夹文章列表成功", result);
    }
    
}
