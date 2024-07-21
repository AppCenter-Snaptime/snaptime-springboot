package me.snaptime.reply.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindParentReplyResDto(

        List<ParentReplyInfo> parentReplyInfos,
        boolean hasNextPage
) {
    public static FindParentReplyResDto toDto(List<ParentReplyInfo> parentReplyInfos, boolean hasNextPage){
        return FindParentReplyResDto.builder()
                .parentReplyInfos(parentReplyInfos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
