package me.snaptime.user.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record UserPagingResDto(

        List<UserFindByNameResDto> userFindByNameResDtos,
        boolean hasNextPage
){
    public static UserPagingResDto toDto(List<UserFindByNameResDto> userFindByNameResDtos, boolean hasNextPage){
        return UserPagingResDto.builder()
                .userFindByNameResDtos(userFindByNameResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
