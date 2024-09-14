package me.snaptime.snapLike.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.service.AlarmAddService;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snapLike.domain.SnapLike;
import me.snaptime.snapLike.repository.SnapLikeRepository;
import me.snaptime.snapLike.service.SnapLikeService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnapLikeServiceImpl implements SnapLikeService {

    private final SnapRepository snapRepository;
    private final UserRepository userRepository;
    private final SnapLikeRepository snapLikeRepository;
    private final AlarmAddService alarmAddService;

    @Override
    @Transactional
    public String toggleSnapLike(String reqEmail, Long snapId){
        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        Optional<SnapLike> snapLikeOptional = snapLikeRepository.findBySnapAndUser(snap,reqUser);

        if(snapLikeOptional.isEmpty()){
            snapLikeRepository.save(
                    SnapLike.builder()
                            .snap(snap)
                            .user(reqUser)
                            .build()
            );
            alarmAddService.createSnapAlarm(reqUser,snap.getUser(),snap, AlarmType.LIKE);
            return "좋아요를 눌렀습니다.";
        }
        else{
            snapLikeRepository.delete(snapLikeOptional.get());
            return "좋아요를 취소하였습니다.";
        }
    }

    @Override
    public Long findSnapLikeCnt(Long snapId){
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        return snapLikeRepository.countBySnap(snap);
    }

    @Override
    public boolean isLikedSnap(Long snapId, String reqEmail){
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return snapLikeRepository.existsBySnapAndUser(snap,reqUser);
    }

}
