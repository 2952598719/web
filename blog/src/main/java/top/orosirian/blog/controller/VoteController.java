package top.orosirian.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import top.orosirian.blog.service.VoteService;
import top.orosirian.blog.utils.ResultCodeEnum;

@Validated
@RestController
public class VoteController {
    
    @Autowired
    private VoteService voteService;

    @PostMapping("/like/{targetUid}")
    @SaCheckLogin
    public SaResult like(@PathVariable Long targetUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        voteService.vote(targetUid, userUid, true);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "点赞成功", null);
    }

    @PostMapping("/dislike/{targetUid}")
    @SaCheckLogin
    public SaResult dislike(@PathVariable Long targetUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        voteService.vote(targetUid, userUid, false);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "点踩成功", null);
    }

    @DeleteMapping("/like/{targetUid}")
    @SaCheckLogin
    public SaResult cancelLike(@PathVariable Long targetUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        voteService.disvote(targetUid, userUid, true);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "取消点赞成功", null);
    }

    @DeleteMapping("/dislike/{targetUid}")
    @SaCheckLogin
    public SaResult cancelDislike(@PathVariable Long targetUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        voteService.disvote(targetUid, userUid, false);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "取消点踩成功", null);
    }

}
