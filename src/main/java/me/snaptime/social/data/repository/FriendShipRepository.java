package me.snaptime.social.data.repository;

import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<FriendShip,Long> ,
        QuerydslPredicateExecutor<FriendShip> {
    Optional<FriendShip> findByToUserAndFromUser(User toUser, User fromUser);
}
