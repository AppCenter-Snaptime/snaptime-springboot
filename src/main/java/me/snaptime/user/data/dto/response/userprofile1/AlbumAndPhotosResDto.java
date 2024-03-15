package me.snaptime.user.data.dto.response.userprofile1;

import lombok.Builder;

import java.util.List;

@Builder
public record AlbumAndPhotosResDto(
        Long albumId,
        String albumName,
        List<Long> photoIdList
){}
