package me.snaptime.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserUpdateDto(
        @Schema(
                example = "홍길순",
                description = "유저의 이름을 입력해주세요"
        )
        @NotBlank(message = "유저 이름 입력은 필수입니다.")
        String name,
        @Schema(
                example = "strong@gmail.com",
                description = "유저의 이메일을 입력해주세요"
        )
        @NotBlank(message = "유저 이메일 입력은 필수입니다.")
        String email,
        @Schema(
                example = "1999-10-29",
                description = "유저의 생년월일을 입력해주세요"
        )
        @NotBlank(message = "유저 생년월일 입력은 필수입니다.")
        String birthDay

){}
