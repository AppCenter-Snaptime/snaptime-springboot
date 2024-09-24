package me.snaptime.profile.service;

import me.snaptime.profile.dto.res.AlbumSnapResDto;
import me.snaptime.profile.dto.res.ProfileCntResDto;
import me.snaptime.profile.dto.res.ProfileTagSnapResDto;
import me.snaptime.profile.dto.res.UserProfileResDto;

import java.util.List;

public interface ProfileService {
    /* 호출자의 loginId, 피호출자의 loginId를 통해 피호출자의 album과 snap을 조회 */
    List<AlbumSnapResDto> getAlbumSnap(String reqEmail, String targetEmail);
    /* loginId에 해당하는 User의 profile 사진을 조회 */
    UserProfileResDto getUserProfile(String reqEmail, String targetEmail);
    /* loginId에 해당하는 User의 스냅, 팔로우, 팔로워 수 리턴 */
    ProfileCntResDto getUserProfileCnt(String email);
    /* loginId에 해당하는 User가 Tag된 snap들을 조회합니다 */
    List<ProfileTagSnapResDto> getTagSnap(String email);

}
