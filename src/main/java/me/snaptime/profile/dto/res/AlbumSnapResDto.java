package me.snaptime.profile.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record AlbumSnapResDto(
        Long albumId,
        String albumName,
        List<String> snapUrlList
){}
