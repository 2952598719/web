package top.orosirian.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VoteMapper {
    
    void vote(Long targetUid, Long userUid, boolean voteType);

    void disvote(Long targetUid, Long userUid);

    // boolean checkVote(Long targetUid, Long userUid, boolean voteType);

    Boolean checkVote(Long targetUid, Long userUid);

}
