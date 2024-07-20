package me.snaptime.snap.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindSnapPagingResDto(

    List<SnapResInfo> snapResInfoList,
    boolean hasNextPage
) {
    public static FindSnapPagingResDto toDto(List<SnapResInfo> snapResInfoList, boolean hasNextPage){

        return FindSnapPagingResDto.builder()
                .snapResInfoList(snapResInfoList)
                .hasNextPage(hasNextPage)
                .build();
    }
}
