package me.snaptime.user.service;

import me.snaptime.profile.dto.res.AlbumSnapResDto;
import me.snaptime.profile.dto.res.ProfileCntResDto;
import me.snaptime.profile.dto.res.ProfileTagSnapResDto;
import me.snaptime.profile.dto.res.UserProfileResDto;

import java.util.List;

public interface UserProfileService {

    /* 호출자의 loginId, 피호출자의 loginId를 통해 피호출자의 album과 snap을 조회 */
    public List<AlbumSnapResDto> getAlbumSnap(String yourLoginId, String targetLoginId);
    /* loginId에 해당하는 User의 profile 사진을 조회 */
    public UserProfileResDto getUserProfile(String loginId);
    /* loginId에 해당하는 User의 스냅, 팔로우, 팔로워 수 리턴 */
    public ProfileCntResDto getUserProfileCnt(String loginId);
    /* loginId에 해당하는 User가 Tag된 snap들을 조회합니다 */
    public List<ProfileTagSnapResDto> getTagSnap(String loginId);
}