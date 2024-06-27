package me.snaptime.social.data.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.social.data.domain.QFriendShip.friendShip;
import static me.snaptime.user.data.domain.QUser.user;

@Builder
public record FindFriendResDto(

        String loginId,
        String profilePhotoURL,
        String userName,
        Long friendShipId
) {
    public static FindFriendResDto toDto(Tuple result,String profilePhotoURL){
        return FindFriendResDto.builder()
                .loginId(result.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .userName(result.get(user.name))
                .friendShipId(result.get(friendShip.friendShipId))
                .build();
    }
}
