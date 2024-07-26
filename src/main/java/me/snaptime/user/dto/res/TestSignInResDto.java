package me.snaptime.user.dto.res;

import lombok.Builder;

@Builder
public record TestSignInResDto(
        String testAccessToken,
        String testRefreshToken
) {
}
