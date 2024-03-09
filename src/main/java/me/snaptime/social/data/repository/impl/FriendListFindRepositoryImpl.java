package me.snaptime.social.data.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.social.common.FriendSearchType;
import me.snaptime.social.data.repository.FriendListFindRepository;
import me.snaptime.user.data.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendListFindRepositoryImpl implements FriendListFindRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Object> findFriendList(User reqUser, FriendSearchType searchType, Long pageNum, String searchKeyword) {
        return null;
    }

    private OrderSpecifier createOrderSpecifier() {
        return null;
    }

    // 쿼리의 WHERE절을 생성하는 메소드, where절이 길어져 가독성을 위해 분리했습니다.
    private BooleanBuilder getBuilder(){
        BooleanBuilder builder = new BooleanBuilder();
        return builder;
    }
}
