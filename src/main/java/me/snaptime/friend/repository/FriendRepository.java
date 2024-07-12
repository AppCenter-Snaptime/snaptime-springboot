package me.snaptime.friend.repository;

import me.snaptime.friend.common.FriendStatus;
import me.snaptime.friend.domain.Friend;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Long>, FriendPagingRepository {
    Optional<Friend> findByToUserAndFromUser(User toUser, User fromUser);
    
    // 내가 팔로우 하는 사람의 수
    Long countByFromUserAndFriendStatus(User fromUser, FriendStatus friendStatus);

    // 나를 팔로우 하는 사람의 수
    Long countByToUserAndFriendStatus(User toUser, FriendStatus friendStatus);

    boolean existsByToUserAndFromUser(User toUser, User fromUser);
}
