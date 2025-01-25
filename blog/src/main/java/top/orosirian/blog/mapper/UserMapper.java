package top.orosirian.blog.mapper;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import top.orosirian.blog.entity.vo.UserBriefVO;
import top.orosirian.blog.entity.vo.UserDetailVO;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author orosirian
 * @since 2024-12-23
 */
@Mapper
public interface UserMapper {

    void insertUser(@Param("userUid")Long userUid, @Param("userName") String userName, @Param("passWord") String passWord, @Param("nickName") String nickName);

    void updatePassWord(@Param("userUid") Long userUid, @Param("passWord") String passWord);

    void deleteUser(@Param("userUid") Long userUid,  @Param("newUserName") String newUserName, @Param("newNickName") String newNickName);

    void updateUserInfo(@Param("userUid") Long userUid, 
                    @Param("nickName") String nickName, @Param("gender") Integer gender,
                    @Param("biography") String biography, @Param("birthday") LocalDate birthday,
                    @Param("phoneNumber") String phoneNumber, @Param("emailAddress") String emailAddress
                    );

    void updateAvatar(@Param("userUid") Long userUid, @Param("avatarUid") Long avatarUid);

    String selectPassWord(@Param("userUid") Long userUid);

    Long selectUserUidFromUserName(@Param("userName") String userName);

    String selectUserNameFromUserUid(@Param("userUid") Long userUid);

    Long selectAvatarUid(@Param("userUid") Long userUid);

    UserBriefVO selectUserBasic(@Param("userUid") Long userUid);

    UserDetailVO selectUserInfo(@Param("userUid") Long userUid); 

    boolean isUserNameExists(@Param("userName") String userName);



}
