package me.snaptime.reply.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddChildReplyReqDto(
        @Schema(
                example = "대댓글내용입니다.",
                description = "대댓글로 등록할 내용을 입력해주세요"
        )
        @NotBlank(message = "내용을 입력해주세요")
        String replyMessage,

        @Schema(
                example = "1",
                description = "등록할 대댓글의 부모댓글id를 입력해주세요"
        )
        @NotNull(message = "parentReplyId를 입력해주세요.")
        Long parentReplyId,

        @Schema(
                example = "홍길동",
                description = "태그할 유저의 loginId를 입력해주세요. 없으면 입력하지 않아도 됩니다."
        )
        String tagLoginId
) {
}
