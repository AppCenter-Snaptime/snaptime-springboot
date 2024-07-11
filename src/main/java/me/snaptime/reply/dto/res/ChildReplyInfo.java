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

    public static ChildReplyInfo toDto(Tuple result, String profilePhotoURL,String timeAgo){
        QUser tagUser = new QUser("tagUser");
        QUser writerUser = new QUser("writerUser");

        return ChildReplyInfo.builder()
                .writerLoginId(result.get(writerUser.loginId))
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(result.get(writerUser.name))
                .content(result.get(childReply.content))
                .tagUserLoginId(result.get(tagUser.loginId))
                .tagUserName(result.get(tagUser.name))
                .parentReplyId(result.get(childReply.parentReply.parentReplyId))
                .childReplyId(result.get(childReply.childReplyId))
                .timeAgo(timeAgo)
                .build();
    }
}
