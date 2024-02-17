package me.snaptime.social.data.repository;

import me.snaptime.social.data.domain.Friend;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Long> {
    Optional<Friend> findByToUserAndFromUser(User toUser, User fromUser);
}
