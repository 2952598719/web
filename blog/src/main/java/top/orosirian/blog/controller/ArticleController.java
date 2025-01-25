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
        articleService.addArticle(userUid, articleParam.getTitle(), articleParam.getArticleContent());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "发表文章成功", null);
    }

    @PutMapping("/article/{articleUid}")
    @SaCheckLogin
    public SaResult putArticle(@PathVariable @Valid Long articleUid, @RequestBody @Valid ArticleParam articleParam) {
        articleService.modifyArticle(articleUid, articleParam.getTitle(), articleParam.getArticleContent());
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
    
}
