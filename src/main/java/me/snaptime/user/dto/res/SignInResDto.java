package me.snaptime.user.dto.res;


import lombok.Builder;

@Builder
public record SignInResDto(
        String accessToken
)
{}
