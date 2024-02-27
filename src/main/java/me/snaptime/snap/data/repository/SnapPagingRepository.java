package me.snaptime.snap.data.repository;

import com.querydsl.core.Tuple;
import me.snaptime.user.data.domain.User;

import java.util.List;

public interface SnapPagingRepository {
    List<Tuple> findSnapPaging(String loginId, Long pageNum, User reqUser);
}
