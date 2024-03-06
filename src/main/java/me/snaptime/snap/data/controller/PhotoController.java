package me.snaptime.snap.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.snaptime.snap.component.EncryptionComponent;
import me.snaptime.snap.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@Tag(name = "[Photo] Photo API")
public class PhotoController {
    private final PhotoService photoService;
    private final EncryptionComponent encryptionComponent;

    @Operation(summary = "Photo 조회", description = "조회할 Photo의 id를 입력해주세요")
    @Parameters({
            @Parameter(name = "id", description = "찾을 사진의 id를 입력해주세요"),
    })
    @GetMapping
    public ResponseEntity<byte[]> findPhoto(
            final @RequestParam("id") Long id
    ) {
        String uId = "abcd";
        SecretKey secretKey = encryptionComponent.getSecretKey(uId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(
                photoService.downloadPhotoFromFileSystem(id, secretKey)
        );
    }
}
