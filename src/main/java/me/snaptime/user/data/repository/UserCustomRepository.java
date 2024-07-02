package me.snaptime.user.data.repository;

import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.response.userprofile.AlbumSnapResDto;
import me.snaptime.user.data.dto.response.userprofile.ProfileTagSnapResDto;

import java.util.List;

public interface UserCustomRepository {
    List<AlbumSnapResDto> findAlbumSnap(User targetUser, Boolean checkPermission);
    List<ProfileTagSnapResDto> findTagSnap(User reqUser);
}
