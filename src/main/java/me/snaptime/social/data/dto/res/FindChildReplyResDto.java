package me.snaptime.social.data.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindChildReplyResDto(

        List<ChildReplyInfo> childReplyInfoList,
        boolean hasNextPage

) {
    public static FindChildReplyResDto toDto(List<ChildReplyInfo> childReplyInfoList, boolean hasNextPage){

        return FindChildReplyResDto.builder()
                .childReplyInfoList(childReplyInfoList)
                .hasNextPage(hasNextPage)
                .build();
    }
}
