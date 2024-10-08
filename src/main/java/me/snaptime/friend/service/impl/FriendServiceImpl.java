package me.snaptime.friend.service.impl;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.service.AlarmAddService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.domain.Friend;
import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.friend.dto.res.FriendInfoResDto;
import me.snaptime.friend.dto.res.FriendPagingResDto;
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
    private final AlarmAddService alarmAddService;

    @Override
    @Transactional
    public void sendFollow(String senderEmail, String receiverEmail){

        User sender = findUserByEmail(senderEmail);
        User receiver = findUserByEmail(receiverEmail);

        Optional<Friend> friendOptional = friendRepository.findBySenderAndReceiver(sender,receiver);
        
        // 만약 이미 팔로우요청을 보낸 유저일 경우
        if(friendOptional.isPresent())
            throw new CustomException(ExceptionCode.ALREADY_FOLLOW);

        // 자기자신에게 팔로우요청을 했다면
        if (receiver.getUserId() == sender.getUserId())
            throw new CustomException(ExceptionCode.SELF_FRIEND_REQ);

        friendRepository.save(Friend.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .build());

        alarmAddService.createFollowAlarm(sender,receiver);
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
    public void unFollow(String deletorEmail, String deletedUserEmail){

        User deletor = findUserByEmail(deletorEmail);
        User deletedUser = findUserByEmail(deletedUserEmail);
        Friend friend = friendRepository.findBySenderAndReceiver(deletor,deletedUser)
                .orElseThrow(() -> new CustomException(ExceptionCode.FRIEND_NOT_EXIST));

        friendRepository.delete(friend);
    }

    @Override
    public FriendPagingResDto findFriendPage(String reqEmail, String targetEmail, Long pageNum,
                                             FriendSearchType searchType, String searchKeyword){

        User reqUser = findUserByEmail(reqEmail);
        User targetUser = findUserByEmail(targetEmail);
        List<Tuple> tuples = friendRepository.findFriendPage(targetUser,searchType,pageNum,searchKeyword);

        // 다음 페이지 유무 체크
        boolean hasNextPage = NextPageChecker.hasNextPage(tuples,20L);

        List<FriendInfoResDto> friendInfoResDtos = tuples.stream().map(tuple ->
        {
            boolean isMyFriend = checkIsFollow(reqUser ,findUserByEmail(tuple.get(user.email)));
            String profilePhotoURL = urlComponent.makeProfileURL(tuple.get(user.profilePhoto.profilePhotoId));
            return FriendInfoResDto.toDto(tuple,profilePhotoURL,isMyFriend);
        }).collect(Collectors.toList());

        return FriendPagingResDto.toDto(friendInfoResDtos, hasNextPage);
    }

    @Override
    public FriendCntResDto findFriendCnt(String email){

        User targetUser = findUserByEmail(email);

        // target의 팔로잉,팔로워 수 조회
        Long followingCnt = friendRepository.countBySender(targetUser);
        Long followerCnt = friendRepository.countByReceiver(targetUser);

        return FriendCntResDto.toDto(followerCnt,followingCnt);
    }

    @Override
    public boolean checkIsFollow(User reqUser, User targetUser){
        return friendRepository.existsBySenderAndReceiver(reqUser, targetUser);
    }

    private User findUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
    }

}