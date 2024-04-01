package me.snaptime.social.data.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.common.FriendSearchType;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.repository.FindFriendListRepository;
import me.snaptime.user.data.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.social.data.domain.QFriendShip.friendShip;
import static me.snaptime.user.data.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class FindFriendListRepositoryImpl implements FindFriendListRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findFriendList(User reqUser, FriendSearchType searchType, Long pageNum, String searchKeyword) {
        Pageable pageable= PageRequest.of((int) (pageNum-1),30);

        List<Tuple> result =  jpaQueryFactory.select(
                        user.loginId,user.profilePhoto.id,user.name,friendShip.id
                )
                .from(friendShip)
                .join(user).on(getJoinBuilder(searchType))
                .where(getWhereBuilder(reqUser, searchType,searchKeyword))
                .orderBy(createOrderSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이지의 크기
                .fetch();

        if(result.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return result;
    }

    private OrderSpecifier createOrderSpecifier() {
        return new OrderSpecifier(Order.ASC, user.id);
    }

    // WHERE절을 동적으로 만들기 위한 메소드
    private BooleanBuilder getWhereBuilder(User reqUser, FriendSearchType friendSearchType, String searchKeyword){
        BooleanBuilder builder = new BooleanBuilder();

        if(friendSearchType == FriendSearchType.FOLLOWING){
            builder.and(friendShip.fromUser.id.eq(reqUser.getId()));
            builder.and(friendShip.friendStatus.eq(FriendStatus.FOLLOW));
        }
        else{
            builder.and(friendShip.toUser.id.eq(reqUser.getId()));
            builder.and(friendShip.friendStatus.eq(FriendStatus.FOLLOW));
        }
        if(searchKeyword !=null){
            builder.and(user.name.contains(searchKeyword));
        }

        return builder;
    }
    
    // 조인조건을 동적으로 만들기 위한 메소드
    private BooleanBuilder getJoinBuilder(FriendSearchType friendSearchType){
        BooleanBuilder builder = new BooleanBuilder();
        if(friendSearchType == FriendSearchType.FOLLOWING){
            return builder.and(friendShip.toUser.id.eq(user.id));
        }
        else{
            return builder.and(friendShip.fromUser.id.eq(user.id));
        }
    }
}
