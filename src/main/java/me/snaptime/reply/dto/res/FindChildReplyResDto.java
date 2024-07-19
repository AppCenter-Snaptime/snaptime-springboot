package me.snaptime.reply.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindChildReplyResDto(

        List<ChildReplyInfo> childReplyInfos,
        boolean hasNextPage

) {
    public static FindChildReplyResDto toDto(List<ChildReplyInfo> childReplyInfos, boolean hasNextPage){

        return FindChildReplyResDto.builder()
                .childReplyInfos(childReplyInfos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
