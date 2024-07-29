package me.snaptime.friend.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FriendPagingResDto(

        List<FriendInfoResDto> friendInfoResDtos,
        boolean hasNextPage
) {
    public static FriendPagingResDto toDto(List<FriendInfoResDto> friendInfoResDtos, boolean hasNextPage){
        return FriendPagingResDto.builder()
                .friendInfoResDtos(friendInfoResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
