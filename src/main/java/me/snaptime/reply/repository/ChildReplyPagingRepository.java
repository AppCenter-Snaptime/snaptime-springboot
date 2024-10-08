package me.snaptime.reply.repository;

import me.snaptime.reply.domain.ChildReply;

import java.util.List;

public interface ChildReplyPagingRepository {

    List<ChildReply> findReplyPage(Long parentReplyId, Long pageNum);

    Long countByParentReplyId(Long parentReplyId);
}
