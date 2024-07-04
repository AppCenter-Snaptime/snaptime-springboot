package me.snaptime.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.social.service.SnapLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "[Social] Like API")
public class SnapLikeController {

    private final SnapLikeService snapLikeService;

    @PostMapping("/likes/toggle")
    @Operation(summary = "스냅 좋아요 토글", description = "좋아요 토글할 snapId를 보내주세요<br>" +
                                    "특정 유저가 특정스냅에 좋아요를 눌렀다면 좋아요가 취소됩니다.<br>"+
                                    "좋아요를 누르지 않았다면 좋아요가 추가됩니다.")
    public ResponseEntity<CommonResponseDto<Void>> toggleSnapLike(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final Long snapId){

        String message = snapLikeService.toggleSnapLike(userDetails.getUsername(), snapId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto(message,null));
    }
}
