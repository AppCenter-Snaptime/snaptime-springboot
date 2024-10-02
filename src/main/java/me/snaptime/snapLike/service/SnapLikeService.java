package me.snaptime.snapLike.service;

import me.snaptime.snapLike.dto.res.SnapLikeResDto;

public interface SnapLikeService {

    // 스냅 좋아요 토글기능
    SnapLikeResDto toggleSnapLike(String reqLoginId, Long snapId);

    // 스냅에 달린 좋아요 개수 조회
    Long findSnapLikeCnt(Long snapId);

    // 자신이 좋아요를 누른 스냅인지 여부 체크
    boolean isLikedSnap(Long snapId, String reqLoginId);

}
