package me.snaptime.friendShip.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friendShip.common.FriendSearchType;
import me.snaptime.friendShip.common.FriendStatus;
import me.snaptime.friendShip.domain.FriendShip;
import me.snaptime.friendShip.dto.req.AcceptFollowReqDto;
import me.snaptime.friendShip.dto.res.FindFriendResDto;
import me.snaptime.friendShip.dto.res.FriendCntResDto;
import me.snaptime.friendShip.dto.res.FriendInfo;
import me.snaptime.friendShip.repository.FriendShipRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.NextPageChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.snaptime.user.domain.QUser.user;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendShipService {

    private final FriendShipRepository friendShipRepository;
    private final UserRepository userRepository;
    private final UrlComponent urlComponent;


    // 친구요청 전송(fromUser(요청자)의 팔로잉 +1, toUser의 팔로워 +1)
    @Transactional
    public void sendFriendShipReq(String loginId, String fromUserLoginId){

        User fromUser = findUserByLoginId(loginId);
        User toUser = findUserByLoginId(fromUserLoginId);

        Optional<FriendShip> friendShipOptional = friendShipRepository.findByToUserAndFromUser(toUser,fromUser);
        
        // 만약 이미 팔로우요청을 보낸 유저일 경우
        if(friendShipOptional.isPresent()){
            FriendShip friendShip = friendShipOptional.get();

            if(friendShip.getFriendStatus().equals(FriendStatus.FOLLOW))
                throw new CustomException(ExceptionCode.ALREADY_FOLLOW);
            else if(friendShip.getFriendStatus().equals(FriendStatus.REJECTED))
                throw new CustomException(ExceptionCode.REJECT_FRIEND_REQ);
            else{
                friendShip.updateFriendStatus(FriendStatus.FOLLOW);
                friendShipRepository.save(friendShip);
            }
            return ;
        }

        // 자기자신에게 팔로우요청을 했다면
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

        User fromUser = findUserByLoginId(loginId);
        User toUser = findUserByLoginId(acceptFollowReqDto.fromUserLoginId());
        FriendShip friendShip = findFriendShipByToUserAndFromUser(toUser,fromUser);

        if(friendShip.getFriendStatus() != FriendStatus.WAITING)
            throw new CustomException(ExceptionCode.FRIENDSHIP_REQ_NOT_FOUND);

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
                .orElseThrow(() -> new CustomException(ExceptionCode.FRIENDSHIP_NOT_EXIST));

        User fromUser = findUserByLoginId(loginId);

        if(friendShip.getFromUser().getId() != fromUser.getId()){
            throw new CustomException(ExceptionCode.ACCESS_FAIL_FRIENDSHIP);
        }

        friendShipRepository.delete(friendShip);
    }
    
    // 팔로워 or 팔로잉 친구리스트 조회
    public FindFriendResDto findFriendList(String loginId, String targetLoginId, Long pageNum,
                                           FriendSearchType searchType, String searchKeyword){

        User targetUser = findUserByLoginId(targetLoginId);
        List<Tuple> result = friendShipRepository.findFriendList(targetUser,searchType,pageNum,searchKeyword);

        // 다음 페이지 유무 체크
        boolean hasNextPage = NextPageChecker.hasNextPage(result,20L);
        if(hasNextPage)
            result.remove(20);

        List<FriendInfo> friendInfoList = result.stream().map(entity ->
        {
            boolean isMyFriend = checkIsFriend(loginId,targetUser);
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(user.profilePhoto.id));
            return FriendInfo.toDto(entity,profilePhotoURL,isMyFriend);
        }).collect(Collectors.toList());

        return FindFriendResDto.toDto(friendInfoList, hasNextPage);
    }

    // 유저 프로필 조회 시 팔로잉,팔로워 수를 반환하는 메소드
    public FriendCntResDto findFriendShipCnt(String loginId){
        User user = findUserByLoginId(loginId);

        // 나를 팔로우하는 사람의 수, 내가 팔로우하는 사람의 수 조회
        Long followerCnt = friendShipRepository.countByToUserAndFriendStatus(user,FriendStatus.FOLLOW);
        Long followingCnt = friendShipRepository.countByFromUserAndFriendStatus(user,FriendStatus.FOLLOW);

        return FriendCntResDto.toDto(followerCnt,followingCnt);
    }

    // 해당 유저가 자신이 팔로우했는지 유무체크
    private boolean checkIsFriend(String loginId, User targetUser){
        User user = findUserByLoginId(loginId);

        return friendShipRepository.existsByToUserAndFromUser(user,targetUser);
    }

    private User findUserByLoginId(String loginId){
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
    }

    private FriendShip findFriendShipByToUserAndFromUser(User toUser, User fromUser){
        return friendShipRepository.findByToUserAndFromUser(toUser,fromUser)
                .orElseThrow(() -> new CustomException(ExceptionCode.FRIENDSHIP_NOT_EXIST));
    }
}