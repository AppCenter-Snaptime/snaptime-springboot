package me.snaptime.album.dto.res;

import lombok.Builder;

@Builder
public record FindAllAlbumsResDto(
        Long id,
        String name,
        String photoUrl
) {
}
