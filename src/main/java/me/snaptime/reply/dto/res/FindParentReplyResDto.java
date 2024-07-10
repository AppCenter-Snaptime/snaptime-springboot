package me.snaptime.reply.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindParentReplyResDto(

        List<ParentReplyInfo> parentReplyInfoList,
        boolean hasNextPage
) {
    public static FindParentReplyResDto toDto(List<ParentReplyInfo> parentReplyInfoList, boolean hasNextPage){
        return FindParentReplyResDto.builder()
                .parentReplyInfoList(parentReplyInfoList)
                .hasNextPage(hasNextPage)
                .build();
    }
}
