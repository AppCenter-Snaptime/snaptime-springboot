package me.snaptime.snap.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.dto.res.FindSnapPagingResDto;
import me.snaptime.snap.dto.res.SnapPagingInfo;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snap.service.SnapPagingService;
import me.snaptime.snapLike.service.SnapLikeService;
import me.snaptime.snapTag.service.SnapTagService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.NextPageChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.snaptime.snap.domain.QSnap.snap;
import static me.snaptime.user.domain.QUser.user;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnapPagingServiceImpl implements SnapPagingService {

    private final UserRepository userRepository;
    private final SnapRepository snapRepository;
    private final UrlComponent urlComponent;
    private final SnapTagService snapTagService;
    private final SnapLikeService snapLikeService;

    public FindSnapPagingResDto findSnapPaging(String loginId, Long pageNum){

        User reqUser = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        List<Tuple> result = snapRepository.findSnapPaging(pageNum,reqUser);
        boolean hasNextPage = NextPageChecker.hasNextPage(result,10L);

        List<SnapPagingInfo> snapPagingInfoList = result.stream().map(entity ->
        {
            Long snapId = entity.get(snap.id);
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(user.profilePhoto.id));
            String snapPhotoURL = urlComponent.makePhotoURL(entity.get(snap.fileName),false);

            return SnapPagingInfo.toDto(entity,profilePhotoURL,snapPhotoURL,
                    snapTagService.findTagUserList(snapId),
                    snapLikeService.findSnapLikeCnt(snapId),
                    snapLikeService.isLikedSnap(snapId, loginId));
        }).collect(Collectors.toList());

        return FindSnapPagingResDto.toDto(snapPagingInfoList,hasNextPage);
    }

}
