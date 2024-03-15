package me.snaptime.user.data.repository;

import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.response.userprofile2.AlbumSnapResDto;

import java.util.List;

public interface UserCustomRepository {
    //UserProfileResDto findUserProfile1(User reqUser);
    //UserProfileResDto findUserProfile2(User reqUser);
    List<AlbumSnapResDto> fidAlbumSnap(User reqUser);

}
