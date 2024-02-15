package me.snaptime.Social.data.repository;

import me.snaptime.Social.data.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend,Long> {

}
