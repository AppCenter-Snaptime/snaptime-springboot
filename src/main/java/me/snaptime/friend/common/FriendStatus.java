package me.snaptime.friend.common;

public enum FriendStatus {
    /*
        FOLLOW : fromUser가 toUser를 팔로우한 상태
        WAITING : fromUser가 toUser의 친구요청에 대해 수락대기중, 수락하면 FOLLOW로 바뀐다.
        REJECTED : fromUser가 toUser에게 친구요청이 거절된 상태
    */

    FOLLOW,
    WAITING,
    REJECTED
}
