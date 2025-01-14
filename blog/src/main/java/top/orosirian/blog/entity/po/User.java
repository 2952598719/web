package top.orosirian.blog.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author orosirian
 * @since 2024-12-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户uid
     */
    @TableId("user_uid")
    private Long userUid;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码的bcrypt加密
     */
    private String passWord;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像uid
     */
    private Long avatarUid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
