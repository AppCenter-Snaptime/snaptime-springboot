package me.snaptime.snap.repository.impl;

import com.querydsl.core.Tuple;
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
    public List<Tuple> findSnapPage(Long pageNum, User reqUser) {

        Pageable pageable= PageRequest.of((int) (pageNum-1),10);

        // 내가 팔로우한 유저의 id를 가져오는 쿼리
        List<Long> followUserIds = jpaQueryFactory.select( user.userId ).distinct()
                .from(user)
                .join(friend).on(friend.receiver.userId.eq(user.userId))
                .where(friend.sender.userId.eq(reqUser.getUserId()))
                .fetch();

        // 나의 스냅도 커뮤니티에 포함되기 위해 나의 id추가
        followUserIds.add(reqUser.getUserId());

        List<Tuple> tuples =  jpaQueryFactory.select(
                        user.loginId, user.profilePhoto.profilePhotoId, user.name,
                        snap.id, snap.createdDate, snap.lastModifiedDate, snap.oneLineJournal, snap.fileName
                ).distinct()
                .from(friend)
                .join(snap).on(snap.user.userId.eq(user.userId))
                .where(user.userId.in(followUserIds).and(snap.isPrivate.isFalse()))
                .orderBy(snap.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //다음 페이지 유무체크를 위해 +1을 합니다.
                .fetch();

        if(tuples.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return tuples;
    }

}
