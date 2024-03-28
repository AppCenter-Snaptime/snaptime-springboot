package me.snaptime.snap.data.dto.res;

import lombok.Builder;

import java.util.List;

@Builder
public record FindAlbumResDto(
        Long id,
        String name,
        List<String> photoUrl
) {
}
