package top.orosirian.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FollowMapper {

    void insertFollow(Long masterUid, Long fanUid);

    void deleteFollow(Long masterUid, Long fanUid);

    Integer selectMasterNum(Long fanUid);

    Integer selectFanNum(Long masterUid);

    // 获取的信息包括关注者的uid和昵称
    List<Long> selectMasterList(Long fanUid);

    List<Long> selectFanList(Long masterUid);

    boolean isFollowed(Long masterUid, Long fanUid);

}
