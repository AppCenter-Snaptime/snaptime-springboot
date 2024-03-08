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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@Tag(name = "[Photo] Photo API")
public class PhotoController {
    private final SnapService snapService;

    @Operation(summary = "Photo 조회", description = "")
    @Parameters({
            @Parameter(name = "fileName", description = "찾을 사진의 이름을 입력해주세요"),
    })
    @GetMapping
    public ResponseEntity<byte[]> findPhoto(
            final @RequestParam("fileName") String fileName,
            final @RequestParam("isEncrypted") boolean isEncrypted
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String uId = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(
                snapService.downloadPhotoFromFileSystem(fileName, uId, isEncrypted)
        );
    }
}
