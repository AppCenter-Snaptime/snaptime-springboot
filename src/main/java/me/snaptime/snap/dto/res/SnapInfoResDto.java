package me.snaptime.snap.dto.res;

import lombok.Builder;
import me.snaptime.snap.domain.Snap;

import java.time.LocalDateTime;

@Builder
public record SnapInfoResDto(
        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        String loginId,
        String profilePhotoURL,
        String userName
) {
    public static SnapInfoResDto entityToResDto(Snap entity, String snapPhotoURL, String profilePhotoURL) {
        String userUid = null;
        String userName = null;
        if (entity.getUser() != null) {
            userUid = entity.getUser().getLoginId();
            userName = entity.getUser().getName();
        }
        return SnapInfoResDto.builder()
                .snapId(entity.getId())
                .oneLineJournal(entity.getOneLineJournal())
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(entity.getCreatedDate())
                .snapModifiedDate(entity.getLastModifiedDate())
                .loginId(userUid)
                .profilePhotoURL(profilePhotoURL)
                .userName(userName)
                .build();
    }
}