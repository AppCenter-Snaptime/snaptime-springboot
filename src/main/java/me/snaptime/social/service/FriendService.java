package me.snaptime.Social.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.Social.data.repository.FriendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;

    @Transactional
    public void sendFriendReq(Long userId, String nickName){

    }
}
