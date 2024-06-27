package me.snaptime.social.data.repository.reply.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.data.repository.reply.ChildReplyPagingRepository;
import me.snaptime.user.data.domain.QUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.social.data.domain.QChildReply.childReply;


@Repository
@RequiredArgsConstructor
public class ChildReplyPagingRepositoryImpl implements ChildReplyPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findReplyList(String loginId, Long parentReplyId, Long pageNum) {
        Pageable pageable= PageRequest.of((int) (pageNum-1),20);
        QUser tagUser = new QUser("tagUser");
        QUser writerUser = new QUser("writerUser");


        List<Tuple> result =  jpaQueryFactory.select(
                        childReply.childReplyId,childReply.content,childReply.parentReply.parentReplyId,
                        writerUser.name,writerUser.loginId,tagUser.name,tagUser.loginId,
                        writerUser.profilePhoto.id
                )
                .from(childReply)
                .leftJoin(tagUser).on(childReply.replyTagUser.id.eq(tagUser.id))
                .join(writerUser).on(childReply.user.id.eq(writerUser.id))
                .where(childReply.parentReply.parentReplyId.eq(parentReplyId))
                .orderBy(createOrderSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) //페이지의 크기
                .fetch();

        if(result.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return result;
    }

    private OrderSpecifier createOrderSpecifier() {
        return new OrderSpecifier(Order.DESC, childReply.createdDate);
    }

}
