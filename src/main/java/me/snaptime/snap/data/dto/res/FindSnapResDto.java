package me.snaptime.snap.data.dto.res;

import lombok.Builder;
import me.snaptime.snap.data.domain.Snap;

@Builder
public record FindSnapResDto(
        Long id,
        String oneLineJournal,
        String photoURL,
        String albumName,
        String userUid
) {
    public static FindSnapResDto entityToResDto(Snap entity, String photoURL) {
        String albumName = null;
        String userUid = null;
        if (entity.getAlbum() != null) {
                albumName = entity.getAlbum().getName();
        }
        if (entity.getUser() != null) {
            userUid = entity.getUser().getLoginId();
        }
        return FindSnapResDto.builder()
                .id(entity.getId())
                .oneLineJournal(entity.getOneLineJournal())
                .photoURL(photoURL)
                .albumName(albumName)
                .userUid(userUid)
                .build();
    }
}
