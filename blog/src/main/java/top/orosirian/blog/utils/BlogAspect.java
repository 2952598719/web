package top.orosirian.blog.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BlogAspect {

    private static final Logger log = LoggerFactory.getLogger(BlogAspect.class);

    /**
     * ArticleService
     */
    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.addArticle(..))")
    public void addArticleMethod() {}
    @AfterReturning(pointcut = "addArticleMethod()", returning = "result")
    public void afterAddArticleSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}发表文章成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.modifyArticle(..))")
    public void modifyArticleMethod() {}
    @AfterReturning(pointcut = "modifyArticleMethod()", returning = "result")
    public void afterModifyArticleSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long articleUid = (Long) args[0];
        Long userUid = (Long) args[1];
        log.info("用户{}修改文章{}成功", userUid, articleUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.removeArticle(..))")
    public void removeArticleMethod() {}
    @AfterReturning(pointcut = "removeArticleMethod()", returning = "result")
    public void afterRemoveArticleSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long articleUid = (Long) args[0];
        Long userUid = (Long) args[1];
        log.info("用户{}删除文章{}成功", userUid, articleUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchArticle(..))")
    public void searchArticleMethod() {}
    @AfterReturning(pointcut = "searchArticleMethod()", returning = "result")
    public void afterSearchArticleSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long articleUid = (Long) args[0];
        log.info("获取文章{}成功", articleUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchArticleList(..))")
    public void searchArticleListMethod() {}
    @AfterReturning(pointcut = "searchArticleListMethod()", returning = "result")
    public void afterSearchArticleListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer currentPage = (Integer) args[0];
        log.info("获取第{}页文章成功", currentPage);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchTitleArticleList(..))")
    public void searchTitleArticleListMethod() {}
    @AfterReturning(pointcut = "searchTitleArticleListMethod()", returning = "result")
    public void afterSearchTitleArticleListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer currentPage = (Integer) args[0];
        String title = (String) args[0];
        log.info("获取搜索{}第{}页文章成功", title, currentPage);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchUserArticleList(..))")
    public void searchUserArticleListMethod() {}
    @AfterReturning(pointcut = "searchUserArticleListMethod()", returning = "result")
    public void afterSearchUserArticleListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer currentPage = (Integer) args[0];
        log.info("获取用户第{}页文章成功", currentPage);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchMasterArticleList(..))")
    public void searchMasterArticleListMethod() {}
    @AfterReturning(pointcut = "searchMasterArticleListMethod()", returning = "result")
    public void afterSearchMasterArticleListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer currentPage = (Integer) args[0];
        Long userUid = (Long) args[2];
        log.info("获取用户{}关注列表第{}页文章成功", userUid, currentPage);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchVoteType(..))")
    public void searchVoteTypeMethod() {}
    @AfterReturning(pointcut = "searchVoteTypeMethod()", returning = "result")
    public void afterSearchVoteTypeSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long articleUid = (Long) args[0];
        log.info("获取文章{}点赞状态成功", articleUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchTagArticleList(..))")
    public void searchTagArticleListMethod() {}
    @AfterReturning(pointcut = "searchTagArticleListMethod()", returning = "result")
    public void afterSearchTagArticleListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer currentPage = (Integer) args[0];
        String tagName = (String) args[2];
        log.info("获取Tag {} 第{}页文章成功", currentPage, tagName);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchArticleTag(..))")
    public void searchArticleTagMethod() {}
    @AfterReturning(pointcut = "searchArticleTagMethod()", returning = "result")
    public void afterSearchArticleTagSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long articleUid = (Long) args[0];
        log.info("获取文章{}的标签成功", articleUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.collect(..))")
    public void collectMethod() {}
    @AfterReturning(pointcut = "collectMethod()", returning = "result")
    public void afterCollectSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long collectionUid = (Long) args[1];
        Long articleUid = (Long) args[2];
        log.info("文章{}已收藏至合集{}", articleUid, collectionUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.uncollect(..))")
    public void uncollectMethod() {}
    @AfterReturning(pointcut = "uncollectMethod()", returning = "result")
    public void afterUncollectSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long collectionUid = (Long) args[1];
        Long articleUid = (Long) args[2];
        log.info("文章{}已从合集{}取消收藏", articleUid, collectionUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.checkCollect(..))")
    public void checkCollectMethod() {}
    @AfterReturning(pointcut = "checkCollectMethod()", returning = "result")
    public void afterCheckCollectSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        Long articleUid = (Long) args[2];
        log.info("{}获取{}收藏情况成功", userUid, articleUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.ArticleService.searchCollectionArticleList(..))")
    public void searchCollectionArticleListMethod() {}
    @AfterReturning(pointcut = "searchCollectionArticleListMethod()", returning = "result")
    public void afterSearchCollectionArticleListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long collectionUid = (Long) args[2];
        log.info("获取合集{}文章列表成功", collectionUid);
    }


    /**
      * CollectionService
      */
    @Pointcut("execution(* top.orosirian.blog.service.CollectionService.addCollection(..))")
    public void addCollectionMethod() {}
    @AfterReturning(pointcut = "addCollectionMethod()", returning = "result")
    public void afterAddCollectionSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        String collectionName = (String) args[1];
        log.info("用户{}新增收藏夹{}成功", userUid, collectionName);
    }

    @Pointcut("execution(* top.orosirian.blog.service.CollectionService.removeCollection(..))")
    public void removeCollectionMethod() {}
    @AfterReturning(pointcut = "removeCollectionMethod()", returning = "result")
    public void afterRemoveCollectionSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        Long collectionUid = (Long) args[1];
        log.info("用户{}删除收藏夹{}成功", userUid, collectionUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.CollectionService.modifyCollection(..))")
    public void modifyCollectionMethod() {}
    @AfterReturning(pointcut = "modifyCollectionMethod()", returning = "result")
    public void afterModifyCollectionSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        Long collectionUid = (Long) args[1];
        log.info("用户{}修改收藏夹{}成功", userUid, collectionUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.CollectionService.searchMyCollectionList(..))")
    public void searchMyCollectionListMethod() {}
    @AfterReturning(pointcut = "searchMyCollectionListMethod()", returning = "result")
    public void afterSearchMyCollectionListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}获取个人收藏夹成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.CollectionService.searchCollectionOfArticleList(..))")
    public void searchCollectionOfArticleListMethod() {}
    @AfterReturning(pointcut = "searchCollectionOfArticleListMethod()", returning = "result")
    public void afterSearchCollectionOfArticleListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        Long articleUid = (Long) args[1];
        log.info("获取用户{}关于文章{}的收藏夹成功", userUid, articleUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.CollectionService.searchCollectionUidList(..))")
    public void searchCollectionUidListMethod() {}
    @AfterReturning(pointcut = "searchCollectionUidListMethod()", returning = "result")
    public void afterSearchCollectionUidListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("获取用户{}收藏夹列表成功", userUid);
    }


    /**
     * CommentService
     */
    @Pointcut("execution(* top.orosirian.blog.service.CommentService.addComment(..))")
    public void addCommentMethod() {}
    @AfterReturning(pointcut = "addCommentMethod()", returning = "result")
    public void afterAddCommentSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}发表评论成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.CommentService.removeComment(..))")
    public void removeCommentMethod() {}
    @AfterReturning(pointcut = "removeCommentMethod()", returning = "result")
    public void afterRemoveCommentSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        Long commentUid = (Long) args[1];
        log.info("用户{}删除评论{}成功", userUid, commentUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.CommentService.searchArticleComment(..))")
    public void searchArticleCommentMethod() {}
    @AfterReturning(pointcut = "searchArticleCommentMethod()", returning = "result")
    public void afterSearchArticleCommentSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long articleUid = (Long) args[0];
        log.info("获取文章{}评论列表成功", articleUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.CommentService.searchComment(..))")
    public void searchCommentMethod() {}
    @AfterReturning(pointcut = "searchCommentMethod()", returning = "result")
    public void afterSearchCommentSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long commentUid = (Long) args[0];
        log.info("获取评论{}成功", commentUid);
    }


    /**
     * UserService
     */
    @Pointcut("execution(* top.orosirian.blog.service.UserService.register(..))")
    public void registerMethod() {}
    @AfterReturning(pointcut = "registerMethod()", returning = "result")
    public void afterRegisterSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        String userName = (String) args[0];
        String nickName = (String) args[0];
        log.info("用户注册成功，用户名'{}'，昵称'{}'", userName, nickName);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.unregister(..))")
    public void unregisterMethod() {}
    @AfterReturning(pointcut = "unregisterMethod()", returning = "result")
    public void afterUnregisterSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}注销成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.login(..))")
    public void loginMethod() {}
    @AfterReturning(pointcut = "loginMethod()", returning = "result")
    public void afterLoginSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        String userName = (String) args[0];
        log.info("用户{}登陆成功", userName);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.emaillogin(..))")
    public void emailloginMethod() {}
    @AfterReturning(pointcut = "emailloginMethod()", returning = "result")
    public void afterEmailloginSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}通过邮箱登录成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.sendcode(..))")
    public void sendcodeMethod() {}
    @AfterReturning(pointcut = "sendcodeMethod()", returning = "result")
    public void afterSendcodeSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        String emailAddress = (String) args[0];
        log.info("邮件{}任务提交成功", emailAddress);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.logout(..))")
    public void logoutMethod() {}
    @AfterReturning(pointcut = "logoutMethod()", returning = "result")
    public void afterLogoutSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}登出成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.checkLogin(..))")
    public void checkLoginMethod() {}
    @AfterReturning(pointcut = "checkLoginMethod()", returning = "result")
    public void afterCheckLoginSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("获取用户{}基础信息成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.searchUserUidByUserName(..))")
    public void searchUserUidByUserNameMethod() {}
    @AfterReturning(pointcut = "searchUserUidByUserNameMethod()", returning = "result")
    public void afterSearchUserUidByUserNameSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        String userName = (String) args[0];
        log.info("获取用户{}的uid成功", userName);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.searchUserByUserUid(..))")
    public void searchUserByUserUidMethod() {}
    @AfterReturning(pointcut = "searchUserByUserUidMethod()", returning = "result")
    public void afterSearchUserByUserUidSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("获取用户{}详细信息成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.modifyPassWord(..))")
    public void modifyPassWordMethod() {}
    @AfterReturning(pointcut = "modifyPassWordMethod()", returning = "result")
    public void afterModifyPassWordSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}密码更新成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.modifyUserInfo(..))")
    public void modifyUserInfoMethod() {}
    @AfterReturning(pointcut = "modifyUserInfoMethod()", returning = "result")
    public void afterModifyUserInfoSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}个人信息更新成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.modifyAvatar(..))")
    public void modifyAvatarMethod() {}
    @AfterReturning(pointcut = "modifyAvatarMethod()", returning = "result")
    public void afterModifyAvatarSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}头像更新成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.follow(..))")
    public void followMethod() {}
    @AfterReturning(pointcut = "followMethod()", returning = "result")
    public void afterFollowSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long masterUid = (Long) args[0];
        Long fanUid = (Long) args[1];
        log.info("{}关注{}成功", masterUid, fanUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.removeFollow(..))")
    public void removeFollowMethod() {}
    @AfterReturning(pointcut = "removeFollowMethod()", returning = "result")
    public void afterRemoveFollowSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long masterUid = (Long) args[0];
        Long fanUid = (Long) args[1];
        log.info("{}取关{}成功", fanUid, masterUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.checkFollow(..))")
    public void checkFollowMethod() {}
    @AfterReturning(pointcut = "checkFollowMethod()", returning = "result")
    public void afterCheckFollowSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long masterUid = (Long) args[0];
        Long fanUid = (Long) args[1];
        log.info("{}关注{}情况获取成功", fanUid, masterUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.searchMasterNum(..))")
    public void searchMasterNumMethod() {}
    @AfterReturning(pointcut = "searchMasterNumMethod()", returning = "result")
    public void afterSearchMasterNumSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}关注人数获取成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.searchMasterList(..))")
    public void searchMasterListMethod() {}
    @AfterReturning(pointcut = "searchMasterListMethod()", returning = "result")
    public void afterSearchMasterListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer currentPage = (Integer) args[0];
        Long userUid = (Long) args[2];
        log.info("获取用户{}的第{}页关注列表成功", userUid, currentPage);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.searchFanNum(..))")
    public void searchFanNumMethod() {}
    @AfterReturning(pointcut = "searchFanNumMethod()", returning = "result")
    public void afterSearchFanNumSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("用户{}粉丝人数获取成功", userUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.searchFanList(..))")
    public void searchFanListMethod() {}
    @AfterReturning(pointcut = "searchFanListMethod()", returning = "result")
    public void afterSearchFanListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer currentPage = (Integer) args[0];
        Long userUid = (Long) args[2];
        log.info("获取用户{}的第{}页粉丝列表成功", userUid, currentPage);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.searchNoticeList(..))")
    public void searchNoticeListMethod() {}
    @AfterReturning(pointcut = "searchNoticeListMethod()", returning = "result")
    public void afterSearchNoticeListSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer currentPage = (Integer) args[0];
        Long userUid = (Long) args[2];
        log.info("获取用户{}的第{}页通知列表成功", userUid, currentPage);
    }

    @Pointcut("execution(* top.orosirian.blog.service.UserService.searchUnreadNum(..))")
    public void searchUnreadNumMethod() {}
    @AfterReturning(pointcut = "searchUnreadNumMethod()", returning = "result")
    public void afterSearchUnreadNumSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[0];
        log.info("获取用户{}未读消息数成功", userUid);
    }

    /**
     * VoteService
     */
    @Pointcut("execution(* top.orosirian.blog.service.VoteService.vote(..))")
    public void voteMethod() {}
    @AfterReturning(pointcut = "voteMethod()", returning = "result")
    public void afterVoteSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long targetUid = (Long) args[0];
        Long userUid = (Long) args[1];
        String type = ((Boolean) args[2]) ? "点赞" : "点踩";
        log.info("用户{}{}{}成功", userUid, type, targetUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.VoteService.disvote(..))")
    public void disvoteMethod() {}
    @AfterReturning(pointcut = "disvoteMethod()", returning = "result")
    public void afterDisvoteSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long targetUid = (Long) args[0];
        Long userUid = (Long) args[1];
        String type = ((Boolean) args[2]) ? "点赞" : "点踩";
        log.info("用户{}取消{}{}成功", userUid, type, targetUid);
    }

    @Pointcut("execution(* top.orosirian.blog.service.VoteService.checkVote(..))")
    public void checkVoteMethod() {}
    @AfterReturning(pointcut = "checkVoteMethod()", returning = "result")
    public void afterCheckVoteSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long targetUid = (Long) args[0];
        Long userUid = (Long) args[1];
        log.info("获取用户{}赞踩{}情况成功", userUid, targetUid);
    }
    
}
