package me.snaptime.friend.service;

import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.dto.res.FindFriendResDto;
import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.user.domain.User;

public interface FriendService {

    // 친구요청 전송(sender(요청자)의 팔로잉 +1, receiver의 팔로워 +1)
    void sendFollow(String senderLoginId, String receiverLoginId);

    // 친구요청 수락(sender(수락자)의 팔로잉 +1, receiver의 팔로워 +1)
    // 친구요청 거절(sender(수락자)의 팔로워 -1, receiver의 팔로잉 -1)

    String acceptFollow(User sender, User receiver, boolean isAccept);

    // 친구 삭제(deletingUser(삭제자)의 팔로잉 -1, deletedUser의 팔로워 -1)
    void unFollow(String deletingUserLoginId, String deletedUserLoginId);

    // myloginId유저가 targetLoginId의 팔로워 or 팔로잉 친구리스트 조회
    FindFriendResDto findFriends(String loginId, String targetLoginId, Long pageNum,
                                 FriendSearchType searchType, String searchKeyword);

    // 유저 프로필 조회 시 팔로잉,팔로워 수를 반환하는 메소드
    FriendCntResDto findFriendCnt(String loginId);

    /*
        reqUser가 targetUser를 팔로우했는 지 여부 반환
    */
    boolean checkIsFollow(User reqUser, User targetUser);


}
