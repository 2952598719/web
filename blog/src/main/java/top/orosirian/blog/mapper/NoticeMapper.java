package top.orosirian.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import top.orosirian.blog.entity.vo.NoticeVO;

@Mapper
public interface NoticeMapper {

    void insertNoticeFollow(Long noticeUid, Long receiverUid, Long triggerUid, Integer noticeType);

    void insertNotice(Long noticeUid, Long receiverUid, Long triggerUid, Long subjectUid, Integer noticeType, Long articleUid);

    void updateNotice(Long noticeUid, Integer noticeType);

    List<NoticeVO> selectNoticeList(Long userUid);

    Long selectNoticeUid(Long triggerUid, Long subjectUid);    // 点赞只有一条记录，因此只需要点赞者Uid和点赞对象即可找到
    
    Long selectNoticeUidByFollow(Long masterUid, Long fanUid);

}
