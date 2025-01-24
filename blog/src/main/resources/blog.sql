
CREATE DATABASE IF NOT EXISTS blog;
USE blog;

-- VARCHAR(255)而不是256，是因为256就需要2个字节存储长度了
-- 需要写成TINYINT UNSIGNED，而不是UNSIGNED TINYINT，或者TINYINY ... UNSIGNED
-- CREATE语句内最后一行不能有逗号，记得去掉
-- 用--作为注释，记得在--后加一个空格，否则会报错
-- 插入单行用VALUES，多行用VALUE

-- ---------------
-- 用户相关
-- ---------------
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user(
    user_uid    BIGINT      NOT NULL COMMENT '用户uid',
    user_name   VARCHAR(20) NOT NULL COMMENT '用户名',              -- 长度最小限制在后端，而不是数据库实现
    pass_word   CHAR(60)    NOT NULL COMMENT '密码的bcrypt加密',    -- 不用VARCHAR是为了向面试官说明VARCHAR和CHAR的不同点，虽然MySQL8貌似两者已经差不多了
    nick_name   VARCHAR(255) NOT NULL COMMENT '昵称',
    avatar_uid  BIGINT      COMMENT '头像uid',
    gender      TINYINT     NOT NULL DEFAULT 0 COMMENT '性别，0为未知，1为男，2为女',
    biography   VARCHAR(255) COMMENT '个人简介',
    birthday    DATE        COMMENT '生日',
    phone_number       VARCHAR(20) COMMENT '手机号',
    email_address       VARCHAR(50) COMMENT '邮箱',
    user_status TINYINT     NOT NULL DEFAULT 0 COMMENT '用户状态，0为正常，1为已注销',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(user_uid),
    UNIQUE INDEX user_name_unique_index(user_name)
) COMMENT '用户表';
INSERT INTO t_user(user_uid, user_name, pass_word, nick_name) 
    VALUES(1871217981827657728, 'orosirian', '$2a$10$JHEBKfQF3NNmMU5LSMtOb.by0Y7AU7kuAU6aA1gxJmpSXMoeMiQdC', "用户");  -- 密码aa111111

DROP TABLE IF EXISTS t_follow;
CREATE TABLE t_follow(
    master_uid    BIGINT NOT NULL COMMENT '被关注者uid',
    fan_uid    BIGINT NOT NULL COMMENT '粉丝uid',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(master_uid, fan_uid),
    INDEX master_uid_index(master_uid), -- 用于统计某人粉丝数
    INDEX fan_uid_index(fan_uid)  -- 用于统计某人关注数
) COMMENT '用户关注表';

-- ---------------
-- 文章相关
-- ---------------
DROP TABLE IF EXISTS t_article;
CREATE TABLE t_article(
    article_uid     BIGINT NOT NULL COMMENT '文章uid',
    user_uid        BIGINT NOT NULL COMMENT '作者uid',
    title           VARCHAR(255) NOT NULL COMMENT '标题',
    article_content LONGTEXT COMMENT '内容',
    article_type    TINYINT NOT NULL DEFAULT 0 COMMENT '状态，1为正常，2为已删除',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(article_uid),
    INDEX user_uid_index(user_uid),
    INDEX article_type_index(article_type)
) COMMENT '文章表';



-- ---------------
-- 其他功能
-- ---------------

DROP TABLE IF EXISTS t_image;
CREATE TABLE t_image(
    image_uid   BIGINT NOT NULL COMMENT '图片uid',
    image_url   VARCHAR(255) NOT NULL COMMENT '图片地址',
    image_hash  VARCHAR(255) NOT NULL COMMENT '图片hash，删除图床的图片时需要提供',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(image_uid)
) COMMENT '图片表';



