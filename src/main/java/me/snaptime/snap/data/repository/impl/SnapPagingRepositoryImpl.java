package me.snaptime.snap.data.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.repository.SnapPagingRepository;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.user.data.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.social.data.domain.QFriendShip.friendShip;
import static me.snaptime.user.data.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class SnapPagingRepositoryImpl implements SnapPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // snap 페이징 조회
    @Override
    public List<Tuple> findSnapPaging(String loginId, Long pageNum, User reqUser) {

        Pageable pageable= PageRequest.of((int) (pageNum-1),10);

        List<Tuple> result =  jpaQueryFactory.select(
                        user.id, user.profilePhoto.id, user.name,
                        snap.id, snap.createdDate, snap.lastModifiedDate, snap.oneLineJournal, snap.photo.id
                )
                .from(friendShip)
                .rightJoin(user).on(friendShip.toUser.id.eq(user.id)).fetchJoin()
                .join(snap).on(snap.user.id.eq(user.id)).fetchJoin()
                .where(getBuilder(reqUser))
                .orderBy(createOrderSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이지의 크기
                .fetch();

        if(result.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return result;
    }

    // 정렬 조건을 동적으로 생성하는 메소드, 추후에 기능추가 시 확장에 용이하게 하기 위해 해당 로직을 분리
    private OrderSpecifier createOrderSpecifier() {
        return new OrderSpecifier(Order.DESC, snap.createdDate);
    }

    // 쿼리의 WHERE절을 생성하는 메소드, where절이 길어져 가독성을 위해 분리했습니다.
    private BooleanBuilder getBuilder(User reqUser){
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.id.eq(reqUser.getId()));
        builder.or(friendShip.fromUser.eq(reqUser)
                .and(friendShip.friendStatus.eq(FriendStatus.FOLLOW).or(friendShip.friendStatus.eq(FriendStatus.WAITING))));
        builder.and(snap.isPrivate.isFalse());
        return builder;
    }


}
