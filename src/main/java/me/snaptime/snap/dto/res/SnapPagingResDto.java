package me.snaptime.snap.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record SnapPagingResDto(

    List<SnapDetailInfoDto> snapDetailInfoDtos,
    boolean hasNextPage
) {
    public static SnapPagingResDto toDto(List<SnapDetailInfoDto> snapDetailInfoDtos, boolean hasNextPage){

        return SnapPagingResDto.builder()
                .snapDetailInfoDtos(snapDetailInfoDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
