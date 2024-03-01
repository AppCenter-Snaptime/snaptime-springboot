package me.snaptime.snap.data.controller;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.snap.service.EncryptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/encryption")
@RequiredArgsConstructor
public class EncryptionController {
    private final EncryptionService encryptionService;

    @GetMapping
    public ResponseEntity<CommonResponseDto<String>> getEncryptionKey() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>("사용자의 암호화키입니다. ", encryptionService.getEncryptionKey("abcd"))
        );
    }
}
