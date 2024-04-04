package me.snaptime.social.data.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AcceptFollowReqDto(

        @Schema(
                example = "홍길동",
                description = "팔로우 수락을 받을 유저의 이름을 입력해주세요"
        )
        @NotBlank(message = "유저의 LoginId를 입력해주세요.")
        String fromUserLoginId,
        @Schema(
                example = "true",
                description = "팔로우 수락여부를 보내주세요."
        )
        @NotNull(message = "수락여부를 보내주세요.")
        Boolean isAccept
) {
}
