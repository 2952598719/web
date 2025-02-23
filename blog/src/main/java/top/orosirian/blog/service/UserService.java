package top.orosirian.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.param.ModifyInfoParam;
import top.orosirian.blog.entity.vo.UserBriefVO;
import top.orosirian.blog.entity.vo.UserDetailVO;
import top.orosirian.blog.mapper.FollowMapper;
import top.orosirian.blog.mapper.ImageMapper;
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

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private FollowMapper followMapper;

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

    public UserBriefVO checkLogin(Long userUid) {
        UserBriefVO userBasicVO = userMapper.selectUserBasic(userUid);
        if(userBasicVO.getAvatarUrl() == null) {
            userBasicVO.setAvatarUrl("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        }
        log.info("获取用户{}基础信息成功", userUid);
        return userBasicVO;
    }

    /**
     * 个人信息
     */

    public UserDetailVO searchUserByUserName(String userName) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        if(userUid == null) throw new BusinessException(ResultCodeEnum.USERNAME_NOT_EXIST, "用户名不存在");

        UserDetailVO userInfoVO = userMapper.selectUserInfo(userUid);
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

    public void modifyAvatar(Long userUid, String avatarUrl, String avatarHash) {
        Long originAvatarUid = userMapper.selectAvatarUid(userUid);
        if(originAvatarUid != null && !originAvatarUid.equals(1885248114797973504L)) {
            imageMapper.deleteImage(originAvatarUid);
        }

        Long avatarUid = snowflake.nextId();
        imageMapper.insertImage(avatarUid, avatarUrl, avatarHash);
        userMapper.updateAvatar(userUid, avatarUid);

        log.info("用户{}头像更新成功", userUid);
    }


    /**
     * 关注取关
     */

     public void follow(String masterUserName, Long fanUid) {
        Long masterUid = userMapper.selectUserUidFromUserName(masterUserName);
        if(masterUid == null) throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        if(masterUid == fanUid) throw new BusinessException(ResultCodeEnum.USER_FOLLOW_CONFLICT);

        followMapper.insertFollow(masterUid, fanUid);
        log.info("{}关注{}成功", fanUid, masterUid);
    }

    public void removeFollow(String masterUserName, Long fanUid) {
        Long masterUid = userMapper.selectUserUidFromUserName(masterUserName);
        if(masterUid == null) throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);

        followMapper.deleteFollow(masterUid, fanUid);
        log.info("{}取关{}成功", fanUid, masterUid);
    }

    public boolean checkFollow(String masterUserName, Long fanUid) {
        Long masterUid = userMapper.selectUserUidFromUserName(masterUserName);
        if(masterUid == null) throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        boolean isFollowed = followMapper.isFollowed(masterUid, fanUid);
        log.info("用户关注情况获取成功");
        return isFollowed;
    }

    public Integer searchMasterNum(String userName) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        Integer masterNum = followMapper.selectMasterNum(userUid);
        log.info("用户关注人数获取成功");
        return masterNum;
    }

    public PageInfo<UserBriefVO> searchMasterList(Integer currentPage, Integer pageSize, String userName) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        PageHelper.startPage(currentPage, pageSize);
        List<UserBriefVO> masterList = followMapper.selectMasterList(userUid);
        PageInfo<UserBriefVO> pageInfo = new PageInfo<>(masterList);
        log.info("获取用户{}的第{}页关注列表成功", userUid, currentPage);
        return pageInfo;
    }

    public Integer searchFanNum(String userName) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        Integer fanNum = followMapper.selectFanNum(userUid);
        log.info("用户粉丝人数获取成功");
        return fanNum;
    }

    public PageInfo<UserBriefVO> searchFanList(Integer currentPage, Integer pageSize, String userName) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        PageHelper.startPage(currentPage, pageSize);
        List<UserBriefVO> fanList = followMapper.selectFanList(userUid);
        PageInfo<UserBriefVO> pageInfo = new PageInfo<>(fanList);
        log.info("获取用户{}的第{}页粉丝列表成功", userUid, currentPage);
        return pageInfo;
    }

    
}
