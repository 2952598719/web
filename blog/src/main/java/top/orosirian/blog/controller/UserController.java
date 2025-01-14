package top.orosirian.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import jakarta.validation.Valid;
import top.orosirian.blog.entity.param.LoginParam;
import top.orosirian.blog.entity.param.RegisterParam;
import top.orosirian.blog.entity.vo.UserLoginInfoVO;
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
        UserLoginInfoVO info = userService.checkLogin(StpUtil.getLoginIdAsLong());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "处于登陆状态", info);
    }


}
