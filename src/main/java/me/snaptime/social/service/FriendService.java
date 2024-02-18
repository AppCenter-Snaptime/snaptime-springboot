package me.snaptime.social.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.social.data.repository.FriendRepository;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 친구요청 전송(fromUser의 팔로우 +1, toUser의 팔로워 +1)
    @Transactional
    public void sendFriendShipReq(Long userId, String name){
        User fromUser = userRepository.findById(userId)
                .orElseThrow();
        User toUser = userRepository.findUserByName(name)
                .orElseThrow();

        Optional<FriendShip> friendShip = friendRepository.findByToUserAndFromUser(toUser,fromUser);

        if(friendShip.isPresent()){
            FriendStatus friendStatus = friendShip.get().getFriendStatus();
            if(friendStatus.equals(FriendStatus.WATING))
                throw new CustomException(ExceptionCode.WATING_FRIEND_REQ);
            else if(friendStatus.equals(FriendStatus.REJECTED))
                throw new CustomException(ExceptionCode.REJECT_FRIEND_REQ);
            else
                throw new CustomException(ExceptionCode.ALREADY_FRIEND);
        }

        if (toUser.getId() == fromUser.getId())
            throw new CustomException(ExceptionCode.SELF_FRIEND_REQ);
        friendRepository.save(FriendShip.builder()
                        .fromUser(fromUser)
                        .toUser(toUser)
                        .friendStatus(FriendStatus.WATING)
                        .build());
    }

    // 친구요청 수락(fromUser의 팔로워 +1, toUser의 팔로우 +1)
    @Transactional
    public void acceptFriendShipReq(Long friendShipId){

    }

    // 팔로우 해제
    @Transactional
    public void deleteFriendShip(Long userId, Long friendShipId){

    }

    // 팔로우 친구리스트 조회
    public Object findFollowList(){
        return null;
    }

    // 팔로워 친구리스트 조회
    public Object findFollowerList(){
        return null;
    }

    // 유저 프로필 조회 시 팔로우,팔로워 수를 반환하는 메소드
    public Object findFriendShipCnt(){
        return null;
    }
}