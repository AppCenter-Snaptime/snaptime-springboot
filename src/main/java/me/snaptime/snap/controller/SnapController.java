package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.snap.component.crawling.CrawlingComponent;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.req.ModifySnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.service.SnapService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/snap")
@RequiredArgsConstructor
@Tag(name = "[Snap] Snap API")
@Slf4j
public class SnapController {
    private final SnapService snapService;
    private final CrawlingComponent crawlingComponent;

    @Operation(summary = "Snap 생성", description = "Empty Value를 보내지마세요")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponseDto<Long>> createSnap(
            final @RequestParam(value = "isPrivate") boolean isPrivate,
            final @RequestParam(value = "albumId", required = false) Long album_id,
            final @RequestParam(value = "tagUserLoginIds", required = false) List<String> tagUserLoginIds,
            final @ModelAttribute CreateSnapReqDto createSnapReqDto,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        Long snapId = snapService.createSnap(createSnapReqDto, uId, isPrivate, tagUserLoginIds, album_id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto<>(
                "스냅이 정상적으로 저장되었습니다.",
                        snapId
        ));
    }

    @Operation(summary = "Snap 찾기", description = "Snap 한 개 가져오기")
    @Parameter(name = "id", description = "찾을 Snap의 id")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto<FindSnapResDto>> findSnap(
            final @PathVariable("id") Long id,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "스냅이 정상적으로 불러와졌습니다.",
                        snapService.findSnap(id, uId)
                )
        );
    }

    @Operation(summary = "Snap 수정", description = "Snap 수정하기")
    @Parameters({
            @Parameter(name = "isPrivate", description = "변경 할 상태"),
            @Parameter(name = "snapId", description = "변경 할 Snap의 ID"),
            @Parameter(name = "tagUserLoginIds", description = "변경 할 TagID")
    })
    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponseDto<Long>> modifySnap(
            final @ModelAttribute ModifySnapReqDto modifySnapReqDto,
            final @RequestParam boolean isPrivate,
            final @RequestParam Long snapId,
            final @RequestParam(required = false) List<String> tagUserLoginIds,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "스냅이 정상적으로 수정되었습니다.",
                        snapService.modifySnap(snapId, modifySnapReqDto, uId, tagUserLoginIds, isPrivate)
                )
        );
    }

    @Operation(summary = "Snap 공개상태 변경", description = "Snap 공개 상태를 변경합니다.")
    @PostMapping("/visibility")
    @Parameters({
            @Parameter(name = "snapId", description = "변경할 Snap id"),
            @Parameter(name = "isPrivate", description = "변경할 상태")
    })
    public ResponseEntity<CommonResponseDto<Void>> changeVisibility(
            final @RequestParam("snapId") Long snapId,
            final @RequestParam("isPrivate") boolean isPrivate,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        snapService.changeVisibility(snapId, uId, isPrivate);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "게시글의 상태가 성공적으로 변경되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "Snap 앨범 위치 변경", description = "Snap의 앨범 위치를 변경합니다.")
    @PostMapping("/album")
    @Parameters({
            @Parameter(name = "snapId", description = "위치를 변경할 Snap Id"),
            @Parameter(name = "albumId", description = "이동할 Album Id")
    })
    ResponseEntity<CommonResponseDto<Void>> relocateSnap(
            final @RequestParam Long snapId,
            final @RequestParam Long albumId,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        snapService.relocateSnap(snapId, albumId, uId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "스냅의 위치가 " + albumId + "번 앨범으로 변경되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "Snap 삭제", description = "스냅을 삭제합니다.")
    @DeleteMapping
    @Parameter(name = "snapId", description = "삭제할 Snap ID")
    ResponseEntity<CommonResponseDto<Void>> deleteSnap(
            final @RequestParam Long snapId,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        snapService.deleteSnap(snapId, uId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        snapId + "번 스냅이 삭제되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "하루필름 크롤링 테스트 API", description = "하루필름 크롤링 테스트 API입니다.")
    @GetMapping("/test")
    public ResponseEntity<?> test(final @RequestParam("url") String url) {
        byte[] image = crawlingComponent.getImageFromHaruFilm(url);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(image);
    }

    @Operation(summary = "1Percent 크롤링 테스트 API", description = "1")
    @GetMapping("/test2")
    ResponseEntity<?> test2(final @RequestParam String url) {
        byte[] image = crawlingComponent.getImageFromOnePercent(url);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(image);
    }
}
