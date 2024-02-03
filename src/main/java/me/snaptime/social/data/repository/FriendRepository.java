package me.snaptime.social.data.repository;

import me.snaptime.social.data.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend,Long> {

}
