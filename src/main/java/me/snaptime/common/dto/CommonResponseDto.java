package me.snaptime.common.dto;

public record CommonResponseDto<T>(
        String msg,
        T result
) {
}
