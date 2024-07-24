package me.snaptime.snap.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snapTag.dto.res.FindTagUserResDto;

import java.time.LocalDateTime;
import java.util.List;

import static me.snaptime.snap.domain.QSnap.snap;
import static me.snaptime.user.domain.QUser.user;


@Builder
public record SnapDetailInfoDto(

        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        String writerLoginId,
        String profilePhotoURL,
        String writerUserName,
        List<FindTagUserResDto> findTagUsers,
        Long likeCnt,
        boolean isLikedSnap
) {
    public static SnapDetailInfoDto toDto(Tuple tuple, String profilePhotoURL, String snapPhotoURL,
                                          List<FindTagUserResDto> findTagUsers, Long likeCnt, boolean isLikedSnap){

        return SnapDetailInfoDto.builder()
                .snapId(tuple.get(snap.id))
                .oneLineJournal(String.valueOf(tuple.get(snap.oneLineJournal)))
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(tuple.get(snap.createdDate))
                .snapModifiedDate(tuple.get(snap.lastModifiedDate))
                .writerLoginId(tuple.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .writerUserName(tuple.get(user.name))
                .findTagUsers(findTagUsers)
                .likeCnt(likeCnt)
                .isLikedSnap(isLikedSnap)
                .build();
    }

    public static SnapDetailInfoDto toDto(Snap snap, String profilePhotoURL, String snapPhotoURL,
                                       List<FindTagUserResDto> findTagUsers, Long likeCnt, boolean isLikedSnap){

        return SnapDetailInfoDto.builder()
                .snapId(snap.getId())
                .oneLineJournal(snap.getOneLineJournal())
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(snap.getCreatedDate())
                .snapModifiedDate(snap.getLastModifiedDate())
                .writerLoginId(snap.getUser().getLoginId())
                .profilePhotoURL(profilePhotoURL)
                .writerUserName(snap.getUser().getName())
                .findTagUsers(findTagUsers)
                .likeCnt(likeCnt)
                .isLikedSnap(isLikedSnap)
                .build();
    }


}
