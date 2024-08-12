package me.snaptime.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.TestSignInResDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.dto.res.UserPagingResDto;
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

    @Operation(summary = "자신의 유저 정보 조회",description = "자신의 유저 정보를 조회합니다. ")
    @GetMapping("/my")
    public ResponseEntity<CommonResponseDto<UserFindResDto>> getMyUser(@AuthenticationPrincipal UserDetails userDetails){
        UserFindResDto userFindResDto = userService.getUser(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 정보가 성공적으로 조회되었습니다.",
                        userFindResDto));
    }

    @GetMapping("/{pageNum}")
    @Operation(summary = "이름을 통해 유저리스트 조회",description = "유저이름을 통해 유저리스트를 반환합니다.<br>"+
                                                                "해당 이름으로 시작하는 유저를 20개씩 반환합니다.<br>"+
                                                                "searchKeyword는 반드시 입력해야합니다.")
    public ResponseEntity<CommonResponseDto<UserPagingResDto>> findUserPageByName(
            @RequestParam(name = "searchKeyword") @NotBlank(message = "검색어를 입력해주세요.") String searchKeyword,
            @PathVariable(name = "pageNum") final Long pageNum){

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("유저 검색이 완료되었습니다.",
                userService.findUserPageByName(searchKeyword, pageNum)));
    }

    @Operation(summary = "유저 정보 수정",description = "해당 유저의 정보를 수정합니다. " +
            "<br> 유저 loginId 수정 이후에는, Token의 loginId 정보와 현재 유저의 loginId가 다르므로," +
            "<br> Token을 버리고 재 login을 유도해야 합니다.")
    @PatchMapping()
    public ResponseEntity<CommonResponseDto<UserFindResDto>> changeUser(@AuthenticationPrincipal UserDetails userDetails,
                                                                        @Valid @RequestBody UserUpdateReqDto userUpdateReqDto){
        UserFindResDto userFindResDto = userService.updateUser(userDetails.getUsername(), userUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 정보 수정이 성공적으로 완료되었습니다.",
                        userFindResDto));
    }
    @Operation(summary = "유저 비밀번호 수정",description = "해당 유저의 비밀번호를 수정합니다.")
    @PatchMapping("/password")
    public ResponseEntity<CommonResponseDto<Void>> changeUser(@AuthenticationPrincipal UserDetails userDetails,
                                                              @RequestParam("password")
                                                              @NotBlank(message = "패스워드 입력은 필수입니다.") String password) {
        userService.updatePassword(userDetails.getUsername(), password);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 비밀번호 수정이 성공적으로 완료되었습니다.",
                        null));
    }


    @Operation(summary = "유저 삭제",description = "유저 번호로 유저를 삭제합니다.")
    @DeleteMapping()
    public ResponseEntity<CommonResponseDto<Void>> deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                                                              @RequestParam("password")
                                                              @NotBlank(message = "패스워드 입력은 필수입니다.") String password){
        userService.deleteUser(password, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 삭제가 성공적으로 완료되었습니다.",
                        null));
    }

    @Operation(summary = "회원가입", description = "회원 가입 할 유저의 정보를 입력합니다. " +
            "<br> 회원가입이 완료되면 자동으로 유저의 기본 profile 사진이 등록됩니다." +
            "<br> 이후에 유저의 Token 을 통해 profile 사진을 수정할 수 있습니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<CommonResponseDto<UserFindResDto>> signUp(@Valid @RequestBody UserReqDto userReqDto){
        UserFindResDto userFindResDto = signService.signUp(userReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CommonResponseDto<>(
                        "유저 회원가입을 성공적으로 완료하였습니다.",
                        userFindResDto));
    }

    @Operation(summary = "로그인", description = "회원 가입 한 유저의 loginId와 password를 입력합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<CommonResponseDto<SignInResDto>> signIn(@Valid @RequestBody SignInReqDto signInReqDto){
        SignInResDto signInResDto = signService.signIn(signInReqDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 로그인을 성공적으로 완료하였습니다.",
                        signInResDto));
    }

    @Operation(summary = "엑세스 토큰 재발급", description = "RefreshToken 을 통해 AccessToken 재발급"+
    "<br> 엑세스 토큰이 만료되어 401 에러가 발생하면, RefreshToken을 헤더에 담아 요청"+
    "<br>  AccessToken과 RefreshToken 을 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponseDto<SignInResDto>> reissue(HttpServletRequest request){
        SignInResDto signInResDto = signService.reissueAccessToken(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "리프레시 토큰으로 엑세스 토큰 재발급 성공",
                        signInResDto
                ));
    }

    @Operation(summary = "테스트 로그인", description = "회원 가입 한 유저의 loginId와 password를 입력합니다."+
            "<br> refreshToken을 통한 자동 로그인 구현을 위한 테스트 api입니다")
    @PostMapping("/test/sign-in")
    public ResponseEntity<CommonResponseDto<TestSignInResDto>> testSignIn(@Valid @RequestBody SignInReqDto signInReqDto){
        TestSignInResDto testSignInResDto = signService.testSignIn(signInReqDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "테스트 유저 로그인을 성공적으로 완료하였습니다.",
                        testSignInResDto));
    }
}
