package me.snaptime.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.friend.service.FriendService;
import me.snaptime.profile.dto.res.AlbumSnapResDto;
import me.snaptime.profile.dto.res.ProfileCntResDto;
import me.snaptime.profile.dto.res.ProfileTagSnapResDto;
import me.snaptime.profile.dto.res.UserProfileResDto;
import me.snaptime.profile.repository.ProfileRepository;
import me.snaptime.profile.service.ProfileService;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final UrlComponent urlComponent;
    private final SnapRepository snapRepository;
    private final FriendService friendService;

    @Override
    @Transactional(readOnly = true)
    public List<AlbumSnapResDto> getAlbumSnap(String reqLoginId, String targetLoginId) {
        User targetUser = userRepository.findByLoginId(targetLoginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return profileRepository.findAlbumSnap(targetUser, reqLoginId.equals(targetLoginId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResDto getUserProfile(String reqLoginId, String targetLoginId) {

        Boolean isFollow = null;
        User targetUser = userRepository.findByLoginId(targetLoginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if(!reqLoginId.equals(targetLoginId)){
            User reqUser = userRepository.findByLoginId(reqLoginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
            isFollow = friendService.checkIsFollow(reqUser, targetUser);
        }

        String profileURL = urlComponent.makeProfileURL(targetUser.getProfilePhoto().getProfilePhotoId());

        return UserProfileResDto.toDto(targetUser, profileURL, isFollow);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileCntResDto getUserProfileCnt(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Long userSnapCnt = snapRepository.countByUser(user);
        FriendCntResDto friendCntResDto = friendService.findFriendCnt(loginId);

        return ProfileCntResDto.builder()
                .snapCnt(userSnapCnt)
                .followerCnt(friendCntResDto.followerCnt())
                .followingCnt(friendCntResDto.followingCnt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileTagSnapResDto> getTagSnap(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return profileRepository.findTagSnap(user);
    }

}
