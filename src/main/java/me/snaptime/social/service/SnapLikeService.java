package me.snaptime.social.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.data.domain.SnapLike;
import me.snaptime.social.data.repository.snapLike.SnapLikeRepository;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnapLikeService {

    private final SnapRepository snapRepository;
    private final UserRepository userRepository;
    private final SnapLikeRepository snapLikeRepository;

    @Transactional
    // 스냅 좋아요 토글기능
    public String toggleSnapLike(String loginId, Long snapId){
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        Optional<SnapLike> optionalSnapLike = snapLikeRepository.findBySnapAndUser(snap,user);

        if(optionalSnapLike.isEmpty()){
            snapLikeRepository.save(
                    SnapLike.builder()
                            .snap(snap)
                            .user(user)
                            .build()
            );
            return "좋아요를 눌렀습니다.";
        }
        else{
            snapLikeRepository.delete(optionalSnapLike.get());
            return "좋아요를 취소하였습니다.";
        }
    }

    // 스냅에 달린 좋아요 개수 조회
    public Long findSnapLikeCnt(Long snapId){
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        return snapLikeRepository.countBySnap(snap);
    }
}
