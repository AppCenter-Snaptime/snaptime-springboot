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
public record SnapResInfo(
        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        String writerLoginId,
        String profilePhotoURL,
        String writerUserName,
        List<FindTagUserResDto> findTagUserList,
        Long likeCnt,
        boolean isLikedSnap
) {
    public static SnapResInfo toDto(Tuple result, String profilePhotoURL, String snapPhotoURL,
                                    List<FindTagUserResDto> findTagUserList, Long likeCnt, boolean isLikedSnap){

        return SnapResInfo.builder()
                .snapId(result.get(snap.id))
                .oneLineJournal(String.valueOf(result.get(snap.oneLineJournal)))
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(result.get(snap.createdDate))
                .snapModifiedDate(result.get(snap.lastModifiedDate))
                .writerLoginId(result.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .writerUserName(result.get(user.name))
                .findTagUserList(findTagUserList)
                .likeCnt(likeCnt)
                .isLikedSnap(isLikedSnap)
                .build();
    }

    public static SnapResInfo toDto(Snap result, String profilePhotoURL, String snapPhotoURL,
                                    List<FindTagUserResDto> findTagUserList, Long likeCnt, boolean isLikedSnap){

        return SnapResInfo.builder()
                .snapId(result.getId())
                .oneLineJournal(result.getOneLineJournal())
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(result.getCreatedDate())
                .snapModifiedDate(result.getLastModifiedDate())
                .writerLoginId(result.getUser().getLoginId())
                .profilePhotoURL(profilePhotoURL)
                .writerUserName(result.getUser().getName())
                .findTagUserList(findTagUserList)
                .likeCnt(likeCnt)
                .isLikedSnap(isLikedSnap)
                .build();
    }

}
