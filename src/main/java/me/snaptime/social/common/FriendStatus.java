package me.snaptime.social.common;

public enum FriendStatus {
    /*
        FOLLOW : fromUser가 toUser를 팔로우한 상태
        WATING : toUser가 fromUser의 친구요청에 대기중인 상태
        REJECTED : fromUser가 toUser에게 친구요청이 거절된 상태
    */

    FOLLOW,
    WATING,
    REJECTED
}
