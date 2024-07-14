package me.snaptime.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.req.UserUpdateDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.UserResDto;
import me.snaptime.user.service.SignService;
import me.snaptime.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name ="[User] User API", description = "유저 생성,유저 조회, 유저 수정, 유저 삭제")
@Slf4j
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final SignService signService;

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
        UserResDto userResDto = signService.signUp(userReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CommonResponseDto<>(
                        "유저 회원가입을 성공적으로 완료하였습니다.",
                        userResDto));
    }

    @Operation(summary = "로그인", description = "회원 가입 한 유저의 loginId와 password를 입력합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<CommonResponseDto<SignInResDto>> signIn(@Valid @RequestBody SignInReqDto signInReqDto){
        SignInResDto signInResponseDto = signService.signIn(signInReqDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 로그인을 성공적으로 완료하였습니다.",
                        signInResponseDto));
    }
}
