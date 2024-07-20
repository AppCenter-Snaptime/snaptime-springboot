package me.snaptime.user.service;

import jakarta.servlet.http.HttpServletRequest;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.UserResDto;

public interface SignService {

    /* 회원 가입 하는 메서드 */
    public UserResDto signUp(UserReqDto userReqDto);
    /* 로그인 하는 메서드, accessToken을 리턴한다 */
    public SignInResDto signIn(SignInReqDto signInReqDto);

    /* 헤더에 담긴 RefreshToken 을 통해 AccessToken을 재발급합니다 */
    public SignInResDto reissueAccessToken(HttpServletRequest request);
}
