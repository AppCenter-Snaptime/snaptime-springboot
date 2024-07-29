package me.snaptime.reply.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record ParentReplyPagingResDto(

        List<ParentReplyInfoResDto> parentReplyInfoResDtos,
        boolean hasNextPage
) {
    public static ParentReplyPagingResDto toDto(List<ParentReplyInfoResDto> parentReplyInfoResDtos, boolean hasNextPage){
        return ParentReplyPagingResDto.builder()
                .parentReplyInfoResDtos(parentReplyInfoResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
