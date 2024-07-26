package me.snaptime.user.service;

import jakarta.servlet.http.HttpServletRequest;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.TestSignInResDto;
import me.snaptime.user.dto.res.UserResDto;

public interface SignService {

    /* 회원 가입 하는 메서드 */
    UserResDto signUp(UserReqDto userReqDto);
    /* 로그인 하는 메서드, accessToken을 리턴한다 */
    SignInResDto signIn(SignInReqDto signInReqDto);

    /* 헤더에 담긴 RefreshToken 을 통해 AccessToken을 재발급합니다 */
    SignInResDto reissueAccessToken(HttpServletRequest request);

    /* 토큰 유효시간 만료 후 재발급, 재로그인 테스트용 api */
    TestSignInResDto testSignIn(SignInReqDto signInReqDto);
}
