package me.snaptime.social.data.repository.reply;

import com.querydsl.core.Tuple;

import java.util.List;

public interface ParentReplyPagingRepository {

    List<Tuple> findReplyList(String loginId, Long snapId,Long pageNum);
}
