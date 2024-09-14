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
                example = "kang@gmail.com",
                description = "유저의 닉네임을 입력해주세요"
        )
        String nickName

){}
