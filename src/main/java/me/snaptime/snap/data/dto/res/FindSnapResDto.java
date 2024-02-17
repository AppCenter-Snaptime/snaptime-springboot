package me.snaptime.snap.data.dto.res;

import lombok.Builder;
import me.snaptime.snap.data.domain.Snap;

@Builder
public record FindSnapResDto(
        Long id,
        String oneLineJournal,
        Long photoId,
        String albumName,
        String userUid
) {
    public static FindSnapResDto entityToResDto(Snap entity) {
        return FindSnapResDto.builder()
                .id(entity.getId())
                .oneLineJournal(entity.getOneLineJournal())
                .photoId(entity.getPhoto().getId())
                .albumName(entity.getAlbum().getName())
                .userUid(entity.getUser().getLonginId())
                .build();
    }
}
