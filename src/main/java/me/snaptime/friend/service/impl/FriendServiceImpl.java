package me.snaptime.friend.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.service.CreateAlarmService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.domain.Friend;
import me.snaptime.friend.dto.res.FindFriendResDto;
import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.friend.dto.res.FriendInfo;
import me.snaptime.friend.repository.FriendRepository;
import me.snaptime.friend.service.FriendService;
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
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final UrlComponent urlComponent;
    private final CreateAlarmService createAlarmService;

    @Override
    @Transactional
    public void sendFollow(String senderLoginId, String receiverLoginId){

        User sender = findUserByLoginId(senderLoginId);
        User receiver = findUserByLoginId(receiverLoginId);

        Optional<Friend> friendOptional = friendRepository.findBySenderAndReceiver(sender,receiver);
        
        // 만약 이미 팔로우요청을 보낸 유저일 경우
        if(friendOptional.isPresent())
            throw new CustomException(ExceptionCode.ALREADY_FOLLOW);

        // 자기자신에게 팔로우요청을 했다면
        if (receiver.getId() == sender.getId())
            throw new CustomException(ExceptionCode.SELF_FRIEND_REQ);

        friendRepository.save(Friend.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .build());

        createAlarmService.createFollowAlarm(sender,receiver);
    }

    @Override
    @Transactional
    public String acceptFollow(User sender, User receiver, boolean isAccept){

        // sender가 receiver에게 친구요청을 보낸게 맞는지 체크
        Optional<Friend> friendOptional = friendRepository.findBySenderAndReceiver(sender,receiver);
        if(friendOptional.isEmpty())
            throw new CustomException(ExceptionCode.FRIEND_REQ_NOT_FOUND);

        if(isAccept){
            Friend friend = Friend.builder()
                    .sender(receiver)
                    .receiver(sender)
                    .build();
            friendRepository.save(friend);

            return "팔로우 수락을 완료했습니다.";
        }
        else{
            friendRepository.delete(friendOptional.get());
            return "팔로우 거절을 완료했습니다.";
        }
    }

    @Override
    @Transactional
    public void unFollow(String deletorLoginId, String deletedUserLoginId){

        User deletor = findUserByLoginId(deletorLoginId);
        User deletedUser = findUserByLoginId(deletedUserLoginId);
        Friend friend = friendRepository.findBySenderAndReceiver(deletor,deletedUser)
                .orElseThrow(() -> new CustomException(ExceptionCode.FRIEND_NOT_EXIST));

        friendRepository.delete(friend);
    }

    @Override
    public FindFriendResDto findFriendList(String reqLoginId, String targetLoginId, Long pageNum,
                                           FriendSearchType searchType, String searchKeyword){

        User reqUser = findUserByLoginId(reqLoginId);
        User targetUser = findUserByLoginId(targetLoginId);
        List<Tuple> result = friendRepository.findFriendList(targetUser,searchType,pageNum,searchKeyword);

        // 다음 페이지 유무 체크
        boolean hasNextPage = NextPageChecker.hasNextPage(result,20L);

        List<FriendInfo> friendInfoList = result.stream().map(entity ->
        {
            boolean isMyFriend = checkIsFriend(reqUser ,findUserByLoginId(entity.get(user.loginId)));
            String profilePhotoURL = urlComponent.makeProfileURL(entity.get(user.profilePhoto.id));
            return FriendInfo.toDto(entity,profilePhotoURL,isMyFriend);
        }).collect(Collectors.toList());

        return FindFriendResDto.toDto(friendInfoList, hasNextPage);
    }

    @Override
    public FriendCntResDto findFriendCnt(String loginId){

        User targetUser = findUserByLoginId(loginId);

        // target의 팔로잉,팔로워 수 조회
        Long followingCnt = friendRepository.countBySender(targetUser);
        Long followerCnt = friendRepository.countByReceiver(targetUser);

        return FriendCntResDto.toDto(followerCnt,followingCnt);
    }

    // 자신이 해당유저를 팔로우했는 지 유무 반환
    private boolean checkIsFriend(User reqUser, User targetUser){
        return friendRepository.existsBySenderAndReceiver(reqUser, targetUser);
    }

    private User findUserByLoginId(String loginId){
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
    }

}