package me.snaptime.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignInReqDto(

        @NotBlank(message = "유저 email 입력은 필수입니다.")
        @Schema(
                example = "kang4746@gmail.com",
                description = "유저의 email를 입력해주세요"
        )
        String email,

        @NotBlank(message = "유저 password 입력은 필수입니다.")
        @Schema(
                example = "password",
                description = "유저의 password를 입력해주세요"
        )
        String password
)
{}
