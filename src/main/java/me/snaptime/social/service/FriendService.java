package me.snaptime.social.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.social.data.repository.FriendRepository;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendFriendReq(Long userId, String name){

    }
}