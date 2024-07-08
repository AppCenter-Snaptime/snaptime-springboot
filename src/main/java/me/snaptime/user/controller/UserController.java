package me.snaptime.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.user.data.dto.request.SignInReqDto;
import me.snaptime.user.data.dto.request.UserReqDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.SignInResDto;
import me.snaptime.user.data.dto.response.UserResDto;
import me.snaptime.user.data.dto.response.userprofile.AlbumSnapResDto;
import me.snaptime.user.data.dto.response.userprofile.ProfileCntResDto;
import me.snaptime.user.data.dto.response.userprofile.ProfileTagSnapResDto;
import me.snaptime.user.data.dto.response.userprofile.UserProfileResDto;
import me.snaptime.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name ="[User] User API", description = "유저 생성,유저 조회, 유저 수정, 유저 삭제")
@Slf4j
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회",description = "유저 번호로 유저 정보를 조회합니다. ")
    @GetMapping()
    public ResponseEntity<CommonResponseDto<UserResDto>> getUser(@AuthenticationPrincipal UserDetails principal){
        UserResDto userResDto = userService.getUser(principal.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 정보가 성공적으로 조회되었습니다.",
                        userResDto));
    }


    @Operation(summary = "유저 정보 수정",description = "해당 유저의 정보를 수정합니다. " +
            "<br> 유저 loginId 수정 이후에는, Token의 loginId 정보와 현재 유저의 loginId가 다르므로," +
            "<br> Token을 버리고 재 login을 유도해야 합니다.")
    @PutMapping()
    public ResponseEntity<CommonResponseDto<UserResDto>> changeUser(@AuthenticationPrincipal UserDetails principal,
                                                                    @Valid @RequestBody UserUpdateDto userUpdateDto){
        UserResDto userResDto = userService.updateUser(principal.getUsername(), userUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 정보 수정이 성공적으로 완료되었습니다.",
                        userResDto));
    }
    @Operation(summary = "유저 비밀번호 수정",description = "해당 유저의 비밀번호를 수정합니다.")
    @PutMapping("/password")
    public ResponseEntity<CommonResponseDto<Void>> changeUser(@AuthenticationPrincipal UserDetails principal,
                                                              @RequestParam("password")
                                                              @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String password) {
        userService.updatePassword(principal.getUsername(), password);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 비밀번호 수정이 성공적으로 완료되었습니다.",
                        null));
    }


    @Operation(summary = "유저 삭제",description = "유저 번호로 유저를 삭제합니다.")
    @DeleteMapping()
    public ResponseEntity<CommonResponseDto<Void>> deleteUser(@AuthenticationPrincipal UserDetails principal){
        userService.deleteUser(principal.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 삭제가 성공적으로 완료되었습니다.",
                        null));
    }

    @Operation(summary = "회원가입", description = "회원 가입 할 유저의 정보를 입력합니다. " +
            "<br> 회원가입이 완료되면 자동으로 유저의 기본 profile 사진이 등록됩니다." +
            "<br> 이후에 유저의 Token 을 통해 profile 사진을 수정할 수 있습니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<CommonResponseDto<UserResDto>> signUp(@Valid @RequestBody UserReqDto userReqDto){
        UserResDto userResDto = userService.signUp(userReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CommonResponseDto<>(
                        "유저 회원가입을 성공적으로 완료하였습니다.",
                        userResDto));
    }

    @Operation(summary = "로그인", description = "회원 가입 한 유저의 loginId와 password를 입력합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<CommonResponseDto<SignInResDto>> signIn(@Valid @RequestBody SignInReqDto signInReqDto){
        SignInResDto signInResponseDto = userService.signIn(signInReqDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 로그인을 성공적으로 완료하였습니다.",
                        signInResponseDto));
    }

    @Operation(summary = "유저 앨범, 스냅 조회", description = "유저의 앨범들과, 각 앨범의 스냅들을 조회합니다." +
            "<br> 자신의 프로필 조회 -> 앨범 당 private, public 관계 없이 최근 snap 2개 리턴" +
            "<br> 다른 사람의 프로필 조회 -> snap이 전부 private이거나 없는 경우 앨범 리턴 x 그리고 private 인 snap 리턴 x")
    @Parameter(name = "login_id", description = "앨범과 사진들을 가져오기 위한 loginId", required = true)
    @GetMapping("/album_snap")
    public ResponseEntity<CommonResponseDto<List<AlbumSnapResDto>>> getAlbumSnap(@AuthenticationPrincipal UserDetails principal,
                                                                                 @RequestParam("login_id")
                                                                                 @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String targetLoginId){
        String yourLoginId = principal.getUsername();
        List<AlbumSnapResDto> albumSnapResDtoList = userService.getAlbumSnap(yourLoginId, targetLoginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 앨범과 스냅 조회를 성공적으로 완료하였습니다.",
                        albumSnapResDtoList
                ));
    }

    @Operation(summary = "유저 이름, 프로필 사진 조회", description = "유저의 이름과, 프로필 사진을 조회합니다." +
            "<br> 유저 번호, 유저 이름, 프로필 사진 url 리턴(토큰 없이 url 접근 가능)" +
            "<br> 토큰이 없어도 해당 Api 엔드포인트를 요청할 수 있습니다.")
    @Parameter(name = "login_id", description = "이름과 프로필 사진을 가져오기 위한 loginId", required = true)
    @GetMapping("/profile")
    public ResponseEntity<CommonResponseDto<UserProfileResDto>> getUserProfile(@RequestParam("login_id")
                                                                                   @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String loginId){
        UserProfileResDto userProfileResDto = userService.getUserProfile(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 이름과, 프로필 사진 조회를 성공적으로 완료하였습니다.",
                        userProfileResDto
                ));
    }

    @Operation(summary = "유저의 Snap 수, Follower 수,  Following 수 조회", description = "유저의 loginId로 유저의 snap 수, 팔로워 수, 팔로잉 수를 조회합니다.")
    @Parameter(name = "login_id", description = "팔로워와 팔로잉 수를 가져오기 위한 loginId", required = true)
    @GetMapping("/profile/count")
    public ResponseEntity<CommonResponseDto<ProfileCntResDto>> getProfileCnt(@RequestParam("login_id")
                                                                                 @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String loginId){
        ProfileCntResDto profileCntResDto = userService.getUserProfileCnt(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 팔로워, 팔로잉 수 조회를 성공적으로 완료하였습니다.",
                        profileCntResDto
                ));
    }

    @Operation(summary = "유저의 태그된 snap 들 조회", description = "유저의 loginId로 유저가 태그된 snap 들을 조회합니다" +
            "<br> snap id 기준 내림차순으로 조회합니다.(최근 snap 이 제일 먼저 조회)")
    @Parameter(name = "login_id", description = "팔로워와 팔로잉 수를 가져오기 위한 loginId", required = true)
    @GetMapping("/profile/tag")
    public ResponseEntity<CommonResponseDto<List<ProfileTagSnapResDto>>> getTagSnap(@RequestParam("login_id")
                                                                                        @NotBlank(message = "로그인 아이디 입력은 필수입니다.") String loginId){

        List<ProfileTagSnapResDto> profileTagSnapResDto = userService.getTagSnap(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저가 태그된 Snap 들을 성공적으로 조회하였습니다.",
                        profileTagSnapResDto
                ));
    }

}
