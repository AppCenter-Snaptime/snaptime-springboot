package me.snaptime.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.user.data.dto.request.SignInReqDto;
import me.snaptime.user.data.dto.request.UserReqDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.SignInResDto;
import me.snaptime.user.data.dto.response.UserResDto;
import me.snaptime.user.data.dto.response.userprofile.AlbumSnapResDto;
import me.snaptime.user.data.dto.response.userprofile.UserProfileResDto;
import me.snaptime.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name ="[User] User API", description = "유저 생성,유저 조회, 유저 수정, 유저 삭제")
@Slf4j
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회",description = "유저 번호로 유저를 조회합니다.")
    @GetMapping()
    public ResponseEntity<CommonResponseDto<UserResDto>> getUser(@AuthenticationPrincipal UserDetails principal){
        UserResDto userResDto = userService.getUser(principal.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 정보가 성공적으로 조회되었습니다.",
                        userResDto));
    }


    @Operation(summary = "유저 정보 수정",description = "해당 유저의 정보를 수정합니다.")
    @PutMapping()
    public ResponseEntity<CommonResponseDto<UserResDto>> changeUser(@AuthenticationPrincipal UserDetails principal,
                                                                    @RequestBody UserUpdateDto userUpdateDto){
        UserResDto userResDto = userService.updateUser(principal.getUsername(), userUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 정보 수정이 성공적으로 완료되었습니다.",
                        userResDto));
    }
    @Operation(summary = "유저 비밀번호 수정",description = "해당 유저의 비밀번호를 수정합니다.")
    @PutMapping("/updatePassword")
    public ResponseEntity<CommonResponseDto<Void>> changeUser(@AuthenticationPrincipal UserDetails principal,
                                                              @RequestParam("password") String password){
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

    @Operation(summary = "회원가입", description = "회원 가입 할 유저의 정보를 입력합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<CommonResponseDto<UserResDto>> signUp(@RequestBody UserReqDto userReqDto){
        UserResDto userResDto = userService.signUp(userReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CommonResponseDto<>(
                        "유저 회원가입을 성공적으로 완료하였습니다.",
                        userResDto));
    }

    @Operation(summary = "로그인", description = "회원 가입 한 유저의 loginId와 password를 입력합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<CommonResponseDto<SignInResDto>> signIn(@RequestBody SignInReqDto signInReqDto){
        SignInResDto signInResponseDto = userService.signIn(signInReqDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 로그인을 성공적으로 완료하였습니다.",
                        signInResponseDto));
    }

    @Operation(summary = "유저 앨범, 스냅 조회", description = "유저의 앨범들과, 각 앨범의 스냅들을 조회합니다.")
    @Parameter(name = "loginId", description = "앨범과 사진들을 가져오기 위한 loginId", required = true)
    @GetMapping("/albumSnap")
    public ResponseEntity<CommonResponseDto<List<AlbumSnapResDto>>> getAlbumSnap(@RequestParam("loginId") String loginId){
        List<AlbumSnapResDto> albumSnapResDtoList = userService.getAlbumSnap(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 앨범과 스냅 조회를 성공적으로 완료하였습니다.",
                        albumSnapResDtoList
                ));
    }

    @Operation(summary = "유저 이름, 프로필 사진 조회", description = "유저의 이름과, 프로필 사진을 조회합니다.")
    @Parameter(name = "loginId", description = "이름과 프로필 사진을 가져오기 위한 loginId", required = true)
    @GetMapping("/profile")
    public ResponseEntity<CommonResponseDto<UserProfileResDto>> getUserProfile(@RequestParam("loginId") String loginId){
        UserProfileResDto userProfileResDto = userService.getUserProfile(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 이름과, 프로필 사진 조회를 성공적으로 완료하였습니다.",
                        userProfileResDto
                ));
    }
}
