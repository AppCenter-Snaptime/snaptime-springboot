package me.snaptime.snapLike.dto.res;

import lombok.Builder;

@Builder
public record SnapLikeResDto(
        String message,
        Long likeCnt
) {

    public static SnapLikeResDto toDto(String message, Long likeCnt){

        return SnapLikeResDto.builder()
                .message(message)
                .likeCnt(likeCnt)
                .build();
    }
}
