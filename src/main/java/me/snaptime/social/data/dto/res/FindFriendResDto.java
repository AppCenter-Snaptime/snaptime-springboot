package me.snaptime.social.data.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.user.data.domain.QUser.user;

@Builder
public record FindFriendResDto(

        Long userId,
        Long profilePhotoId,
        String userName
) {
    public static FindFriendResDto toDto(Tuple result){
        return FindFriendResDto.builder()
                .userId(result.get(user.id))
                .profilePhotoId(result.get(user.profilePhoto.id))
                .userName(result.get(user.name))
                .build();
    }
}
