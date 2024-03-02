package me.snaptime.user.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.user.data.dto.response.ProfilePhotoResponseDto;
import me.snaptime.user.service.ProfilePhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Operation(summary = "프로필 사진 조회",description = "유저의 프로필 사진을 조회 합니다.")
    //@Parameter(name = "profilePhotoId", description = "조회 할 프로필 사진의 id")
    @GetMapping()
    public ResponseEntity<?> downloadProfileToFileSystem() {
        log.info("[downloadProfile] 유저의 프로필 사진을 조회합니다.");
        //SecurityContextHolder에서 현재 인증된 사용자의 정보를 담고 있는 Authentication 객체를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentcation객체가 가지고 있는 Principal 객체가 반환됩니다. 이 객체는 UserDetails 인터페이스를 구현한 사용자 정보 객체입니다.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //UserDetails객체에서 인증된 사용자의 loginId를 getUsername()메서드로 가져옵니다.
        String loginId = userDetails.getUsername(); // 로그인한 사용자의 아이디

        byte[] downloadProfile = profilePhotoService.downloadPhotoFromFileSystem(loginId);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(downloadProfile);
    }

    @Operation(summary = "프로필 사진 수정",description = "유저의 프로필 사진을 수정 합니다.")
    //@Parameter(name = "userId", description = "프로필 사진을 수정 할 유저 id")
    @PutMapping(consumes = MULTIPART_FORM_DATA, path = "/update")
    public ResponseEntity<?> updateProfileToFileSystem(@RequestParam("image") MultipartFile file) throws Exception {
        //SecurityContextHolder에서 현재 인증된 사용자의 정보를 담고 있는 Authentication 객체를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentcation객체가 가지고 있는 Principal 객체가 반환됩니다. 이 객체는 UserDetails 인터페이스를 구현한 사용자 정보 객체입니다.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //UserDetails객체에서 인증된 사용자의 loginId를 getUsername()메서드로 가져옵니다.
        String loginId = userDetails.getUsername(); // 로그인한 사용자의 아이디
        log.info("[updateProfile] 유저의 프로필 사진을 수정합니다. loginId : {}", loginId);

        ProfilePhotoResponseDto updateProfile = profilePhotoService.updatePhotoFromFileSystem(loginId, file);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "프로필 사진 수정을 성공적으로 완료하였습니다.",
                        updateProfile)
        );
    }

    //    @Operation(summary = "프로필 사진 업로드",description = "유저의 프로필 사진을 업로드 합니다.")
//    //@Parameter(name = "userId", description = "프로필 사진을 업로드 할 유저 id")
//    @PostMapping(consumes = MULTIPART_FORM_DATA)
//    public ResponseEntity<?> uploadProfileToFileSystem(@RequestParam("imageFile") MultipartFile imageFile) throws Exception {
//        //SecurityContextHolder에서 현재 인증된 사용자의 정보를 담고 있는 Authentication 객체를 가져온다.
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        //Authentcation객체가 가지고 있는 Principal 객체가 반환됩니다. 이 객체는 UserDetails 인터페이스를 구현한 사용자 정보 객체입니다.
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        //UserDetails객체에서 인증된 사용자의 loginId를 getUsername()메서드로 가져옵니다.
//        String loginId = userDetails.getUsername(); // 로그인한 사용자의 아이디
//
//        log.info("[uploadProfile] 유저의 프로필 사진을 업로드합니다. loginId : {}",loginId);
//
//        ProfilePhotoResponseDto uploadProfile = profilePhotoService.uploadPhotoToFileSystem(loginId, imageFile);
//        return ResponseEntity.status(HttpStatus.CREATED).body(
//                new CommonResponseDto<>(
//                        "프로필 사진 생성을 성공적으로 완료하였습니다.",
//                        uploadProfile)
//        );
//    }


//    @Operation(summary = "프로필 사진 삭제",description = "유저의 프로필 사진을 삭제 합니다.")
//    @Parameter(name = "userId", description = "프로필 사진을 삭제할 유저의 id")
//    @DeleteMapping("/{userId}")
//    public ResponseEntity<?> deleteProfileToFileSystem(@PathVariable("userId") Long userId)throws Exception{
//        log.info("[deleteProfile] 유저의 프로필 사진을 삭제합니다. userId : {}", userId);
//        profilePhotoService.deletePhotoFromFileSystem(userId);
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new CommonResponseDto<>(
//                        "프로필 사진 삭제를 성공적으로 완료하였습니다.",
//                        null)
//        );
//    }

}
