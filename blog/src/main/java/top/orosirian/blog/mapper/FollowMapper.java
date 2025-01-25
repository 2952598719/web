package top.orosirian.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import top.orosirian.blog.entity.vo.UserBriefVO;

@Mapper
public interface FollowMapper {

    void insertFollow(Long masterUid, Long fanUid);

    void deleteFollow(Long masterUid, Long fanUid);

    Integer selectMasterNum(Long fanUid);

    Integer selectFanNum(Long masterUid);

    List<UserBriefVO> selectMasterList(Long fanUid);

    List<UserBriefVO> selectFanList(Long masterUid);

    boolean isFollowed(Long masterUid, Long fanUid);

}
