package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.service.AlbumService;
import me.snaptime.snap.service.SnapService;
import me.snaptime.social.service.SnapTagService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping("/snap")
@RequiredArgsConstructor
@Tag(name = "[Snap] Snap API")
@Slf4j
public class SnapController {
    private final SnapService snapService;
    private final AlbumService albumService;
    private final SnapTagService snapTagService;

    @Operation(summary = "Snap 생성", description = "Empty Value를 보내지마세요")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponseDto<Long>> createSnap(
            final @RequestParam(value = "isPrivate") boolean isPrivate,
            final @RequestParam(value = "nonClassification") boolean nonClassification,
            final @RequestParam(value = "albumId", required = false) Long album_id,
            final @RequestParam(value = "tagUserLoginIds", required = false) List<String> tagUserLoginIds,
            final @ModelAttribute CreateSnapReqDto createSnapReqDto,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String uId = userDetails.getUsername();
        // 먼저 Snap 저장
        Long snapId = snapService.createSnap(createSnapReqDto, uId, isPrivate, tagUserLoginIds, nonClassification);

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

    @Operation(summary = "하루필름 크롤링 테스트 API", description = "하루필름 크롤링 테스트 API입니다.")
    @GetMapping("/test")
    public ResponseEntity<?> test(
            final @RequestParam("url") String url,
            final @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        Document doc = Jsoup.connect(url).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36").timeout(3000).get();
        Elements image = doc.select("div.main_cont > img");
        URL imageURL = new URL("http://haru9.mx2.co.kr" + image.attr("src"));
        URLConnection connection = imageURL.openConnection();
        InputStream inputStream = connection.getInputStream();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(
                inputStream.readAllBytes()
        );
    }

}
