package me.snaptime.reply.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.reply.domain.QParentReply.parentReply;
import static me.snaptime.user.domain.QUser.user;


@Builder
public record ParentReplyInfo(

        String writerLoginId,
        String writerProfilePhotoURL,
        String writerUserName,
        String content,
        Long replyId,
        String timeAgo
) {
    public static ParentReplyInfo toDto(Tuple tuple, String profilePhotoURL,String timeAgo){
        return ParentReplyInfo.builder()
                .writerLoginId(tuple.get(user.loginId))
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(tuple.get(user.name))
                .content(tuple.get(parentReply.content))
                .replyId(tuple.get(parentReply.parentReplyId))
                .timeAgo(timeAgo)
                .build();
    }
}
