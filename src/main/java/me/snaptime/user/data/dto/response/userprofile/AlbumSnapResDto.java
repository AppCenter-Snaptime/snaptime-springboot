package me.snaptime.user.data.dto.response.userprofile;

import lombok.Builder;

import java.util.List;

@Builder
public record AlbumSnapResDto(
        Long albumId,
        String albumName,
        List<Long> snapIdList
){}
