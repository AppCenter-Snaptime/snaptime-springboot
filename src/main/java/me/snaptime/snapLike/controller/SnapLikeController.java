package me.snaptime.snapLike.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.snapLike.dto.res.SnapLikeResDto;
import me.snaptime.snapLike.service.SnapLikeService;
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
    @Parameter(name = "snapId", description = "snap아이디를 입력해주세요.")
    public ResponseEntity<CommonResponseDto<SnapLikeResDto>> toggleSnapLike(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final Long snapId){

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("좋아요 토글 성공",snapLikeService.toggleSnapLike(userDetails.getUsername(), snapId)));
    }
}
