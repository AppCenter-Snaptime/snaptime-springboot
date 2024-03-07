package me.snaptime.snap.data.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;

import java.time.LocalDateTime;

import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.user.data.domain.QUser.user;

@Builder
public record FindSnapPagingResDto(
        Long snapId,
        String oneLineJournal,
        Long photoId,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        Long userId,
        Long profilePhotoId,
        String userName

) {
    public static FindSnapPagingResDto toDto(Tuple result){
        return FindSnapPagingResDto.builder()
                .snapId(result.get(snap.id))
                .oneLineJournal(String.valueOf(result.get(snap.oneLineJournal)))
                .photoId(result.get(snap.photo.id))
                .snapCreatedDate(result.get(snap.createdDate))
                .snapModifiedDate(result.get(snap.lastModifiedDate))
                .userId(result.get(user.id))
                .profilePhotoId(result.get(user.profilePhoto.id))
                .userName(result.get(user.name))
                .build();
    }
}
