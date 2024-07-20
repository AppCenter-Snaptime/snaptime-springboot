package me.snaptime.reply.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.repository.ParentReplyPagingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.reply.domain.QParentReply.parentReply;
import static me.snaptime.user.domain.QUser.user;


@Repository
@RequiredArgsConstructor
public class ParentReplyPagingRepositoryImpl implements ParentReplyPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findReplyList(Long snapId,Long pageNum) {
        Pageable pageable= PageRequest.of((int) (pageNum-1),20);

        List<Tuple> result =  jpaQueryFactory.select(
                        user.loginId,user.profilePhoto.profilePhotoId,user.name,
                        parentReply.content,parentReply.parentReplyId,parentReply.lastModifiedDate
                )
                .from(parentReply)
                .join(user).on(parentReply.user.userId.eq(user.userId))
                .where(parentReply.snap.id.eq(snapId))
                .orderBy(createOrderSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //페이지의 크기
                .fetch();

        if(result.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return result;
    }

    private OrderSpecifier createOrderSpecifier() {
        return new OrderSpecifier(Order.DESC, parentReply.createdDate);
    }

}
