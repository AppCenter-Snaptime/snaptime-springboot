package me.snaptime.social.data.repository.reply.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.data.repository.reply.ParentReplyPagingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.social.data.domain.QParentReply.parentReply;
import static me.snaptime.user.data.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class ParentReplyPagingRepositoryImpl implements ParentReplyPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findReplyList(String loginId, Long snapId,Long pageNum) {
        Pageable pageable= PageRequest.of((int) (pageNum-1),20);

        List<Tuple> result =  jpaQueryFactory.select(
                        user.loginId,user.profilePhoto.id,user.name,
                        parentReply.content,parentReply.parentReplyId
                )
                .from(parentReply)
                .join(user).on(parentReply.user.id.eq(user.id))
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
