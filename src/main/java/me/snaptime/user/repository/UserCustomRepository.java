package me.snaptime.user.repository;

import me.snaptime.profile.userProfile.dto.res.AlbumSnapResDto;
import me.snaptime.profile.userProfile.dto.res.ProfileTagSnapResDto;
import me.snaptime.user.domain.User;

import java.util.List;

public interface UserCustomRepository {
    List<AlbumSnapResDto> findAlbumSnap(User targetUser, Boolean checkPermission);
    List<ProfileTagSnapResDto> findTagSnap(User reqUser);
}
