package me.snaptime.social.data.repository.friendShip;

import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.user.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<FriendShip,Long>, FriendShipPagingRepository {
    Optional<FriendShip> findByToUserAndFromUser(User toUser, User fromUser);
    
    // 내가 팔로우 하는 사람의 수
    Long countByFromUserAndFriendStatus(User fromUser, FriendStatus friendStatus);

    // 나를 팔로우 하는 사람의 수
    Long countByToUserAndFriendStatus(User toUser, FriendStatus friendStatus);
}
