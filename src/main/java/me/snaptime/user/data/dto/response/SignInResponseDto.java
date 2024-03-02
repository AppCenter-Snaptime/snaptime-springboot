package me.snaptime.user.data.dto.response;


import lombok.Builder;

@Builder
public record SignInResponseDto(
        String accessToken
)
{}
