package me.snaptime.snapLike.service;

public interface SnapLikeService {

    // 스냅 좋아요 토글기능
    String toggleSnapLike(String loginId, Long snapId);

    // 스냅에 달린 좋아요 개수 조회
    Long findSnapLikeCnt(Long snapId);

    // 자신이 좋아요를 누른 스냅인지 여부 체크
    boolean isLikedSnap(Long snapId, String loginId);

}
