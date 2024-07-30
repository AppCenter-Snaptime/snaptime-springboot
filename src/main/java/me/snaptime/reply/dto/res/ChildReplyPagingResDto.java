package me.snaptime.reply.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record ChildReplyPagingResDto(

        List<ChildReplyInfoResDto> childReplyInfoResDtos,
        boolean hasNextPage

) {
    public static ChildReplyPagingResDto toDto(List<ChildReplyInfoResDto> childReplyInfoResDtos, boolean hasNextPage){

        return ChildReplyPagingResDto.builder()
                .childReplyInfoResDtos(childReplyInfoResDtos)
                .hasNextPage(hasNextPage)
                .build();
    }
}
