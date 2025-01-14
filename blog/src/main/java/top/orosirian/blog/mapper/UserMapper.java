package top.orosirian.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    String selectPassWord(@Param("userUid") Long userUid);

    Long selectUserUidFromUserName(@Param("userName") String userName);

    String selectUserNameFromUserUid(@Param("userUid") Long userUid);

    boolean isUserNameExists(@Param("userName") String userName);



}
