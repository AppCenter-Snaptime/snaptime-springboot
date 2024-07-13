package me.snaptime.user.service;

import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.UserResDto;

public interface SignService {

    /* 회원 가입 하는 메서드 */
    public UserResDto signUp(UserReqDto userRequestDto);
    /* 로그인 하는 메서드, accessToken을 리턴한다 */
    public SignInResDto signIn(SignInReqDto signInRequestDto);
}
