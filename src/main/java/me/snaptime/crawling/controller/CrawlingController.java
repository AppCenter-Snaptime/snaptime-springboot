package me.snaptime.crawling.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.crawling.service.CrawlingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawler")
@RequiredArgsConstructor
@Tag(name = "[Crawling] Crawling API")
@Slf4j
public class CrawlingController {
    private final CrawlingService crawlingService;

    @Operation(summary = "하루필름 크롤링 API", description = "하루필름 크롤링 API입니다.")
    @Parameter(name = "url", description = "QR Code의 URL")
    @GetMapping("/harufilm")
    public ResponseEntity<?> harufilm(final @RequestParam("url") String url) {
        byte[] image = crawlingService.getImage("harufilm", url);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(image);
    }

    @Operation(summary = "1Percent 크롤링 API", description = "1Percent 크롤링 API입니다.")
    @Parameter(name = "url", description = "QR Code의 URL")
    @GetMapping("/onepercent")
    ResponseEntity<?> onePercent(final @RequestParam String url) {
        byte[] image = crawlingService.getImage("onepercent", url);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(image);
    }

    @Operation(summary = "Studio808 크롤링 API", description = "Studio808 크롤링 API입니다.")
    @Parameter(name = "url", description = "QR Code의 URL")
    @GetMapping("/studio808")
    ResponseEntity<?> studio808(final @RequestParam String url) {
        byte[] image = crawlingService.getImage("studio808", url);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(image);
    }

    @Operation(summary = "PhotoSignature 크롤링 API", description = "PhotoSignature 크롤링 API입니다.")
    @Parameter(name = "url", description = "QR Code의 URL")
    @GetMapping("/photosignature")
    ResponseEntity<?> photosignature(final @RequestParam String url) {
        byte[] image = crawlingService.getImage("photosignature", url);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(image);
    }


}
