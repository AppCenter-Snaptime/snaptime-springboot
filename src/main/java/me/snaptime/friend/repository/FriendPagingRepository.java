package me.snaptime.friend.repository;

import com.querydsl.core.Tuple;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.user.domain.User;

import java.util.List;

public interface FriendPagingRepository {

    List<Tuple> findFriendList(User targetUser, FriendSearchType searchType, Long pageNum , String searchKeyword);
}
