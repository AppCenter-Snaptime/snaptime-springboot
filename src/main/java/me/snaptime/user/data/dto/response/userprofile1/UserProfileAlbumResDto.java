package me.snaptime.user.data.dto.response.userprofile1;

import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileAlbumResDto(
        Long userId,
        String userName,
        Long profilePhotoId,
        List<AlbumAndPhotosResDto> albumAndPhotos
){}
