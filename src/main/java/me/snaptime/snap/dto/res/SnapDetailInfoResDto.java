package me.snaptime.snap.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snapTag.dto.res.TagUserFindResDto;

import java.time.LocalDateTime;
import java.util.List;

import static me.snaptime.snap.domain.QSnap.snap;
import static me.snaptime.user.domain.QUser.user;


@Builder
public record SnapDetailInfoResDto(

        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        String writerEmail,
        String profilePhotoURL,
        String writerUserName,
        List<TagUserFindResDto> tagUserFindResDtos,
        Long likeCnt,
        boolean isLikedSnap
) {
    public static SnapDetailInfoResDto toDto(Tuple tuple, String profilePhotoURL, String snapPhotoURL,
                                             List<TagUserFindResDto> tagUserFindResDtos, Long likeCnt, boolean isLikedSnap){

        return SnapDetailInfoResDto.builder()
                .snapId(tuple.get(snap.id))
                .oneLineJournal(String.valueOf(tuple.get(snap.oneLineJournal)))
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(tuple.get(snap.createdDate))
                .snapModifiedDate(tuple.get(snap.lastModifiedDate))
                .writerEmail(tuple.get(user.email))
                .profilePhotoURL(profilePhotoURL)
                .writerUserName(tuple.get(user.name))
                .tagUserFindResDtos(tagUserFindResDtos)
                .likeCnt(likeCnt)
                .isLikedSnap(isLikedSnap)
                .build();
    }

    public static SnapDetailInfoResDto toDto(Snap snap, String profilePhotoURL, String snapPhotoURL,
                                             List<TagUserFindResDto> tagUserFindResDtos, Long likeCnt, boolean isLikedSnap){

        return SnapDetailInfoResDto.builder()
                .snapId(snap.getId())
                .oneLineJournal(snap.getOneLineJournal())
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(snap.getCreatedDate())
                .snapModifiedDate(snap.getLastModifiedDate())
                .writerEmail(snap.getUser().getEmail())
                .profilePhotoURL(profilePhotoURL)
                .writerUserName(snap.getUser().getName())
                .tagUserFindResDtos(tagUserFindResDtos)
                .likeCnt(likeCnt)
                .isLikedSnap(isLikedSnap)
                .build();
    }


}
