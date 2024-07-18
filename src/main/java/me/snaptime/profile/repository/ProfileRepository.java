package me.snaptime.profile.repository;

import me.snaptime.profile.dto.res.AlbumSnapResDto;
import me.snaptime.profile.dto.res.ProfileTagSnapResDto;
import me.snaptime.user.domain.User;

import java.util.List;

public interface ProfileRepository{

    List<AlbumSnapResDto> findAlbumSnap(User targetUser, Boolean checkPermission);
    List<ProfileTagSnapResDto> findTagSnap(User targetUser);
}
