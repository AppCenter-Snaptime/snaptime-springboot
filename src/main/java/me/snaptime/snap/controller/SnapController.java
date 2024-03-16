package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.service.SnapService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@RestController
@RequestMapping("/snap")
@RequiredArgsConstructor
@Tag(name = "[Snap] Snap API")
@Slf4j
public class SnapController {
    private final SnapService snapService;

    @Operation(summary = "Snap 생성", description = "Empty Value를 보내지마세요")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponseDto<Void>> createSnap(
            final @RequestParam("isPrivate") boolean isPrivate,
            final @ModelAttribute CreateSnapReqDto createSnapReqDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String uId = userDetails.getUsername();
        snapService.createSnap(createSnapReqDto, uId, isPrivate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponseDto<>(
                "스냅이 정상적으로 저장되었습니다.",
                null
        ));
    }

    @Operation(summary = "Snap 찾기", description = "Snap 한 개 가져오기")
    @Parameter(name = "id", description = "찾을 Snap의 id")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto<FindSnapResDto>> findSnap(
            final @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "스냅이 정상적으로 불러와졌습니다.",
                        snapService.findSnap(id)
                )
        );
    }

    @PostMapping("/visibility")
    @Parameters({
            @Parameter(name = "snapId", description = "변경할 Snap id"),
            @Parameter(name = "isPrivate", description = "변경할 상태")
    })
    public ResponseEntity<CommonResponseDto<Void>> changeVisibility(
            final @RequestParam("snapId") Long snapId,
            final @RequestParam("isPrivate") boolean isPrivate
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String uId = userDetails.getUsername();
        snapService.changeVisibility(snapId, uId, isPrivate);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "게시글의 상태가 성공적으로 변경되었습니다.",
                        null
                )
        );
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(
            final @RequestParam("url") String url
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
