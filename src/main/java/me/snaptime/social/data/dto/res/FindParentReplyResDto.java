package me.snaptime.social.data.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.social.data.domain.QParentReply.parentReply;
import static me.snaptime.user.data.domain.QUser.user;

@Builder
public record FindParentReplyResDto(

        String writerLoginId,
        String writerProfilePhotoURL,
        String writerUserName,
        String content,
        Long replyId
) {
    public static FindParentReplyResDto toDto(Tuple result, String profilePhotoURL){
        return FindParentReplyResDto.builder()
                .writerLoginId(result.get(user.loginId))
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(result.get(user.name))
                .content(result.get(parentReply.content))
                .replyId(result.get(parentReply.id))
                .build();
    }
}
