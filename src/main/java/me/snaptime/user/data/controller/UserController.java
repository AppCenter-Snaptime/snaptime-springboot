package me.snaptime.user.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.user.data.dto.request.SignInRequestDto;
import me.snaptime.user.data.dto.request.UserRequestDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.SignInResponseDto;
import me.snaptime.user.data.dto.response.UserResponseDto;
import me.snaptime.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name ="[User] User API", description = "유저 생성,유저 조회, 유저 수정, 유저 삭제")
@Slf4j
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회",description = "유저 번호로 유저를 조회합니다.")
    //@Parameter(name = "userId", description = "찾을 유저의 id")
    @GetMapping()
    public ResponseEntity<CommonResponseDto<UserResponseDto>> getUser(){
        log.info("[getUser] 현재 로그인된 사용자의 토큰에서 loginId 추출");
        //SecurityContextHolder에서 현재 인증된 사용자의 정보를 담고 있는 Authentication 객체를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentcation객체가 가지고 있는 Principal 객체가 반환됩니다. 이 객체는 UserDetails 인터페이스를 구현한 사용자 정보 객체입니다.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //UserDetails객체에서 인증된 사용자의 loginId를 getUsername()메서드로 가져옵니다.
        String loginId = userDetails.getUsername(); // 로그인한 사용자의 아이디

        log.info("[getUser] 유저 loginId로 유저 정보를 가져옵니다. loginId : {}", loginId);
        UserResponseDto userResponseDto = userService.getUser(loginId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 정보가 성공적으로 조회되었습니다.",
                        userResponseDto));
    }


    @Operation(summary = "유저 정보 수정",description = "유저 번호로 해당 유저의 정보를 수정합니다.")
    //@Parameter(name = "userId", description = "수정할 유저의 id")
    @PutMapping()
    public ResponseEntity<CommonResponseDto<UserResponseDto>> changeUser(@RequestBody UserUpdateDto userUpdateDto){
        log.info("[changeUser] 현재 로그인된 사용자의 토큰에서 loginId 추출");
        //SecurityContextHolder에서 현재 인증된 사용자의 정보를 담고 있는 Authentication 객체를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentcation객체가 가지고 있는 Principal 객체가 반환됩니다. 이 객체는 UserDetails 인터페이스를 구현한 사용자 정보 객체입니다.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //UserDetails객체에서 인증된 사용자의 loginId를 getUsername()메서드로 가져옵니다.
        String loginId = userDetails.getUsername(); // 로그인한 사용자의 아이디

        log.info("[getUser] 유저 loginId와 업데이트 내용으로 유저 정보를 수정합니다. loginId : {}", loginId);
        UserResponseDto userResponseDto = userService.updateUser(loginId, userUpdateDto);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 정보 수정이 성공적으로 완료되었습니다.",
                        userResponseDto));
    }

    @Operation(summary = "유저 삭제",description = "유저 번호로 유저를 삭제합니다.")
    //@Parameter(name = "userId", description = "삭제할 유저의 id")
    @DeleteMapping()
    public ResponseEntity<CommonResponseDto<Void>> deleteUser(){
        log.info("[deleteUser] 현재 로그인된 사용자의 토큰에서 loginId 추출");
        //SecurityContextHolder에서 현재 인증된 사용자의 정보를 담고 있는 Authentication 객체를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentcation객체가 가지고 있는 Principal 객체가 반환됩니다. 이 객체는 UserDetails 인터페이스를 구현한 사용자 정보 객체입니다.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //UserDetails객체에서 인증된 사용자의 loginId를 getUsername()메서드로 가져옵니다.
        String loginId = userDetails.getUsername(); // 로그인한 사용자의 아이디

        log.info("[deleteUser] 유저를 삭제합니다. uid : {}", loginId);
        userService.deleteUser(loginId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 삭제가 성공적으로 완료되었습니다.",
                        null));
    }

    @Operation(summary = "회원가입", description = "회원 가입 할 유저의 정보를 입력합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<CommonResponseDto<UserResponseDto>> signUp(@RequestBody UserRequestDto userRequestDto){
        //String role = "USER";
        log.info("[signUp] 회원가입을 수행합니다. loginId : {}, password : ****, name : {}, email : {}, birthDay : {}",userRequestDto.loginId(),userRequestDto.name(),userRequestDto.email(),userRequestDto.birthDay());

        UserResponseDto userResponseDto = userService.signUp(userRequestDto);

        log.info("[signUp] 회원가입을 완료했습니다. id : {}",userResponseDto.loginId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CommonResponseDto<>(
                        "유저 회원가입을 성공적으로 완료하였습니다.",
                        userResponseDto));
    }

    @Operation(summary = "로그인", description = "회원 가입 한 유저의 loginId와 password를 입력합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<CommonResponseDto<SignInResponseDto>> signIn(@RequestBody SignInRequestDto signInRequestDto){
        log.info("[sign-in] 로그인을 수행합니다. id : {}, passowrd : ****",signInRequestDto.loginId());
        SignInResponseDto signInResponseDto = userService.signIn(signInRequestDto);

        log.info("[sign-in] 정상적으로 로그인에 성공하였습니다. accessToken : {}",signInResponseDto.accessToken());

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(
                        "유저 로그인을 성공적으로 완료하였습니다.",
                        signInResponseDto));
    }
}
