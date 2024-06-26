package me.snaptime.snap.data.dto.res;

import com.querydsl.core.Tuple;
import lombok.Builder;
import me.snaptime.social.data.dto.res.FindTagUserResDto;

import java.time.LocalDateTime;
import java.util.List;

import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.user.data.domain.QUser.user;

@Builder
public record FindSnapPagingResDto(
        Long snapId,
        String oneLineJournal,
        String snapPhotoURL,
        LocalDateTime snapCreatedDate,
        LocalDateTime snapModifiedDate,
        String loginId,
        String profilePhotoURL,
        String userName,
        List<FindTagUserResDto> findTagUserList,
        Long likeCnt

) {
    public static FindSnapPagingResDto toDto(Tuple result, String profilePhotoURL, String snapPhotoURL,
                                             List<FindTagUserResDto> findTagUserList, Long likeCnt){
        return FindSnapPagingResDto.builder()
                .snapId(result.get(snap.id))
                .oneLineJournal(String.valueOf(result.get(snap.oneLineJournal)))
                .snapPhotoURL(snapPhotoURL)
                .snapCreatedDate(result.get(snap.createdDate))
                .snapModifiedDate(result.get(snap.lastModifiedDate))
                .loginId(result.get(user.loginId))
                .profilePhotoURL(profilePhotoURL)
                .userName(result.get(user.name))
                .findTagUserList(findTagUserList)
                .likeCnt(likeCnt)
                .build();
    }
}
