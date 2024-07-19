package me.snaptime.snap.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindSnapPagingResDto(

    List<SnapPagingInfo> snapPagingInfos,
    boolean hasNextPage
) {
    public static FindSnapPagingResDto toDto(List<SnapPagingInfo> snapPagingInfos, boolean hasNextPage){

        return FindSnapPagingResDto.builder()
                .snapPagingInfos(snapPagingInfos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
