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
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import jakarta.validation.Valid;
import top.orosirian.blog.entity.param.LoginParam;
import top.orosirian.blog.entity.param.ModifyInfoParam;
import top.orosirian.blog.entity.param.ModifyPassWordParam;
import top.orosirian.blog.entity.param.RegisterParam;
import top.orosirian.blog.entity.vo.UserBriefVO;
import top.orosirian.blog.entity.vo.UserDetailVO;
import top.orosirian.blog.service.UserService;
import top.orosirian.blog.utils.ResultCodeEnum;

@Validated
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册登录相关
     * 登录和登出其实是创建了一个对话，因此用POST，而不用PUT
     */
    @PostMapping("/user/register")
    public SaResult register(@RequestBody @Valid RegisterParam registerParam) {
        userService.register(registerParam.getUserName(), registerParam.getPassWord(), registerParam.getNickName());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "注册成功", null);
    }

    @DeleteMapping("/user/unregister")
    @SaCheckLogin
    public SaResult unregister() {      // 注销，用户名变为随机，昵称变为“已注销用户”
        Long loginId = StpUtil.getLoginIdAsLong();
        StpUtil.logout();
        userService.unregister(loginId);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "注销成功", null);
    }

    @PostMapping("/user/login")
    public SaResult login(@RequestBody @Valid LoginParam loginParam) {
        Long userUid = userService.login(loginParam.getUserName(), loginParam.getPassWord());
        StpUtil.login(userUid);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "登陆成功", tokenInfo);
    }
    
    @PostMapping("/user/logout")
    @SaCheckLogin
    public SaResult logout() {
        userService.logout(StpUtil.getLoginIdAsLong());
        StpUtil.logout();
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "登出成功", null);
    }

    @GetMapping("/user/checkLogin")
    @SaCheckLogin
    public SaResult checkLogin() {
        UserBriefVO info = userService.checkLogin(StpUtil.getLoginIdAsLong());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "处于登陆状态", info);
    }


    /**
     * 个人信息
     */ 

    @GetMapping("/user/info/{userName}")
    public SaResult getUserInfo(@PathVariable String userName) {
        UserDetailVO userInfoVO = userService.searchUserByUserName(userName);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "用户详细信息获取成功", userInfoVO);
    }

    @PutMapping("/user/info")
    @SaCheckLogin
    public SaResult putUserInfo(@RequestBody @Valid ModifyInfoParam modifyInfoParam) {
        userService.modifyUserInfo(StpUtil.getLoginIdAsLong(), modifyInfoParam);    // 参数太多，这个函数就搞一下特殊，不拆
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "信息修改成功", null);
    }

    @PutMapping("/user/info/password")
    @SaCheckLogin
    public SaResult putPassWord(@RequestBody @Valid ModifyPassWordParam modifyPassWordParam) {
        userService.modifyPassWord(StpUtil.getLoginIdAsLong(), modifyPassWordParam.getPassWord());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "密码修改成功", null);
    }

    @PutMapping("/user/info/avatar")
    @SaCheckLogin
    public SaResult putAvatar(@RequestParam String avatarUrl, @RequestParam String avatarHash) {
        userService.modifyAvatar(StpUtil.getLoginIdAsLong(), avatarUrl, avatarHash);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "头像修改成功", null);
    }

    /**
     * 关注取关
     */
    @PostMapping("/user/follow/{masterUserName}")
    @SaCheckLogin
    public SaResult follow(@PathVariable String masterUserName) {
        userService.follow(masterUserName, StpUtil.getLoginIdAsLong());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "关注" + masterUserName + "成功", null);
    }

    @DeleteMapping("/user/follow/{masterUserName}")
    @SaCheckLogin
    public SaResult unfollow(@PathVariable String masterUserName) {
        userService.removeFollow(masterUserName, StpUtil.getLoginIdAsLong());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "取关" + masterUserName + "成功", null);
    }

    @GetMapping("/user/follow/{masterUserName}")
    @SaCheckLogin
    public SaResult checkFollow(@PathVariable String masterUserName) {
        boolean isFollowed = userService.checkFollow(masterUserName, StpUtil.getLoginIdAsLong());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "检查关注成功", isFollowed);
    }

    @GetMapping("/user/info/{userName}/masterNum")
    public SaResult getUserMasterNum(@PathVariable String userName) {
        Integer masterNum = userService.searchMasterNum(userName);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "关注人数获取成功", masterNum);
    }

    @GetMapping("/user/info/{userName}/masterList")
    public SaResult getUserMasterList(@RequestParam Integer currentPage, @RequestParam Integer pageSize, @PathVariable String userName) {
        PageInfo<UserBriefVO> masterList = userService.searchMasterList(currentPage, pageSize, userName);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "关注列表获取成功", masterList);
    }

    @GetMapping("/user/info/{userName}/fanNum")
    public SaResult getUserFanNum(@PathVariable String userName) {
        Integer followerNum = userService.searchFanNum(userName);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "粉丝人数获取成功", followerNum);
    }

    @GetMapping("/user/info/{userName}/fanList")
    public SaResult getUserFanList(@RequestParam Integer currentPage, @RequestParam Integer pageSize, @PathVariable String userName) {
        PageInfo<UserBriefVO> fanList = userService.searchFanList(currentPage, pageSize, userName);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "粉丝列表获取成功", fanList);
    }

}
