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
        Long userUid = (Long) args[1];
        Long articleUid = (Long) args[0];
        log.info("用户{}修改文章{}成功", userUid, articleUid);
    }



     /**
      * CollectionService
      */



    /**
     * CommentService
     */


    /**
     * UserService
     */




    /**
     * VoteService
     */




    @Pointcut("execution(* top.orosirian.blog.service.CommentService.addComment(..))")
    public void addCommentMethod() {}

    @AfterReturning(pointcut = "addCommentMethod()", returning = "result")
    public void afterAddCommentSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userUid = (Long) args[1];
        Long commentUid = (Long) args[0]; // 假设参数顺序正确
        log.info("用户{}发表评论{}成功", userUid, commentUid);
    }
    
}
