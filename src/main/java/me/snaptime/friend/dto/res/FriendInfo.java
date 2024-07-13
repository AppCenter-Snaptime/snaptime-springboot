package me.snaptime.friend.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import static me.snaptime.friend.domain.QFriend.friend;
import static me.snaptime.user.domain.QUser.user;

@Builder
public record FriendInfo(

        String foundLoginId,
        String profilePhotoURL,
        String foundUserName,
        Long friendId,
        boolean isMyFriend
) {
    public static FriendInfo toDto(Tuple result, String profilePhotoURL, boolean isMyFriend){
        return FriendInfo.builder()
                .foundLoginId(result.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .foundUserName(result.get(user.name))
                .friendId(result.get(friend.friendId))
                .isMyFriend(isMyFriend)
                .build();
    }
}
