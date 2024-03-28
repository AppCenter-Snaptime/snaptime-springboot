package me.snaptime.snap.data.dto.res;

import lombok.Builder;

@Builder
public record FindAlbumInfoResDto(
        Long id,
        String name
) {
}
