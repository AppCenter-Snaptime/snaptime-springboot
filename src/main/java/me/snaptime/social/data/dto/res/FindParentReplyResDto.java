package me.snaptime.social.data.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.social.data.domain.QParentReply.parentReply;
import static me.snaptime.user.data.domain.QUser.user;

@Builder
public record FindParentReplyResDto(

        String loginId,
        String profilePhotoURL,
        String userName,
        String content,
        Long replyId
) {
    public static FindParentReplyResDto toDto(Tuple result, String profilePhotoURL){
        return FindParentReplyResDto.builder()
                .loginId(result.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .userName(result.get(user.name))
                .content(result.get(parentReply.content))
                .replyId(result.get(parentReply.id))
                .build();
    }
}
