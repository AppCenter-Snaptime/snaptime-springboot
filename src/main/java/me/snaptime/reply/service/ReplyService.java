package me.snaptime.reply.service;

import me.snaptime.reply.dto.req.AddChildReplyReqDto;
import me.snaptime.reply.dto.req.AddParentReplyReqDto;
import me.snaptime.reply.dto.res.FindChildReplyResDto;
import me.snaptime.reply.dto.res.FindParentReplyResDto;

public interface ReplyService {

    // 댓글을 추가합니다.
    void addParentReply(String loginId, AddParentReplyReqDto addParentReplyReqDto);

    // 댓글에 대댓글을 추가합니다.
    void addChildReply(String loginId, AddChildReplyReqDto addChildReplyReqDto);

    /*
        댓글을 최신순으로 20개씩 조회합니다.
        커뮤니티기능이므로 페이징처리를 합니다.
    */
    FindParentReplyResDto readParentReply(Long snapId, Long pageNum);

    /*
        댓글에 달린 대댓글을 최신순으로 20개씩 조회합니다.
        커뮤니티기능이므로 페이징처리를 합니다.
    */
    FindChildReplyResDto readChildReply(Long parentReplyId, Long pageNum);

    // 댓글을 변경합니다.
    void updateParentReply(String loginId ,Long parentReplyId, String newContent);

    // 대댓글을 변경합니다.
    void updateChildReply(String loginId, Long childReplyId, String newContent);

    /*
        댓글을 삭제합니다.
        댓글에 달린 모든 대댓글까지 함께 삭제됩니다.
    */
    void deleteParentReply(String loginId, Long parentReplyId);

    // 대댓글을 삭제합니다.
    void deleteChildReply(String loginId, Long childReplyId);
}
