package me.snaptime.snap.data.dto.res;

import lombok.Builder;
import me.snaptime.snap.data.domain.Snap;

import java.time.LocalDateTime;

@Builder
public record FindSnapResDto(
        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        String loginId,
        String profilePhotoURL,
        String userName
) {
    public static FindSnapResDto entityToResDto(Snap entity, String snapPhotoURL, String profilePhotoURL) {
        String userUid = null;
        String userName = null;
        if (entity.getUser() != null) {
            userUid = entity.getUser().getLoginId();
            userName = entity.getUser().getName();
        }
        return FindSnapResDto.builder()
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
