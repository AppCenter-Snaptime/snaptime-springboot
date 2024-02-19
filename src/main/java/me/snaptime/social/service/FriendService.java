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
    public void sendFriendShipReq(String loginId, String name){
        User fromUser = userRepository.findByLoginId(loginId);
        User toUser = userRepository.findUserByName(name)
                .orElseThrow();

        Optional<FriendShip> friendShip = friendRepository.findByToUserAndFromUser(toUser,fromUser);

        if(friendShip.isPresent()){
            FriendStatus friendStatus = friendShip.get().getFriendStatus();
            if(friendStatus.equals(FriendStatus.FOLLOW))
                throw new CustomException(ExceptionCode.ALREADY_FOLLOW);
            else
                throw new CustomException(ExceptionCode.REJECT_FRIEND_REQ);
        }

        if (toUser.getId() == fromUser.getId())
            throw new CustomException(ExceptionCode.SELF_FRIEND_REQ);

        friendRepository.save(FriendShip.builder()
                        .fromUser(fromUser)
                        .toUser(toUser)
                        .friendStatus(FriendStatus.FOLLOW)
                        .build());
        friendRepository.save(FriendShip.builder()
                        .friendStatus(FriendStatus.WATING)
                        .fromUser(toUser)
                        .toUser(fromUser)
                        .build());
    }

    // 친구요청 수락(fromUser의 팔로워 +1, toUser의 팔로우 +1)
    @Transactional
    public void acceptFriendShipReq(Long friendShipId, boolean isAccept){
        // friendShip 조회

        // 수락했을 경우 WATING -> FOLLOW로 변경후 저장
        // 거절했을 경우 WATING -> REJECTED로 변경후 저장

        // friendShip리소스가 존재하지 않거나 STATUS가 REJECTED나 FOLLOW일 경우 예외발생
    }

    // 팔로우 해제
    @Transactional
    public void deleteFriendShip(String loginId, Long friendShipId){
        // friendShip 조회

        // friendShip 삭제

        //friendShip리소스가 존재하지 않으면 예외발생
    }
    
    // 팔로워 or 팔로우 친구리스트 조회
    public Object findFollowerList(String loginId, String searchType){
        // user조회

        // 나를 팔로우 or 팔로워하는 사람의 프로필과 이름 페이징조회

        // dto 래핑 후 반환
        return null;
    }

    // 유저 프로필 조회 시 팔로우,팔로워 수를 반환하는 메소드
    public Object findFriendShipCnt(String loginId){
        // user조회

        // 나를 팔로우하는 사람의 수, 내가 팔로우하는 사람의 수 조회

        return null;
    }

    // 팔로우 or 팔로워 친구리스트에서 친구검색
    public Object findFriendByName(String loginId, String searchKeyword, String searchType){
        // user조회
        
        // 검색키워드로 친구검색

        return null;
    }
}