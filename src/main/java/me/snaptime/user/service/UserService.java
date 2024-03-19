package me.snaptime.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.common.jwt.JwtProvider;
import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.request.SignInReqDto;
import me.snaptime.user.data.dto.request.UserReqDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.SignInResDto;
import me.snaptime.user.data.dto.response.UserResDto;
import me.snaptime.user.data.dto.response.userprofile.AlbumSnapResDto;
import me.snaptime.user.data.dto.response.userprofile.UserProfileResDto;
import me.snaptime.user.data.repository.ProfilePhotoRepository;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @Transactional(readOnly = true)
    public UserResDto getUser(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return UserResDto.toDto(user);
    }

    @Transactional
    public UserResDto signUp(UserReqDto userRequestDto) {


        String fileName = "default.png";
        String filePath = FOLDER_PATH + fileName;

        ProfilePhoto profilePhoto = ProfilePhoto.builder()
                .profilePhotoName(fileName)
                .profilePhotoPath(filePath)
                .build();
        //기본 프로필 저장
        profilePhotoRepository.save(profilePhoto);

        User user = User.builder()
                .name(userRequestDto.name())
                .loginId(userRequestDto.loginId())
                .password(passwordEncoder.encode(userRequestDto.password()))
                .email(userRequestDto.email())
                .birthDay(userRequestDto.birthDay())
                //단일 권한을 가진 리스트 생성, 하나의 요소를 가진 불변의 리스트 생성
                .roles(Collections.singletonList("ROLE_USER"))
                .profilePhoto(profilePhoto)
                .build();

        return UserResDto.toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public SignInResDto signIn(SignInReqDto signInRequestDto) {
        User user = userRepository.findByLoginId(signInRequestDto.loginId()).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(signInRequestDto.password(), user.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_EQUAL);
        }
        String accessToken = jwtProvider.createAccessToken(user.getLoginId(), user.getRoles());

        SignInResDto signInResponseDto = SignInResDto.builder()
                .accessToken(accessToken)
                .build();

        return signInResponseDto;
    }

    @Transactional
    public void deleteUser(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        userRepository.deleteById(user.getId());
    }

    @Transactional
    public UserResDto updateUser(String loginId, UserUpdateDto userUpdateDto) {

        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (userUpdateDto.name() != null && !userUpdateDto.name().isEmpty() && !userUpdateDto.name().equals("string")) {
            user.updateUserName(userUpdateDto.name());
        }

        if (userUpdateDto.loginId() != null && !userUpdateDto.loginId().isEmpty() && !userUpdateDto.loginId().equals("string")) {
            user.updateUserLoginId(userUpdateDto.loginId());
        }

        if (userUpdateDto.email() != null && !userUpdateDto.email().isEmpty() && !userUpdateDto.email().equals("string")) {
            user.updateUserEmail(userUpdateDto.email());
        }

        if (userUpdateDto.birthDay() != null && !userUpdateDto.birthDay().isEmpty() && !userUpdateDto.birthDay().equals("string")) {
            user.updateUserBirthDay(userUpdateDto.birthDay());
        }

        return UserResDto.toDto(user);
    }


    @Transactional(readOnly = true)
    public List<AlbumSnapResDto> getAlbumSnap(String loginId){
        User reqUser = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));

        List<AlbumSnapResDto> albumSnapResDtoList = userRepository.fidAlbumSnap(reqUser);

        return albumSnapResDtoList;
    }

    @Transactional(readOnly = true)
    public UserProfileResDto getUserProfile(String loginId){
        User reqUser = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));

        UserProfileResDto userProfileResDto = UserProfileResDto.toDto(reqUser);

        return userProfileResDto;
    }


//    @Transactional
//    public UserProfileResDto getUserProfile(String loginId) {
//        User reqUser = userRepository.findByLoginId(loginId).orElseThrow(()->new CustomException(ExceptionCode.USER_NOT_FOUND));
//
//        UserProfileResDto userProfileResDto = userRepository.findUserProfile1(reqUser);
//
//        return userProfileResDto;
//    }

}