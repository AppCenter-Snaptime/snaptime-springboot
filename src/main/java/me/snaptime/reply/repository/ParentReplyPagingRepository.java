package me.snaptime.reply.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface ParentReplyPagingRepository {

    List<Tuple> findReplyPage(Long snapId, Long pageNum);
}
