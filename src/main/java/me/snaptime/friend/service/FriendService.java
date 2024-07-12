package me.snaptime.friend.service;

import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.dto.req.AcceptFollowReqDto;
import me.snaptime.friend.dto.res.FindFriendResDto;
import me.snaptime.friend.dto.res.FriendCntResDto;

public interface FriendService {

    // 친구요청 전송(fromUser(요청자)의 팔로잉 +1, toUser의 팔로워 +1)
    void sendFollow(String loginId, String fromUserLoginId);

    // 친구요청 수락(fromUser(수락자)의 팔로잉 +1, toUser의 팔로워 +1)
    // 친구요청 거절(fromUser(수락자)의 팔로워 -1, toUser의 팔로잉 -1)
    String acceptFollow(String loginId, AcceptFollowReqDto acceptFollowReqDto);

    // 친구 삭제(fromUser(삭제자)의 팔로잉 -1, toUser의 팔로워 -1)
    void unFollow(String loginId, Long friendShipId);

    // 팔로워 or 팔로잉 친구리스트 조회
    FindFriendResDto findFriendList(String loginId, String targetLoginId, Long pageNum,
                                    FriendSearchType searchType, String searchKeyword);

    // 유저 프로필 조회 시 팔로잉,팔로워 수를 반환하는 메소드
    FriendCntResDto findFriendCnt(String loginId);


}
