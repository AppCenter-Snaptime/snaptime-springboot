package me.snaptime.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserReqDto(
        @Schema(
                example = "홍길순",
                description = "유저의 이름을 입력해주세요"
        )
        @NotBlank(message = "유저 이름 입력은 필수입니다.")
        String name,
        @Schema(
                example = "kang4746@gmail.com",
                description = "유저의 이메일을 입력해주세요"
        )
        @NotBlank(message = "유저 이메일 입력은 필수입니다.")
        String email,
        @Schema(
                example = "password",
                description = "유저의 비밀번호를 입력해주세요"
        )
        @NotBlank(message = "유저 비밀번호 입력은 필수입니다.")
        String password
){
        static public String makeNickName(String email){
                String[]emailParts = email.split("@");
                return emailParts[0] + emailParts[1].charAt(0);
        }
}
