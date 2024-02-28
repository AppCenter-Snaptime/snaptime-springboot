package me.snaptime.snap.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.service.SnapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/snap")
@RequiredArgsConstructor
@Tag(name = "[Snap] Snap API")
public class SnapController {
    private final SnapService snapService;

    @Operation(summary = "Snap 생성", description = "Empty Value를 보내지마세요")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponseDto<?>> createSnap(
            final @RequestParam("isPrivate") boolean isPrivate,
            final @ModelAttribute CreateSnapReqDto createSnapReqDto
    ) {
        snapService.createSnap(createSnapReqDto, "abcd", isPrivate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto<>(
                "스냅이 정상적으로 저장되었습니다.",
                null
        ));
    }

    @Operation(summary = "Snap 찾기", description = "Snap 한 개 가져오기")
    @Parameter(name = "id", description = "찾을 Snap의 id")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto<FindSnapResDto>> findSnap(final @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "스냅이 정상적으로 불러와졌습니다.",
                        snapService.findSnap(id)
                )
        );
    }

    @PostMapping("/visibility")
    public ResponseEntity<CommonResponseDto<Void>> changeVisibility(
            final @RequestParam("snapId") Long snapId,
            final @RequestParam("isPrivate") boolean isPrivate
    ) {
        snapService.changeVisibility(snapId, "abcd", isPrivate);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "게시글의 상태가 성공적으로 변경되었습니다.",
                        null
                )
        );
    }

}
