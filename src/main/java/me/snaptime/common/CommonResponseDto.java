package me.snaptime.common;

public record CommonResponseDto<T>(
        String msg,
        T result
) {
}
