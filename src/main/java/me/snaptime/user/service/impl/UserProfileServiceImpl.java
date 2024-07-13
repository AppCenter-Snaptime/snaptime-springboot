package me.snaptime.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friendShip.dto.res.FriendCntResDto;
import me.snaptime.friendShip.service.FriendShipService;
import me.snaptime.profile.dto.res.AlbumSnapResDto;
import me.snaptime.profile.dto.res.ProfileCntResDto;
import me.snaptime.profile.dto.res.ProfileTagSnapResDto;
import me.snaptime.profile.dto.res.UserProfileResDto;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.user.service.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UrlComponent urlComponent;
    private final SnapRepository snapRepository;
    private final FriendShipService friendShipService;

    @Override
    @Transactional(readOnly = true)
    public List<AlbumSnapResDto> getAlbumSnap(String yourLoginId, String targetLoginId) {
        User targetUser = userRepository.findByLoginId(targetLoginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return userRepository.findAlbumSnap(targetUser, yourLoginId.equals(targetLoginId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResDto getUserProfile(String loginId) {
        User reqUser = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        String profileURL = urlComponent.makeProfileURL(reqUser.getProfilePhoto().getId());

        return UserProfileResDto.toDto(reqUser, profileURL);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileCntResDto getUserProfileCnt(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Long userSnapCnt = snapRepository.countByUser(user);
        FriendCntResDto friendCntResDto = friendShipService.findFriendShipCnt(loginId);

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

        return userRepository.findTagSnap(user);
    }
}
