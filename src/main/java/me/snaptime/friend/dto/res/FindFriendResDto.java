package me.snaptime.friend.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindFriendResDto(

        List<FriendInfo> friendInfoList,
        boolean hasNextPage
) {
    public static FindFriendResDto toDto(List<FriendInfo> friendInfoList, boolean hasNextPage){
        return FindFriendResDto.builder()
                .friendInfoList(friendInfoList)
                .hasNextPage(hasNextPage)
                .build();
    }
}
