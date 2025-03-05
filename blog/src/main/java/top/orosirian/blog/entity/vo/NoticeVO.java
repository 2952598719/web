package top.orosirian.blog.entity.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// {triggerName} {noticeType} 了你的 {subjectUid}      -- 时间
public class NoticeVO {

    private String noticeUid;

    private String triggerName;   // 通知引发人的userName

    private String subjectUid;    // 通知关联对象的Uid

    private Integer noticeType; // 通知种类

    private String articleUid;

    private LocalDateTime updateTime;  // 通知更新时间

    private String subjectContent; // 部分内容，如果是文章就是标题，评论就是内容的前30个字符

}
