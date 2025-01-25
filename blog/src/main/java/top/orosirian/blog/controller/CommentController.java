package top.orosirian.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import jakarta.validation.Valid;
import top.orosirian.blog.entity.param.CommentParam;
import top.orosirian.blog.entity.vo.CommentVO;
import top.orosirian.blog.service.CommentService;
import top.orosirian.blog.utils.ResultCodeEnum;


@Validated
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/comment/{articleUid}")
    @SaCheckLogin
    public SaResult postArticleComment(@PathVariable String articleUid, @RequestBody @Valid CommentParam commentParam) {
        Long userUid = StpUtil.getLoginIdAsLong();
        commentService.addComment(userUid, Long.parseLong(articleUid), Long.parseLong("0"), commentParam.getCommentContent());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "发表评论成功", null);
    }

    @PostMapping("/comment/{articleUid}/{commentUid}")
    @SaCheckLogin
    public SaResult postCommentComment(@PathVariable String articleUid, @PathVariable String commentUid, @RequestBody @Valid CommentParam commentParam) {
        Long userUid = StpUtil.getLoginIdAsLong();
        commentService.addComment(userUid, Long.parseLong(articleUid), Long.parseLong(commentUid), commentParam.getCommentContent());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "发表评论成功", null);
    }

    @DeleteMapping("/comment/{commentUid}")
    @SaCheckLogin
    public SaResult deleteComment(@PathVariable String commentUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        commentService.removeComment(userUid, Long.parseLong(commentUid));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "删除评论成功", null);
    }

    @GetMapping("/comment/article/{articleUid}")
    public SaResult getArticleCommentList(@PathVariable String articleUid, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        Long userUid = null;
        if(StpUtil.isLogin()) {
            userUid = StpUtil.getLoginIdAsLong();
        }
        PageInfo<CommentVO> result = commentService.searchArticleComment(Long.parseLong(articleUid), userUid, currentPage, pageSize);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "获取评论列表成功", result);
    }

    @GetMapping("/comment/{commentUid}")
    public SaResult getComment(@PathVariable String commentUid) {
        CommentVO result = commentService.searchComment(Long.parseLong(commentUid));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "获取评论成功", result);
    }
    
}
