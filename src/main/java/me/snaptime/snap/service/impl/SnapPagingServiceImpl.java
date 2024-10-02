package me.snaptime.snap.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.dto.res.SnapDetailInfoResDto;
import me.snaptime.snap.dto.res.SnapPagingResDto;
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

    public SnapPagingResDto findSnapPage(String reqEmail, Long pageNum){

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        List<Tuple> tuples = snapRepository.findSnapPage(pageNum,reqUser);
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,10L);

        List<SnapDetailInfoResDto> snapDetailInfoResDtos = tuples.stream().map(tuple ->
        {

            Long snapId = tuple.get(snap.id);
            String profilePhotoURL = urlComponent.makeProfileURL(tuple.get(user.profilePhoto.profilePhotoId));
            String snapPhotoURL = urlComponent.makePhotoURL(tuple.get(snap.fileName),false);

            return SnapDetailInfoResDto.toDto(tuple,profilePhotoURL,snapPhotoURL,
                    snapTagService.findTagUsers(snapId, reqEmail),
                    snapLikeService.findSnapLikeCnt(snapId),
                    snapLikeService.isLikedSnap(snapId, reqEmail));
        }).collect(Collectors.toList());

        return SnapPagingResDto.toDto(snapDetailInfoResDtos,hasNextPage);
    }

}
