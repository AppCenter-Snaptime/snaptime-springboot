package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.snap.service.SnapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@Tag(name = "[Photo] Photo API")
public class PhotoController {
    private final SnapService snapService;

    @Operation(summary = "Photo 조회")
    @Parameters({
            @Parameter(name = "fileName", description = "찾을 사진의 이름을 입력해주세요"),
    })
    @GetMapping
    public ResponseEntity<byte[]> findPhoto(
            final @RequestParam("fileName") String fileName,
            final @RequestParam("isEncrypted") boolean isEncrypted,
            final @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(
                snapService.downloadPhotoFromFileSystem(fileName, userEmail, isEncrypted)
        );
    }
}
