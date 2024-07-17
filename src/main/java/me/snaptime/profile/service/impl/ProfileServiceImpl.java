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
    private final UserRepository userRepository;
    private final UrlComponent urlComponent;
    private final SnapRepository snapRepository;
    private final FriendService friendService;

    @Override
    @Transactional(readOnly = true)
    public List<AlbumSnapResDto> getAlbumSnap(String ownLoginId, String targetLoginId) {
        User targetUser = userRepository.findByLoginId(targetLoginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return userRepository.findAlbumSnap(targetUser, ownLoginId.equals(targetLoginId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResDto getUserProfile(String ownLoginId, String targetLoginId) {
        User targetUser = userRepository.findByLoginId(targetLoginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        User ownUser = userRepository.findByLoginId(ownLoginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
//        boolean isFollow = friendRepository.checkFriend(ownUser,targetUser);

        String profileURL = urlComponent.makeProfileURL(targetUser.getProfilePhoto().getId());

        return UserProfileResDto.toDto(targetUser, profileURL /*, isFollow*/);
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

        return userRepository.findTagSnap(user);
    }

}
