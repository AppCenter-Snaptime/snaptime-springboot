package me.snaptime.snap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.jwt.JwtProvider;
import me.snaptime.snap.component.EncryptionComponent;
import me.snaptime.snap.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@Tag(name = "[Photo] Photo API")
public class PhotoController {
    private final PhotoService photoService;
    private final JwtProvider jwtProvider;
    private final EncryptionComponent encryptionComponent;

    @Operation(summary = "Photo 조회", description = "조회할 Photo의 id를 입력해주세요")
    @Parameters({
            @Parameter(name = "id", description = "찾을 사진의 id를 입력해주세요"),
    })
    @GetMapping
    public ResponseEntity<byte[]> findPhoto(
            final @RequestParam("id") Long id,
            final HttpServletRequest request
    ) {
        String token = jwtProvider.resolveToken(request);
        String uId = jwtProvider.getUsername(token);
        SecretKey secretKey = encryptionComponent.getSecretKey(uId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(
                photoService.downloadPhotoFromFileSystem(id, secretKey)
        );
    }
}
