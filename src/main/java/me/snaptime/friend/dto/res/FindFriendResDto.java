package me.snaptime.friend.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindFriendResDto(

        List<FriendInfo> friendInfos,
        boolean hasNextPage
) {
    public static FindFriendResDto toDto(List<FriendInfo> friendInfos, boolean hasNextPage){
        return FindFriendResDto.builder()
                .friendInfos(friendInfos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
