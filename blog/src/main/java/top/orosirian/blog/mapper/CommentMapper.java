package top.orosirian.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import top.orosirian.blog.entity.vo.CommentVO;

@Mapper
public interface CommentMapper {

    void insertComment(Long commentUid, Long userUid, Long articleUid, Long replyUid, String commentContent);

    void deleteComment(Long commentUid);

    List<CommentVO> selectArticleCommentList(Long articleUid, Long userUid);

    Long[] selectArticleCommentUidList(Long articleUid);

    Long selectArticleUid(Long commentUid);

    Long selectAuthorUid(Long authorUid);

    String selectCommentContentPart(Long commentUid);

    CommentVO selectComment(Long commentUid);

    boolean isCommentExist(Long commentUid);

    boolean isCommentBelongToArticle(Long commentUid, Long articleUid);

    boolean isCommentBelongToUser(Long commentUid, Long userUid);

}
