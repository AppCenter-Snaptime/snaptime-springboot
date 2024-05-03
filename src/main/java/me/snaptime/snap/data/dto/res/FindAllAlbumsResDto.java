package me.snaptime.snap.data.dto.res;

import lombok.Builder;

@Builder
public record FindAllAlbumsResDto(
        Long id,
        String name,
        String photoUrl
) {
}
