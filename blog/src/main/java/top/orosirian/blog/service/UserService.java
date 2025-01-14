package top.orosirian.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.vo.UserLoginInfoVO;
import top.orosirian.blog.mapper.UserMapper;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;

@Service
@Slf4j
public class UserService {

    @Autowired
    private Snowflake snowflake;    // 想使用Snowflake和SaToken要加config文件

    @Autowired
    private UserMapper userMapper;

    /**
     * 注册登录
     */
    public void register(String userName, String passWord, String nickName) {
        boolean isUserNameExists = userMapper.isUserNameExists(userName);
        if(isUserNameExists) {
            throw new BusinessException(ResultCodeEnum.USER_USERNAME_AE);
        }

        Long snowUid = snowflake.nextId();
        String hashedPassword = BCrypt.hashpw(passWord);
        userMapper.insertUser(snowUid, userName, hashedPassword, nickName);
        log.info("用户{}注册成功，用户名'{}'，昵称'{}'", snowUid, userName, nickName);
    }

    public Long login(String userName, String passWord) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        if(userUid == null) {
            throw new BusinessException(ResultCodeEnum.USER_USERNAME_NE, "用户名或密码错误");
        }
        if(!BCrypt.checkpw(passWord, userMapper.selectPassWord(userUid))) {
            throw new BusinessException(ResultCodeEnum.USER_PASSWORD_WRONG, "用户名或密码错误");
        }
        log.info("用户{}登陆成功", userUid);
        return userUid;
    }

    public void logout(Long userUid) {
        log.info("用户{}登出成功", userUid);
    }

    public UserLoginInfoVO checkLogin(Long userUid) {
        String userName = userMapper.selectUserNameFromUserUid(userUid);
        String avatarUrl = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";
        log.info("获取用户{}基础信息成功", userUid);
        return new UserLoginInfoVO(userName, avatarUrl);
    }



    
}
