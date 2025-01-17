package top.orosirian.blog.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.param.ModifyInfoParam;
import top.orosirian.blog.entity.vo.UserInfoVO;
import top.orosirian.blog.entity.vo.UserLoginInfoVO;
import top.orosirian.blog.mapper.UserMapper;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;

import top.orosirian.blog.utils.RandomGenerator;

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
            throw new BusinessException(ResultCodeEnum.USERNAME_EXIST, "用户名已存在");
        }

        Long snowUid = snowflake.nextId();
        String hashedPassword = BCrypt.hashpw(passWord);
        userMapper.insertUser(snowUid, userName, hashedPassword, nickName);
        log.info("用户{}注册成功，用户名'{}'，昵称'{}'", snowUid, userName, nickName);
    }

    public void unregister(Long userUid) {
        String newUserName = RandomGenerator.generateSimpleRandomString(20);
        userMapper.deleteUser(userUid, newUserName, "已注销用户");
        log.info("用户{}注销成功", userUid);
    }

    public Long login(String userName, String passWord) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        if(userUid == null) {
            throw new BusinessException(ResultCodeEnum.USERNAME_NOT_EXIST, "用户名或密码错误");
        }
        if(!BCrypt.checkpw(passWord, userMapper.selectPassWord(userUid))) {
            throw new BusinessException(ResultCodeEnum.PASSWORD_WRONG, "用户名或密码错误");
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

    /**
     * 个人信息
     */

    public UserInfoVO searchUserByUserName(String userName) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        if(userUid == null) throw new BusinessException(ResultCodeEnum.USERNAME_NOT_EXIST, "用户名不存在");

        UserInfoVO userInfoVO = userMapper.selectUserInfo(userUid);
        log.info("获取用户{}详细信息成功", userUid);
        return userInfoVO;
    }

    public void modifyPassWord(Long userUid, String passWord) {
        String hashedPassword = BCrypt.hashpw(passWord);
        userMapper.updatePassWord(userUid, hashedPassword);
        log.info("用户{}密码更新成功", userUid);
    }

    public void modifyUserInfo(Long userUid, ModifyInfoParam modifyInfoParam) {
        userMapper.updateUserInfo(userUid, 
                                modifyInfoParam.getNickName(), modifyInfoParam.getGender(), 
                                modifyInfoParam.getBiography(), modifyInfoParam.getBirthday(), 
                                modifyInfoParam.getPhoneNumber(), modifyInfoParam.getEmailAddress()
                            );
        log.info("用户{}个人信息更新成功", userUid);
    }



    
}
