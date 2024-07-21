package me.snaptime.reply.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;
import me.snaptime.user.domain.QUser;

import static me.snaptime.reply.domain.QChildReply.childReply;


@Builder
public record ChildReplyInfo(

        String writerLoginId,
        String writerUserName,
        String writerProfilePhotoURL,
        String content,
        String tagUserLoginId,
        String tagUserName,
        Long parentReplyId,
        Long childReplyId,
        String timeAgo
) {

    public static ChildReplyInfo toDto(Tuple tuple, String profilePhotoURL,String timeAgo){
        QUser tagUser = new QUser("tagUser");
        QUser writerUser = new QUser("writerUser");

        return ChildReplyInfo.builder()
                .writerLoginId(tuple.get(writerUser.loginId))
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(tuple.get(writerUser.name))
                .content(tuple.get(childReply.content))
                .tagUserLoginId(tuple.get(tagUser.loginId))
                .tagUserName(tuple.get(tagUser.name))
                .parentReplyId(tuple.get(childReply.parentReply.parentReplyId))
                .childReplyId(tuple.get(childReply.childReplyId))
                .timeAgo(timeAgo)
                .build();
    }
}
