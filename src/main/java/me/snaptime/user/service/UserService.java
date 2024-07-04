package me.snaptime.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.common.jwt.JwtProvider;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.data.dto.res.FriendCntResDto;
import me.snaptime.social.service.FriendShipService;
import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.request.SignInReqDto;
import me.snaptime.user.data.dto.request.UserReqDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.SignInResDto;
import me.snaptime.user.data.dto.response.UserResDto;
import me.snaptime.user.data.dto.response.userprofile.AlbumSnapResDto;
import me.snaptime.user.data.dto.response.userprofile.ProfileCntResDto;
import me.snaptime.user.data.dto.response.userprofile.ProfileTagSnapResDto;
import me.snaptime.user.data.dto.response.userprofile.UserProfileResDto;
import me.snaptime.user.data.repository.ProfilePhotoRepository;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UrlComponent urlComponent;
    private final SnapRepository snapRepository;
    private final FriendShipService friendShipService;

    @Transactional(readOnly = true)
    public UserResDto getUser(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return UserResDto.toDto(user);
    }

    public UserResDto signUp(UserReqDto userRequestDto) {

        //로그인 id가 이미 존재하는지 확인
        if(userRepository.findByLoginId(userRequestDto.loginId()).isPresent()){
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
        User user = userRepository.findByLoginId(signInRequestDto.loginId()).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(signInRequestDto.password(), user.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_EQUAL);
        }
        String accessToken = jwtProvider.createAccessToken(user.getLoginId(), user.getRoles());

        SignInResDto signInResponseDto = SignInResDto.builder()
                .accessToken(accessToken)
                .build();

        return signInResponseDto;
    }

    public UserResDto updateUser(String loginId, UserUpdateDto userUpdateDto) {

        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if(!loginId.equals(userUpdateDto.loginId()) && userRepository.findByLoginId(userUpdateDto.loginId()).isPresent()){
            throw new CustomException(ExceptionCode.LOGIN_ID_ALREADY_EXIST);
        }

        if (userUpdateDto.name() != null && !userUpdateDto.name().isEmpty()) {
            user.updateUserName(userUpdateDto.name());
        }

        if (userUpdateDto.loginId() != null && !userUpdateDto.loginId().isEmpty()) {
            user.updateUserLoginId(userUpdateDto.loginId());
        }

        if (userUpdateDto.email() != null && !userUpdateDto.email().isEmpty()) {
            user.updateUserEmail(userUpdateDto.email());
        }

        if (userUpdateDto.birthDay() != null && !userUpdateDto.birthDay().isEmpty()) {
            user.updateUserBirthDay(userUpdateDto.birthDay());
        }

        return UserResDto.toDto(user);
    }

    public void deleteUser(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        userRepository.deleteById(user.getId());
    }

    public void updatePassword(String loginId, String password){
        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.updateUserPassword(passwordEncoder.encode(password));
        }
        else{
            throw new CustomException(ExceptionCode.PASSWORD_DUPLICATE);
        }

    }

    @Transactional(readOnly = true)
    public List<AlbumSnapResDto> getAlbumSnap(String yourLoginId, String targetLoginId){
        User targetUser = userRepository.findByLoginId(targetLoginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));

        List<AlbumSnapResDto> albumSnapResDtoList = userRepository.findAlbumSnap(targetUser,yourLoginId.equals(targetLoginId));
        
        return albumSnapResDtoList;
    }

    @Transactional(readOnly = true)
    public UserProfileResDto getUserProfile(String loginId){
        User reqUser = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));
        String profileURL = urlComponent.makeProfileURL(reqUser.getProfilePhoto().getId());
        UserProfileResDto userProfileResDto = UserProfileResDto.toDto(reqUser, profileURL);

        return userProfileResDto;
    }

    @Transactional(readOnly = true)
    public ProfileCntResDto getUserProfileCnt(String loginId){
        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Long userSnapCnt = snapRepository.countByUser(user);
        FriendCntResDto friendCntResDto = friendShipService.findFriendShipCnt(loginId);

        return ProfileCntResDto.builder()
                .snapCnt(userSnapCnt)
                .followerCnt(friendCntResDto.followerCnt())
                .followingCnt(friendCntResDto.followingCnt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProfileTagSnapResDto> getTagSnap(String loginId){
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        List<ProfileTagSnapResDto> profileTagSnapList = userRepository.findTagSnap(user);

        return profileTagSnapList;
    }
}