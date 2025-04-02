package top.orosirian.blog.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import top.orosirian.blog.entity.param.ModifyInfoParam;
import top.orosirian.blog.entity.vo.NoticeVO;
import top.orosirian.blog.entity.vo.UserBriefVO;
import top.orosirian.blog.entity.vo.UserDetailVO;
import top.orosirian.blog.mapper.ArticleMapper;
import top.orosirian.blog.mapper.CommentMapper;
import top.orosirian.blog.mapper.FollowMapper;
import top.orosirian.blog.mapper.ImageMapper;
import top.orosirian.blog.mapper.NoticeMapper;
import top.orosirian.blog.mapper.UserMapper;
import top.orosirian.blog.utils.RedisKeyConstants;
import top.orosirian.blog.utils.ResultCodeEnum;
import top.orosirian.blog.utils.exception.BusinessException;
import top.orosirian.blog.utils.mq.EmailTask;

@Service
@Slf4j
public class UserService {

    private final Integer MAX_RETRY_COUNT = 5;
    private static final String TOPIC = "MAIL";

    @Autowired
    private Snowflake snowflake;    // 想使用Snowflake和SaToken要加config文件

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private NoticeMapper noticeMapper;

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
    }

    public void unregister(Long userUid) {
        String newUserName = RandomUtil.randomString(20);
        userMapper.deleteUser(userUid, newUserName, "已注销用户");
    }

    public Long login(String userName, String passWord) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        if(userUid == null) {
            throw new BusinessException(ResultCodeEnum.USERNAME_NOT_EXIST, "用户名或密码错误");
        }
        if(!BCrypt.checkpw(passWord, userMapper.selectPassWord(userUid))) {
            throw new BusinessException(ResultCodeEnum.PASSWORD_WRONG, "用户名或密码错误");
        }
        return userUid;
    }

    public Long emaillogin(String emailAddress, String code) {
        // 根据邮箱查询用户UID
        Long userUid = userMapper.selectUserUidFromEmailAddress(emailAddress);
        if (userUid == null) {
            throw new BusinessException(ResultCodeEnum.EMAIL_NOT_EXIST, "邮箱未注册");
        }

        // 查看输入错误次数是否超限
        String codeKey = String.format(RedisKeyConstants.EMAIL_CODE_KEY, emailAddress);
        String errorKey = String.format(RedisKeyConstants.EMAIL_ERROR_KEY, emailAddress);
        Long errorCount = redisTemplate.opsForValue().increment(errorKey);
        if (errorCount != null && errorCount >= MAX_RETRY_COUNT) {
            redisTemplate.delete(codeKey);
            redisTemplate.delete(errorKey);
            throw new BusinessException(ResultCodeEnum.CODE_RETRY_LIMIT, "错误次数超限");
        }

        // 获取验证码
        String storedCode = String.valueOf(redisTemplate.opsForValue().get(codeKey));
        if (storedCode == null) {
            throw new BusinessException(ResultCodeEnum.CODE_EXPIRED, "验证码已过期");
        }
        if (!storedCode.equals(code)) {
            redisTemplate.opsForValue().increment(errorKey);  // 自动从字符串到数字
            throw new BusinessException(ResultCodeEnum.CODE_WRONG, "验证码错误");
        }
        // 删除已使用的验证码
        redisTemplate.delete(codeKey);
        
        return userUid;
    }

    public void sendcode(String emailAddress) {
        boolean isEmailExists = userMapper.emailExists(emailAddress);
        if(!isEmailExists) {
            log.info("邮箱{}不存在", emailAddress);
            throw new BusinessException(ResultCodeEnum.EMAIL_NOT_EXIST, "邮箱未注册");
        }

        // 1. 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);
        
        // 2.1 初始化重试计数器
        String retryKey = "email:retry:" + emailAddress;
        redisTemplate.opsForValue().set(retryKey, 0, 15, TimeUnit.MINUTES);
        
        // 2.2 保存到Redis（15分钟有效期）
        String codeKey = String.format(RedisKeyConstants.EMAIL_CODE_KEY, emailAddress);
        redisTemplate.opsForValue().set(codeKey, code, 15, TimeUnit.MINUTES);
        // 3. 发送邮件
        rocketMQTemplate.asyncSend(TOPIC, 
                                    new EmailTask(emailAddress, code), 
                                    new SendCallback() {
                                        @Override
                                        public void onSuccess(SendResult sendResult) {
                                            System.out.printf("异步发送成功: %s", sendResult);
                                        }

                                        @Override
                                        public void onException(Throwable throwable) {
                                            System.out.printf("异步发送失败: %s", throwable.getMessage());
                                        }
        });
        
    }

    public void logout(Long userUid) {
        
    }

    public UserBriefVO checkLogin(Long userUid) {
        UserBriefVO userBasicVO = userMapper.selectUserBasic(userUid);
        return userBasicVO;
    }

    /**
     * 个人信息
     */

    public Long searchUserUidByUserName(String userName) {
        Long userUid = userMapper.selectUserUidFromUserName(userName);
        if(userUid == null) throw new BusinessException(ResultCodeEnum.USERNAME_NOT_EXIST, "用户名不存在");
        return userUid;
    }

    public UserDetailVO searchUserByUserUid(Long userUid) {
        if(userUid == null) throw new BusinessException(ResultCodeEnum.USERNAME_NOT_EXIST, "用户名不存在");
        UserDetailVO userInfoVO = userMapper.selectUserInfo(userUid);
        return userInfoVO;
    }

    public void modifyPassWord(Long userUid, String passWord) {
        String hashedPassword = BCrypt.hashpw(passWord);
        userMapper.updatePassWord(userUid, hashedPassword); 
    }

    public void modifyUserInfo(Long userUid, ModifyInfoParam modifyInfoParam) {
        userMapper.updateUserInfo(userUid, 
                                modifyInfoParam.getNickName(), modifyInfoParam.getGender(), 
                                modifyInfoParam.getBiography(), modifyInfoParam.getBirthday(), 
                                modifyInfoParam.getPhoneNumber(), modifyInfoParam.getEmailAddress()
                            );
    }

    public void modifyAvatar(Long userUid, String avatarUrl, String avatarHash) {
        Long originAvatarUid = userMapper.selectAvatarUid(userUid);
        if(originAvatarUid != null && !originAvatarUid.equals(0L)) {
            imageMapper.deleteImage(originAvatarUid);
        }

        Long avatarUid = snowflake.nextId();
        imageMapper.insertImage(avatarUid, avatarUrl, avatarHash);
        userMapper.updateAvatar(userUid, avatarUid);
    }


    /**
     * 关注取关
     */

    public void follow(Long masterUid, Long fanUid) {
        if(masterUid == null) throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        if(masterUid.equals(fanUid)) throw new BusinessException(ResultCodeEnum.USER_FOLLOW_CONFLICT);

        followMapper.insertFollow(masterUid, fanUid);

        Long noticeUid = noticeMapper.selectNoticeUidByFollow(masterUid, fanUid);
        if(noticeUid == null) {
            noticeMapper.insertNoticeFollow(snowflake.nextId(), masterUid, fanUid, 0);
        } else {
            noticeMapper.updateNotice(noticeUid, 0);
        }

        String notificationKey = String.format(RedisKeyConstants.NOTIFICATION_UNREAD_KEY, masterUid);
        if(redisTemplate.hasKey(notificationKey)) {
            redisTemplate.opsForValue().increment(notificationKey);
        } else {
            redisTemplate.opsForValue().set(notificationKey, 1);
        }
    }

    public void removeFollow(Long masterUid, Long fanUid) {
        if(masterUid == null) throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        followMapper.deleteFollow(masterUid, fanUid);
    }

    public boolean checkFollow(Long masterUid, Long fanUid) {
        if(masterUid == null) throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        boolean isFollowed = followMapper.isFollowed(masterUid, fanUid);
        return isFollowed;
    }

    public Integer searchMasterNum(Long userUid) {
        Integer masterNum = followMapper.selectMasterNum(userUid);
        return masterNum;
    }

    public PageInfo<UserBriefVO> searchMasterList(Integer currentPage, Integer pageSize, Long userUid) {
        PageHelper.startPage(currentPage, pageSize);
        List<UserBriefVO> masterList = followMapper.selectMasterList(userUid);
        PageInfo<UserBriefVO> pageInfo = new PageInfo<>(masterList);
        return pageInfo;
    }

    public Integer searchFanNum(Long userUid) {
        Integer fanNum = followMapper.selectFanNum(userUid);
        return fanNum;
    }

    public PageInfo<UserBriefVO> searchFanList(Integer currentPage, Integer pageSize, Long userUid) {
        PageHelper.startPage(currentPage, pageSize);
        List<UserBriefVO> fanList = followMapper.selectFanList(userUid);
        PageInfo<UserBriefVO> pageInfo = new PageInfo<>(fanList);
        return pageInfo;
    }
    
    public PageInfo<NoticeVO> searchNoticeList(Integer currentPage, Integer pageSize, Long userUid) {
        PageHelper.startPage(currentPage, pageSize);
        List<NoticeVO> noticeList = noticeMapper.selectNoticeList(userUid);
        for(NoticeVO noticeVO : noticeList) {
            String content = "";
            if(noticeVO.getNoticeType() == 1 || noticeVO.getNoticeType() == 3) {
                content = articleMapper.selectArticleTitle(Long.valueOf(noticeVO.getArticleUid()));
            } else if(noticeVO.getNoticeType() == 2 || noticeVO.getNoticeType() == 4) {
                content = commentMapper.selectCommentContentPart(Long.valueOf(noticeVO.getSubjectUid()));
            }
            noticeVO.setSubjectContent(content);
        }

        String notificationKey = String.format(RedisKeyConstants.NOTIFICATION_UNREAD_KEY, userUid);
        redisTemplate.opsForValue().set(notificationKey, 0);

        PageInfo<NoticeVO> pageInfo = new PageInfo<>(noticeList);
        return pageInfo;
    }

    public int searchUnreadNum(Long userUid) {
        String notificationKey = String.format(RedisKeyConstants.NOTIFICATION_UNREAD_KEY, userUid);
        if(redisTemplate.hasKey(notificationKey)) {
            int notificationNum = (int) redisTemplate.opsForValue().get(notificationKey);
            return notificationNum;
        } else {
            return 0;
        }
    }

}
