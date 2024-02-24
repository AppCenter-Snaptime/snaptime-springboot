package me.snaptime.user.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.user.data.dto.response.ProfilePhotoResponseDto;
import me.snaptime.user.service.ProfilePhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@Tag(name ="[ProfilePhoto] ProfilePhoto API", description = "프로필 사진 생성, 프로필 사진 조회, 프로필 사진 수정, 프로필 사진 삭제")
@Slf4j
@RestController
@RequestMapping("/profilePhotos")
@RequiredArgsConstructor
public class ProfilePhotoController {

    private final ProfilePhotoService profilePhotoService;

    @Operation(summary = "프로필 사진 업로드",description = "유저의 프로필 사진을 업로드 합니다.")
    @Parameter(name = "userId", description = "프로필 사진을 업로드 할 유저 id")
    @PostMapping(consumes = MULTIPART_FORM_DATA)
    public ResponseEntity<?> uploadProfileToFileSystem(@RequestParam("userId") Long userId, @RequestParam("imageFile") MultipartFile imageFile) throws Exception {
        log.info("[uploadProfile] 유저의 프로필 사진을 업로드합니다. userId : {}",userId);

        ProfilePhotoResponseDto uploadProfile = profilePhotoService.uploadPhotoToFileSystem(userId, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CommonResponseDto<>(
                        "프로필 사진 생성을 성공적으로 완료하였습니다.",
                        uploadProfile)
        );
    }

    @Operation(summary = "프로필 사진 조회",description = "유저의 프로필 사진을 조회 합니다.")
    @Parameter(name = "profilePhotoId",description = "조회 할 프로필 사진의 id")
    @GetMapping()
    public ResponseEntity<?> downloadProfileToFileSystem(@PathVariable("profilePhotoId") Long profilePhotoId) {
        log.info("[downloadProfile] 유저의 프로필 사진을 조회합니다.");

        byte[] downloadProfile = profilePhotoService.downloadPhotoFromFileSystem(profilePhotoId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(downloadProfile);
    }

    @Operation(summary = "프로필 사진 수정",description = "유저의 프로필 사진을 수정 합니다.")
    @Parameter(name = "userId", description = "프로필 사진을 수정 할 유저 id")
    @PutMapping(consumes = MULTIPART_FORM_DATA, path = "/update")
    public ResponseEntity<?> updateProfileToFileSystem(@RequestParam("userId") Long userId, @RequestParam("image") MultipartFile file) throws Exception {
        log.info("[updateProfile] 유저의 프로필 사진을 수정합니다. userId : {}",userId);
        ProfilePhotoResponseDto updateProfile = profilePhotoService.updatePhotoFromFileSystem(userId, file);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "프로필 사진 수정을 성공적으로 완료하였습니다.",
                        updateProfile)
        );
    }

    @Operation(summary = "프로필 사진 삭제",description = "유저의 프로필 사진을 삭제 합니다.")
    @Parameter(name = "userId", description = "프로필 사진을 삭제할 유저의 id")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteProfileToFileSystem(@PathVariable("userId") Long userId)throws Exception{
        log.info("[deleteProfile] 유저의 프로필 사진을 삭제합니다. userId : {}", userId);
        profilePhotoService.deletePhotoFromFileSystem(userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "프로필 사진 삭제를 성공적으로 완료하였습니다.",
                        null)
        );
    }



}
