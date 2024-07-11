package me.snaptime.snap.repository;

import com.querydsl.core.Tuple;
import me.snaptime.user.domain.User;

import java.util.List;

public interface SnapPagingRepository {
    List<Tuple> findSnapPaging(String loginId, Long pageNum, User reqUser);
}
