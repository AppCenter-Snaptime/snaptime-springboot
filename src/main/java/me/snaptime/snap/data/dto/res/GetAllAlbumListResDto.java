package me.snaptime.snap.data.dto.res;

import lombok.Builder;

@Builder
public record GetAllAlbumListResDto(
        Long id,
        String name
) {
}
