package me.snaptime.user.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.repository.UserPagingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserPagingRepositoryImpl implements UserPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findUserPageByName(String searchKeyword, Long pageNum){
        Pageable pageable= PageRequest.of((int) (pageNum-1),20);

        List<Tuple> tuples =  jpaQueryFactory.select(
                        user.email, user.profilePhoto.profilePhotoId, user.name,user.nickName
                )
                .from(user)
                .where(user.name.startsWith(searchKeyword).or(user.email.startsWith(searchKeyword).or(user.nickName.startsWith(searchKeyword))))
                .orderBy(new OrderSpecifier(Order.ASC, user.userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //페이지의 크기
                .fetch();

        if(tuples.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return  tuples;
    }
}
