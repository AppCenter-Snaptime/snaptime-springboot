package me.snaptime.friendShip.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.friendShip.domain.QFriendShip.friendShip;
import static me.snaptime.user.domain.QUser.user;

@Builder
public record FriendInfo(

        String loginId,
        String profilePhotoURL,
        String userName,
        Long friendShipId,
        boolean isMyFriend
) {
    public static FriendInfo toDto(Tuple result, String profilePhotoURL, boolean isMyFriend){
        return FriendInfo.builder()
                .loginId(result.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .userName(result.get(user.name))
                .friendShipId(result.get(friendShip.friendShipId))
                .isMyFriend(isMyFriend)
                .build();
    }
}