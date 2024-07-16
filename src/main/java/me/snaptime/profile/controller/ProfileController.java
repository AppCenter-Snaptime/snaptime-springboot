package me.snaptime.profile.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.profile.dto.res.AlbumSnapResDto;
import me.snaptime.profile.dto.res.ProfileCntResDto;
import me.snaptime.profile.dto.res.ProfileTagSnapResDto;
import me.snaptime.profile.dto.res.UserProfileResDto;
import me.snaptime.profile.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name ="[Profile] Profile API", description = "프로필-유저이름 조회, 앨범-스냅 조회, 태그스냅 조회, 스냅수-팔로잉수-팔로워수 조회")
@Slf4j
@RequestMapping("/profiles")
@RestController
@RequiredArgsConstructor
@Validated
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "유저 앨범, 스냅 조회", description = "유저의 앨범들과, 각 앨범의 스냅들을 조회합니다." +
            "<br> 자신의 프로필 조회 -> 앨범 당 private, public 관계 없이 최근 snap 2개 리턴" +
            "<br> 다른 사람의 프로필 조회 -> snap이 전부 private이거나 없는 경우 앨범 리턴 x 그리고 private 인 snap 리턴 x")
    @Parameter(name = "loginId", description = "앨범과 사진들을 가져오기 위한 loginId", required = true)
    @GetMapping("/album-snap")
    public ResponseEntity<CommonResponseDto<List<AlbumSnapResDto>>> getAlbumSnap(@AuthenticationPrincipal UserDetails principal,
                                                                                 @RequestParam("loginId")
                                                                                 @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String targetLoginId){
        String yourLoginId = principal.getUsername();
        List<AlbumSnapResDto> albumSnapResDtoList = profileService.getAlbumSnap(yourLoginId, targetLoginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 앨범과 스냅 조회를 성공적으로 완료하였습니다.",
                        albumSnapResDtoList
                ));
    }

    @Operation(summary = "유저 이름, 프로필 사진 조회", description = "유저의 이름과, 프로필 사진을 조회합니다." +
            "<br> 유저 번호, 유저 이름, 프로필 사진 url 리턴(토큰 없이 url 접근 가능)" +
            "<br> 토큰이 없어도 해당 Api 엔드포인트를 요청할 수 있습니다.")
    @Parameter(name = "loginId", description = "이름과 프로필 사진을 가져오기 위한 loginId", required = true)
    @GetMapping("/profile")
    public ResponseEntity<CommonResponseDto<UserProfileResDto>> getUserProfile(@RequestParam("loginId")
                                                                               @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String loginId){
        UserProfileResDto userProfileResDto = profileService.getUserProfile(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 이름과, 프로필 사진 조회를 성공적으로 완료하였습니다.",
                        userProfileResDto
                ));
    }

    @Operation(summary = "유저의 Snap 수, Follower 수,  Following 수 조회", description = "유저의 loginId로 유저의 snap 수, 팔로워 수, 팔로잉 수를 조회합니다.")
    @Parameter(name = "loginId", description = "팔로워와 팔로잉 수를 가져오기 위한 loginId", required = true)
    @GetMapping("/count")
    public ResponseEntity<CommonResponseDto<ProfileCntResDto>> getProfileCnt(@RequestParam("loginId")
                                                                             @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String loginId){
        ProfileCntResDto profileCntResDto = profileService.getUserProfileCnt(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 팔로워, 팔로잉 수 조회를 성공적으로 완료하였습니다.",
                        profileCntResDto
                ));
    }

    @Operation(summary = "유저의 태그된 snap 들 조회", description = "유저의 loginId로 유저가 태그된 snap 들을 조회합니다" +
            "<br> snap id 기준 내림차순으로 조회합니다.(최근 snap 이 제일 먼저 조회)")
    @Parameter(name = "loginId", description = "팔로워와 팔로잉 수를 가져오기 위한 loginId", required = true)
    @GetMapping("/tag-snap")
    public ResponseEntity<CommonResponseDto<List<ProfileTagSnapResDto>>> getTagSnap(@RequestParam("loginId")
                                                                                    @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String loginId){

        List<ProfileTagSnapResDto> profileTagSnapResDto = profileService.getTagSnap(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저가 태그된 Snap 들을 성공적으로 조회하였습니다.",
                        profileTagSnapResDto
                ));
    }
}
