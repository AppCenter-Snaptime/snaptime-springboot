package me.snaptime.social.data.repository;

import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<FriendShip,Long> {
    Optional<FriendShip> findByToUserAndFromUser(User toUser, User fromUser);
}
