package me.snaptime.snap.data.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindSnapPagingResDto(

    List<SnapPagingInfo> snapPagingInfoList,
    boolean hasNextPage
) {
    public static FindSnapPagingResDto toDto(List<SnapPagingInfo> snapPagingInfoList, boolean hasNextPage){
        return FindSnapPagingResDto.builder()
                .snapPagingInfoList(snapPagingInfoList)
                .hasNextPage(hasNextPage)
                .build();
    }
}
