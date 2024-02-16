package me.snaptime.social.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.domain.Friend;
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

    @Transactional
    public void sendFriendReq(Long userId, String name){
        User fromUser = userRepository.findById(userId)
                .orElseThrow();
        User toUser = userRepository.findUserByName(name)
                .orElseThrow();

        Optional<Friend> friend = friendRepository.findByToUserAndFromUser(toUser,fromUser);

        if(friend.isPresent()){
            FriendStatus friendStatus = friend.get().getFriendStatus();
            if(friendStatus.equals(FriendStatus.WATING))
                throw new CustomException(ExceptionCode.WATING_FRIEND_REQ);
            else if(friendStatus.equals(FriendStatus.REJECTED))
                throw new CustomException(ExceptionCode.REJECT_FRIEND_REQ);
            else
                throw new CustomException(ExceptionCode.ALREADY_FRIEND);
        }

        if (toUser.getId() == fromUser.getId())
            throw new CustomException(ExceptionCode.SELF_FRIEND_REQ);
        friendRepository.save(Friend.builder()
                        .fromUser(fromUser)
                        .toUser(toUser)
                        .friendStatus(FriendStatus.WATING)
                        .build());
    }
}