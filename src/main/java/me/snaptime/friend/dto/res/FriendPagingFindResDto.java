package me.snaptime.friend.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FriendPagingFindResDto(

        List<FriendInfoResDto> friendInfoResDtos,
        boolean hasNextPage
) {
    public static FriendPagingFindResDto toDto(List<FriendInfoResDto> friendInfoResDtos, boolean hasNextPage){
        return FriendPagingFindResDto.builder()
                .friendInfoResDtos(friendInfoResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
