package me.snaptime.reply.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface ChildReplyPagingRepository {

    List<Tuple> findReplyList(String loginId, Long parentReplyId, Long pageNum);
}
