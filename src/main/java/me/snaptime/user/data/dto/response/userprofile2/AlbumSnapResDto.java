package me.snaptime.user.data.dto.response.userprofile2;

import lombok.Builder;
import me.snaptime.snap.data.domain.Snap;

import java.util.List;

@Builder
public record AlbumSnapResDto(
        Long albumId,
        String albumName,
        List<Long> snapIdList
){}
