package me.snaptime.album.dto.res;

import lombok.Builder;
import me.snaptime.snap.dto.res.SnapInfoDto;

import java.util.List;

@Builder
public record FindAlbumResDto(
        Long id,
        String name,
        List<SnapInfoDto> snap

) {
}
