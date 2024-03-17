package me.snaptime.social.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.common.FriendSearchType;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.social.data.dto.req.AcceptFollowReqDto;
import me.snaptime.social.data.dto.res.FindFriendResDto;
import me.snaptime.social.data.dto.res.FriendCntResDto;
import me.snaptime.social.data.repository.FriendShipRepository;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendShipService {

    private final FriendShipRepository friendShipRepository;
    private final UserRepository userRepository;

    // 친구요청 전송(fromUser(요청자)의 팔로잉 +1, toUser의 팔로워 +1)
    @Transactional
    public void sendFriendShipReq(String loginId, String fromUserName){
        User fromUser = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        User toUser = findUserByName(fromUserName);

        Optional<FriendShip> friendShip = friendShipRepository.findByToUserAndFromUser(toUser,fromUser);

        if(friendShip.isPresent()){
            FriendStatus friendStatus = friendShip.get().getFriendStatus();
            if(friendStatus.equals(FriendStatus.FOLLOW))
                throw new CustomException(ExceptionCode.ALREADY_FOLLOW);
            else
                throw new CustomException(ExceptionCode.REJECT_FRIEND_REQ);
        }

        if (toUser.getId() == fromUser.getId())
            throw new CustomException(ExceptionCode.SELF_FRIEND_REQ);

        friendShipRepository.save(FriendShip.builder()
                        .fromUser(fromUser)
                        .toUser(toUser)
                        .friendStatus(FriendStatus.FOLLOW)
                        .build());
        friendShipRepository.save(FriendShip.builder()
                        .friendStatus(FriendStatus.WAITING)
                        .fromUser(toUser)
                        .toUser(fromUser)
                        .build());
    }

    // 친구요청 수락(fromUser(수락자)의 팔로잉 +1, toUser의 팔로워 +1)
    // 친구요청 거절(fromUser(수락자)의 팔로워 -1, toUser의 팔로잉 -1)
    @Transactional
    public String acceptFriendShipReq(String loginId, AcceptFollowReqDto acceptFollowReqDto){
        User fromUser = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        User toUser = findUserByName(acceptFollowReqDto.fromUserName());

        FriendShip friendShip = findFriendShipByToUserAndFromUser(toUser,fromUser);

        if(acceptFollowReqDto.isAccept()){
            friendShip.updateFriendStatus(FriendStatus.FOLLOW);
            friendShipRepository.save(friendShip);
            return "팔로우 수락을 완료했습니다.";
        }
        else{
            friendShipRepository.delete(friendShip);
            FriendShip rejectedFriendShip = findFriendShipByToUserAndFromUser(fromUser, toUser);
            rejectedFriendShip.updateFriendStatus(FriendStatus.REJECTED);
            friendShipRepository.save(rejectedFriendShip);
            return "팔로우 거절을 완료했습니다.";
        }
    }

    // 친구 삭제(fromUser(삭제자)의 팔로잉 -1, toUser의 팔로워 -1)
    @Transactional
    public void deleteFriendShip(String loginId, Long friendShipId){
        FriendShip friendShip = friendShipRepository.findById(friendShipId)
                .orElseThrow(() -> new CustomException(ExceptionCode.FRIENDSHIP_NOT_FOUND));

        User fromUser = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if(friendShip.getFromUser().getId() != fromUser.getId()){
            throw new CustomException(ExceptionCode.ACCESS_FAIL_FRIENDSHIP);
        }

        friendShipRepository.delete(friendShip);
    }
    
    // 팔로워 or 팔로잉 친구리스트 조회
    public List<FindFriendResDto> findFriendList(String loginId, Long pageNum, FriendSearchType searchType, String searchKeyword){
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        List<Tuple> result = friendShipRepository.findFriendList(user,searchType,pageNum,searchKeyword);

        return result.stream().map(entity -> {
                    return FindFriendResDto.toDto(entity);
                }).collect(Collectors.toList());
    }

    // 유저 프로필 조회 시 팔로잉,팔로워 수를 반환하는 메소드
    public FriendCntResDto findFriendShipCnt(String loginId){
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 나를 팔로우하는 사람의 수, 내가 팔로우하는 사람의 수 조회
        Long followerCnt = friendShipRepository.countByToUserAndFriendStatus(user,FriendStatus.FOLLOW);
        Long followingCnt = friendShipRepository.countByFromUserAndFriendStatus(user,FriendStatus.FOLLOW);

        return FriendCntResDto.toDto(followerCnt,followingCnt);
    }

    private User findUserByName(String fromUserName){
        return userRepository.findUserByName(fromUserName)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    private FriendShip findFriendShipByToUserAndFromUser(User toUser, User fromUser){
        return friendShipRepository.findByToUserAndFromUser(toUser,fromUser)
                .orElseThrow(() -> new CustomException(ExceptionCode.FRIENDSHIP_NOT_FOUND));
    }
}