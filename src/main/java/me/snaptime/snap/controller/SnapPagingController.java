package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.snap.service.impl.SnapPagingServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/snaps")
@RequiredArgsConstructor
@Tag(name = "[Social] Snap API")
public class SnapPagingController {

    private final SnapPagingServiceImpl snapPagingService;

    @Operation(summary = "Snap 조회", description = "커뮤니티에서 Snap을 10개씩 페이징조회합니다.")
    @Parameter(name = "pageNum", description = "Snap페이지 번호를 보내주세요")
    @GetMapping("/community/{pageNum}")
    public ResponseEntity findSnapPaging(
            @AuthenticationPrincipal final UserDetails userDetails,
            @PathVariable final Long pageNum) {

        String loginId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new CommonResponseDto("스냅 페이징조회가 완료되었습니다.", snapPagingService.findSnapPaging(loginId,pageNum)));
    }

}
