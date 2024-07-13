package me.snaptime.friend.repository;

import me.snaptime.friend.domain.Friend;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Long>, FriendPagingRepository {
    Optional<Friend> findBySenderAndReceiver(User sender, User receiver);
    
    // sender의 팔로잉 수 조회
    Long countBySender(User sender);

    // receiver의 팔로워 수 조회
    Long countByReceiver(User receiver);

    boolean existsBySenderAndReceiver(User sender, User receiver);
}
