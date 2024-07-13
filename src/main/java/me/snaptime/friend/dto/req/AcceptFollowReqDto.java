package me.snaptime.friend.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AcceptFollowReqDto(

        @Schema(
                example = "홍길동",
                description = "receiver(수락받는 사람)의 LoginId를 입력해주세요."
        )
        @NotBlank(message = "유저의 LoginId를 입력해주세요.")
        String receiverLoginId,
        @Schema(
                example = "true",
                description = "팔로우 수락여부를 보내주세요."
        )
        @NotNull(message = "수락여부를 보내주세요.")
        Boolean isAccept
) {
}
