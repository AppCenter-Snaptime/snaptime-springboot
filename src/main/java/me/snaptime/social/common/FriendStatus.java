package me.snaptime.social.common;

public enum FriendStatus {
    /*
        FOLLOW : fromUser가 toUser를 팔로우한 상태
        WATING : toUser가 fromUser의 친구요청에 대기중인 상태
        REJECTED : toUser가 fromUser의 친구요청을 거절한 상태
    */

    FOLLOW,
    WATING,
    REJECTED
}
