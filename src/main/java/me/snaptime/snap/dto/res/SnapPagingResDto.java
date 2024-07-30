package me.snaptime.snap.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record SnapPagingResDto(

    List<SnapDetailInfoResDto> snapDetailInfoResDtos,
    boolean hasNextPage
) {
    public static SnapPagingResDto toDto(List<SnapDetailInfoResDto> snapDetailInfoResDtos, boolean hasNextPage){

        return SnapPagingResDto.builder()
                .snapDetailInfoResDtos(snapDetailInfoResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
