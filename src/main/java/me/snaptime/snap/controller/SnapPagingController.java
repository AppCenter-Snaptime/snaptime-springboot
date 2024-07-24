package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.snap.dto.res.SnapPagingResDto;
import me.snaptime.snap.service.SnapPagingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "[Social] Snap API")
public class SnapPagingController {

    private final SnapPagingService snapPagingService;

    @Operation(summary = "Snap 조회", description = "커뮤니티에서 Snap을 10개씩 페이징조회합니다.")
    @Parameter(name = "pageNum", description = "Snap페이지 번호를 보내주세요")
    @GetMapping("/community/snaps/{pageNum}")
    public ResponseEntity<CommonResponseDto<SnapPagingResDto>> findSnapPage(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long pageNum) {

        String reqLoginId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(
                "스냅 페이징조회가 완료되었습니다.", snapPagingService.findSnapPage(reqLoginId,pageNum)));
    }

}
