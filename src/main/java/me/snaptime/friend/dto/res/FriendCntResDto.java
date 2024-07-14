package me.snaptime.friend.dto.res;

import lombok.Builder;

@Builder
public record FriendCntResDto(

        Long followerCnt,
        Long followingCnt

) {
    public static FriendCntResDto toDto(Long followerCnt, Long followingCnt){
        return FriendCntResDto.builder()
                .followerCnt(followerCnt)
                .followingCnt(followingCnt)
                .build();
    }
}
