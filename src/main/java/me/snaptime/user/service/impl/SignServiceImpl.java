package me.snaptime.user.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.jwt.JwtProvider;
import me.snaptime.jwt.redis.RefreshToken;
import me.snaptime.jwt.redis.RefreshTokenRepository;
import me.snaptime.profilePhoto.domain.ProfilePhoto;
import me.snaptime.profilePhoto.repository.ProfilePhotoRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.TestSignInResDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.user.service.SignService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SignServiceImpl implements SignService {

    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserFindResDto signUp(UserReqDto userReqDto) {

        //로그인 id가 이미 존재하는지 확인
        if(userRepository.findByLoginId(userReqDto.loginId()).isPresent()){
            throw new CustomException(ExceptionCode.LOGIN_ID_ALREADY_EXIST);
        }

        String fileName = "default.png";
        String filePath =  "/test_resource/" + fileName;

        ProfilePhoto profilePhoto = ProfilePhoto.builder()
                .profilePhotoName(fileName)
                .profilePhotoPath(filePath)
                .build();
        //기본 프로필 저장
        profilePhotoRepository.save(profilePhoto);

        //새로운 사용자 객체 생성
        User user = User.builder()
                .name(userReqDto.name())
                .loginId(userReqDto.loginId())
                .password(passwordEncoder.encode(userReqDto.password()))
                .email(userReqDto.email())
                .birthDay(userReqDto.birthDay())
                //단일 권한을 가진 리스트 생성, 하나의 요소를 가진 불변의 리스트 생성
                .roles(Collections.singletonList("ROLE_USER"))
                .profilePhoto(profilePhoto)
                .build();

        return UserFindResDto.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public SignInResDto signIn(SignInReqDto signInReqDto) {
        User user = userRepository.findByLoginId(signInReqDto.loginId()).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(signInReqDto.password(), user.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_EQUAL);
        }
        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getLoginId(), user.getRoles());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getLoginId(),user.getRoles());
        refreshTokenRepository.save(new RefreshToken(user.getUserId(),refreshToken));

        return SignInResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public SignInResDto reissueAccessToken(HttpServletRequest request){

        String token = jwtProvider.getAuthorizationToken(request);
        Long userId = jwtProvider.getUserId(token);

        RefreshToken refreshToken = refreshTokenRepository.findById(userId).orElseThrow(()-> new CustomException(ExceptionCode.TOKEN_NOT_FOUND));

        if(!refreshToken.getRefreshToken().equals(token)) {
            throw new CustomException(ExceptionCode.TOKEN_INVALID);
        }

        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));

        String newAccessToken = jwtProvider.createAccessToken(userId,user.getLoginId(),user.getRoles());
        String newRefreshToken = jwtProvider.createRefreshToken(userId,user.getLoginId(),user.getRoles());

        SignInResDto signInResDto = SignInResDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        refreshTokenRepository.save(new RefreshToken(userId, newRefreshToken));

        return signInResDto;
    }

    @Override
    @Transactional(readOnly = true)
    public TestSignInResDto testSignIn(SignInReqDto signInReqDto) {
        User testUser = userRepository.findByLoginId(signInReqDto.loginId()).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(signInReqDto.password(), testUser.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_EQUAL);
        }
        String testAccessToken = jwtProvider.testCreateAccessToken(testUser.getUserId(), testUser.getLoginId(), testUser.getRoles());
        String testRefreshToken = jwtProvider.testCreateRefreshToken(testUser.getUserId(), testUser.getLoginId(),testUser.getRoles());
        refreshTokenRepository.save(new RefreshToken(testUser.getUserId(),testRefreshToken));

        return TestSignInResDto.builder()
                .testAccessToken(testAccessToken)
                .testRefreshToken(testRefreshToken)
                .build();
    }
}
