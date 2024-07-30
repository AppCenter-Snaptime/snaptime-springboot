package me.snaptime.album.dto.res;

import lombok.Builder;
import me.snaptime.snap.dto.res.SnapInfoResDto;

import java.util.List;

@Builder
public record FindAlbumResDto(
        Long id,
        String name,
        List<SnapInfoResDto> snap

) {
}
