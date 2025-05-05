
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
    avatar_uid  BIGINT      DEFAULT 0 COMMENT '头像uid',
    gender      TINYINT     NOT NULL DEFAULT 0 COMMENT '性别，0为未知，1为男，2为女',
    biography   VARCHAR(255) COMMENT '个人简介',
    birthday    DATE        COMMENT '生日',
    phone_number       VARCHAR(20) COMMENT '手机号',
    email_address       VARCHAR(50) COMMENT '邮箱',
    user_status TINYINT     NOT NULL DEFAULT 0 COMMENT '用户状态，0为正常，1为已注销',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(user_uid),
    UNIQUE INDEX user_name_unique_index(user_name),
    UNIQUE INDEX email_address_unique_index(email_address)
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
    article_type    TINYINT NOT NULL DEFAULT 0 COMMENT '状态，0为正常，1为已删除',
    view_count      BIGINT NOT NULL DEFAULT 0 COMMENT '浏览数',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(article_uid),
    INDEX user_uid_index(user_uid),
    INDEX article_type_index(article_type),
    FULLTEXT INDEX title_fulltext_index(title) WITH PARSER ngram  -- 用于全文搜索，ngram是一种分词器，用于将文本分割成多个单词，然后进行索引
) COMMENT '文章表';

DROP TABLE IF EXISTS t_comment;
CREATE TABLE t_comment(
    comment_uid     BIGINT NOT NULL COMMENT '评论uid',
    user_uid        BIGINT NOT NULL COMMENT '评论者uid',
    article_uid     BIGINT NOT NULL COMMENT '文章uid',
    reply_uid       BIGINT NOT NULL COMMENT '被回复评论uid，如果直接回复文章则为0',
    comment_content TEXT NOT NULL COMMENT '评论内容',   -- TEXT可存2^16-1=65535个字符，LONGTEXT可存2^32-1个字符
    comment_type    TINYINT NOT NULL DEFAULT 0 COMMENT '状态，0为正常，1为已删除',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(comment_uid),
    INDEX user_uid_index(user_uid)
) COMMENT '评论表';

DROP TABLE IF EXISTS t_vote;
CREATE TABLE t_vote(
    target_uid      BIGINT NOT NULL COMMENT '被赞踩者uid',
    user_uid        BIGINT NOT NULL COMMENT '赞踩者uid',
    vote_type       BOOL NOT NULL COMMENT 'true为赞，false为踩',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (target_uid, user_uid),
    INDEX target_uid_index(target_uid, vote_type)  -- 用于统计赞踩数
) COMMENT '赞踩表';

DROP TABLE IF EXISTS t_tag_article;
CREATE TABLE t_tag_article(
    tag_name        VARCHAR(20) NOT NULL COMMENT '标签名',
    article_uid     BIGINT NOT NULL COMMENT '文章uid',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(tag_name, article_uid),
    INDEX tag_name_index(tag_name)
) COMMENT '标签文章表';

DROP TABLE IF EXISTS t_collection;
CREATE TABLE t_collection(  -- 合集也兼当收藏夹的功能
    collection_uid      BIGINT NOT NULL COMMENT '合集uid',
    user_uid            BIGINT NOT NULL COMMENT '所属用户uid',
    collection_name     VARCHAR(255) NOT NULL COMMENT '合集名',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(collection_uid),
    INDEX user_uid_index(user_uid)
) COMMENT '合集表';

DROP TABLE IF EXISTS t_collection_article;
CREATE TABLE t_collection_article(
    collection_uid    BIGINT NOT NULL COMMENT '所属合集uid',
    article_uid     BIGINT NOT NULL COMMENT '文章uid',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY(collection_uid, article_uid),
    INDEX collection_uid_index(collection_uid),
    INDEX article_uid_index(article_uid)    -- 用于统计文章收藏数
) COMMENT '合集文章表';

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
INSERT INTO t_image(image_uid, image_url, image_hash) VALUES(0, "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png", 0)

DROP TABLE IF EXISTS t_notice;
CREATE TABLE t_notice (
    notice_uid      BIGINT PRIMARY KEY                  COMMENT '通知UID',
    receiver_uid    BIGINT  NOT NULL                    COMMENT '接收用户UID',
    trigger_uid     BIGINT  NOT NULL                    COMMENT '触发用户UID',
    subject_uid     BIGINT                              COMMENT '主体uid，notice_type为1~2时为点赞的文章/评论uid，notice_type为3~4时为发表的评论uid',
    notice_type     INT     NOT NULL                    COMMENT '通知类型: 0/1/2/3/4，关注、点赞文章、点赞评论，评论文章、评论评论',
    article_uid     BIGINT                              COMMENT '这条点赞/评论所属的文章uid',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_receiver (receiver_uid, create_time DESC) COMMENT '接收者查询索引'
) COMMENT '系统通知表';
