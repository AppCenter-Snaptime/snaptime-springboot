package me.snaptime.snapLike.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snapLike.domain.SnapLike;
import me.snaptime.snapLike.repository.SnapLikeRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
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

    // 자신이 좋아요를 누른 스냅인지 여부 체크
    public boolean isLikedSnap(Long snapId, String loginId){
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return snapLikeRepository.existsBySnapAndUser(snap,user);
    }

}
