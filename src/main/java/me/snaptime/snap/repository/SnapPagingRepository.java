package me.snaptime.snap.repository;

import com.querydsl.core.Tuple;
import me.snaptime.user.domain.User;

import java.util.List;

public interface SnapPagingRepository {

    /*
        10개의 스냅 + 다음페이지 유무확인을 위한 1개의 스냅을 반환합니다.
    */
    List<Tuple> findSnapPaging(String loginId, Long pageNum, User reqUser);
}
