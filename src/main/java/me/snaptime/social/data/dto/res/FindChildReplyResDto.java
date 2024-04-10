package me.snaptime.social.data.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;
import me.snaptime.user.data.domain.QUser;

import static me.snaptime.social.data.domain.QChildReply.childReply;

@Builder
public record FindChildReplyResDto(

        String loginId,
        String userName,
        String profilePhotoURL,
        String content,
        String tagUserLoginId,
        String tagUserName,
        Long parentReplyId,
        Long childReplyId

) {
    public static FindChildReplyResDto toDto(Tuple result, String profilePhotoURL){
        QUser tagUser = new QUser("tagUser");
        QUser writerUser = new QUser("writerUser");

        return FindChildReplyResDto.builder()
                .loginId(result.get(writerUser.loginId))
                .profilePhotoURL(profilePhotoURL)
                .userName(result.get(writerUser.name))
                .content(result.get(childReply.content))
                .tagUserLoginId(result.get(tagUser.loginId))
                .tagUserName(result.get(tagUser.name))
                .parentReplyId(result.get(childReply.parentReply.id))
                .childReplyId(result.get(childReply.id))
                .build();
    }
}
