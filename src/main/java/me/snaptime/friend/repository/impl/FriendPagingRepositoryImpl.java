package me.snaptime.friend.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.repository.FriendPagingRepository;
import me.snaptime.user.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.friend.domain.QFriend.friend;
import static me.snaptime.user.domain.QUser.user;


@Repository
@RequiredArgsConstructor
public class FriendPagingRepositoryImpl implements FriendPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findFriendPage(User targetUser, FriendSearchType searchType, Long pageNum, String searchKeyword) {

        Pageable pageable= PageRequest.of((int) (pageNum-1),20);

        List<Tuple> tuples =  jpaQueryFactory.select(
                        user.email, user.profilePhoto.profilePhotoId, user.name, friend.friendId
                )
                .from(friend)
                .join(user).on(getJoinBuilder(searchType))
                .where(getWhereBuilder(targetUser, searchType,searchKeyword))
                .orderBy(createOrderSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //페이지의 크기
                .fetch();

        if(tuples.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return tuples;
    }

    private OrderSpecifier createOrderSpecifier() {
        return new OrderSpecifier(Order.ASC, user.userId);
    }

    // WHERE절을 동적으로 만들기 위한 메소드
    private BooleanBuilder getWhereBuilder(User targetUser, FriendSearchType friendSearchType, String searchKeyword){
        BooleanBuilder builder = new BooleanBuilder();

        if(friendSearchType == FriendSearchType.FOLLOWING){
            builder.and(friend.sender.userId.eq(targetUser.getUserId()));
        }
        else{
            builder.and(friend.receiver.userId.eq(targetUser.getUserId()));
        }

        if(searchKeyword !=null){
            builder.and(user.name.startsWith(searchKeyword).or(user.email.startsWith(searchKeyword)));
        }

        return builder;
    }
    
    // 조인조건을 동적으로 만들기 위한 메소드
    private BooleanBuilder getJoinBuilder(FriendSearchType friendSearchType){
        BooleanBuilder builder = new BooleanBuilder();
        if(friendSearchType == FriendSearchType.FOLLOWING){
            return builder.and(friend.receiver.userId.eq(user.userId));
        }
        else{
            return builder.and(friend.sender.userId.eq(user.userId));
        }
    }
}
