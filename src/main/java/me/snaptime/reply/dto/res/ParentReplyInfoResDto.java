package me.snaptime.reply.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.reply.domain.QParentReply.parentReply;
import static me.snaptime.user.domain.QUser.user;


@Builder
public record ParentReplyInfoResDto(

        String writerLoginId,
        String writerProfilePhotoURL,
        String writerUserName,
        String content,
        Long replyId,
        String timeAgo
) {
    public static ParentReplyInfoResDto toDto(Tuple tuple, String profilePhotoURL, String timeAgo){
        return ParentReplyInfoResDto.builder()
                .writerLoginId(tuple.get(user.loginId))
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(tuple.get(user.name))
                .content(tuple.get(parentReply.content))
                .replyId(tuple.get(parentReply.parentReplyId))
                .timeAgo(timeAgo)
                .build();
    }
}
