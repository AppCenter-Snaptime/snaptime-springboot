package me.snaptime.album.dto.res;

import lombok.Builder;

@Builder
public record GetAllAlbumListResDto(
        Long id,
        String name
) {
}
