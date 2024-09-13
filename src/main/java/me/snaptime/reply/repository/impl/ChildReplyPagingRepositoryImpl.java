package me.snaptime.reply.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.domain.ChildReply;
import me.snaptime.reply.repository.ChildReplyPagingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.snaptime.reply.domain.QChildReply.childReply;
import static me.snaptime.reply.domain.QParentReply.parentReply;


@Repository
@RequiredArgsConstructor
public class ChildReplyPagingRepositoryImpl implements ChildReplyPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ChildReply> findReplyPage(Long parentReplyId, Long pageNum) {
        Pageable pageable= PageRequest.of((int) (pageNum-1),20);

        List<ChildReply> childReplies = jpaQueryFactory.select( childReply )
                .from(childReply)
                .where(parentReply.parentReplyId.eq(parentReplyId))
                .orderBy(childReply.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) //페이지의 크기
                .fetch();

        if(childReplies.size() == 0)
            throw new CustomException(ExceptionCode.PAGE_NOT_FOUND);

        return childReplies;
    }
}
