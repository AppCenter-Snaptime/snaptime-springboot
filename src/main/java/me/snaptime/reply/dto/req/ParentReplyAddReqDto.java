package me.snaptime.reply.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParentReplyAddReqDto(
        @Schema(
                example = "댓글내용입니다.",
                description = "댓글로 등록할 내용을 입력해주세요"
        )
        @NotBlank(message = "내용을 입력해주세요")
        String replyMessage,

        @Schema(
                example = "1",
                description = "댓글을 등록할 snapdId를 입력해주세요"
        )
        @NotNull(message = "snapId를 입력해주세요.")
        Long snapId

) {
}