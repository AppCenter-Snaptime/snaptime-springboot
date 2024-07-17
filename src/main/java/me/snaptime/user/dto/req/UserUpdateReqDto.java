package me.snaptime.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserUpdateReqDto(
        @Schema(
                example = "홍길순",
                description = "유저의 이름을 입력해주세요"
        )
        String name,
        @Schema(
                example = "strong@gmail.com",
                description = "유저의 이메일을 입력해주세요"
        )
        String email,
        @Schema(
                example = "1999-10-29",
                description = "유저의 생년월일을 입력해주세요"
        )
        String birthDay

){}
