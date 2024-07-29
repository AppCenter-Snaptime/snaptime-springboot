package me.snaptime.user.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface UserPagingRepository {

    List<Tuple> findUserPageByName(String searchKeyword, Long pageNum);
}
