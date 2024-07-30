package me.snaptime.reply.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.repository.ChildReplyPagingRepository;
import me.snaptime.user.domain.QUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.reply.domain.QChildReply.childReply;


@Repository
@RequiredArgsConstructor
public class ChildReplyPagingRepositoryImpl implements ChildReplyPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findReplyPage(Long parentReplyId, Long pageNum) {
        Pageable pageable= PageRequest.of((int) (pageNum-1),20);
        QUser tagUser = new QUser("tagUser");
        QUser writerUser = new QUser("writerUser");
        
        List<Tuple> tuples =  jpaQueryFactory.select(
                        childReply.childReplyId,childReply.content,childReply.parentReply.parentReplyId,
                        writerUser.name,writerUser.loginId,tagUser.name,tagUser.loginId,
                        writerUser.profilePhoto.profilePhotoId, childReply.lastModifiedDate
                )
                .from(childReply)
                .leftJoin(tagUser).on(childReply.replyTagUser.userId.eq(tagUser.userId))
                .join(writerUser).on(childReply.user.userId.eq(writerUser.userId))
                .where(childReply.parentReply.parentReplyId.eq(parentReplyId))
                .orderBy(createOrderSpecifier())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //페이지의 크기
                .fetch();

        if(tuples.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return tuples;
    }

    private OrderSpecifier createOrderSpecifier() {
        return new OrderSpecifier(Order.DESC, childReply.createdDate);
    }

}
