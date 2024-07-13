package me.snaptime.profilePhoto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.profilePhoto.dto.res.ProfilePhotoResDto;
import me.snaptime.profilePhoto.service.ProfilePhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@Tag(name ="[ProfilePhoto] ProfilePhoto API", description = "프로필 사진 생성, 프로필 사진 조회, 프로필 사진 수정, 프로필 사진 삭제")
@Slf4j
@RestController
@RequestMapping("/profile_photos")
@RequiredArgsConstructor
public class ProfilePhotoController {

    private final ProfilePhotoService profilePhotoService;

    @Operation(summary = "프로필 사진 조회",description = "유저의 번호로 유저의 프로필 사진을 조회 합니다.")
    @Parameter(name = "profile_photo_id", description = "조회 할 프로필 사진의 id")
    @GetMapping("/{profile_photo_id}")
    public ResponseEntity<?> downloadProfileToFileSystem(@PathVariable("profile_photo_id") Long profilePhotoId) {
        log.info("[downloadProfile] 유저의 프로필 사진을 조회합니다. profilePhotoId : {}",profilePhotoId);

        byte[] downloadProfile = profilePhotoService.downloadPhotoFromFileSystem(profilePhotoId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(downloadProfile);
    }

    @Operation(summary = "프로필 사진 수정",description = "유저의 프로필 사진을 수정 합니다." +
            "<br> 유저의 Token 으로 유저를 구분짓습니다.")
    @PutMapping(consumes = MULTIPART_FORM_DATA)
    public ResponseEntity<?> updateProfileToFileSystem(@AuthenticationPrincipal UserDetails principal,
                                                       @RequestParam MultipartFile file) throws Exception {
        String loginId = principal.getUsername();
        log.info("[updateProfile] 유저의 프로필 사진을 수정합니다. writerLoginId : {}", loginId);
        ProfilePhotoResDto updateProfile = profilePhotoService.updatePhotoFromFileSystem(loginId, file);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "프로필 사진 수정을 성공적으로 완료하였습니다.",
                        updateProfile)
        );
    }
}
