package me.snaptime.snap.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.repository.SnapPagingRepository;
import me.snaptime.user.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.friend.domain.QFriend.friend;
import static me.snaptime.snap.domain.QSnap.snap;
import static me.snaptime.user.domain.QUser.user;


@Repository
@RequiredArgsConstructor
public class SnapPagingRepositoryImpl implements SnapPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findSnapPaging(Long pageNum, User reqUser) {

        Pageable pageable= PageRequest.of((int) (pageNum-1),10);

        List<Tuple> result =  jpaQueryFactory.select(
                        user.loginId, user.profilePhoto.id, user.name,
                        snap.id, snap.createdDate, snap.lastModifiedDate, snap.oneLineJournal, snap.fileName
                ).distinct()
                .from(friend)
                .rightJoin(user).on(friend.receiver.id.eq(user.id))
                .join(snap).on(snap.user.id.eq(user.id))
                .where(getBuilder(reqUser))
                .orderBy(createOrderSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //다음 페이지 유무체크를 위해 +1을 합니다.
                .fetch();

        if(result.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return result;
    }

    // 정렬 조건을 동적으로 생성하는 메소드
    private OrderSpecifier createOrderSpecifier() {
        return new OrderSpecifier(Order.DESC, snap.createdDate);
    }

    // 쿼리의 WHERE절을 생성하는 메소드
    private BooleanBuilder getBuilder(User reqUser){
        BooleanBuilder builder = new BooleanBuilder();

        builder.and( friend.sender.id.eq(reqUser.getId()).and(snap.isPrivate.isFalse()) );
        builder.or( user.eq(reqUser).and(snap.isPrivate.isFalse()) );

        return builder;
    }


}
