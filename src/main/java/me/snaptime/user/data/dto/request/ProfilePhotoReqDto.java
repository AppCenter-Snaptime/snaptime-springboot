package me.snaptime.user.data.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ProfilePhotoReqDto(
        @Schema(
                example = "형준프로필",
                description = "프로필 사진 이름을 입력해주세요"
        )
        @NotBlank(message = "프로필 사진 입력은 필수입니다.")
        String profilePhotoName,
        @Schema(
                example = "https://...",
                description = "프로필 경로를 입력해주세요"
        )
        @NotBlank(message = "프로필 사진 경로 입력은 필수입니다.")
        String profilePhotoPath
){}
