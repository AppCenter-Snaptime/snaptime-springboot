package me.snaptime.friendShip.repository;

import com.querydsl.core.Tuple;
import me.snaptime.friendShip.common.FriendSearchType;
import me.snaptime.user.domain.User;

import java.util.List;

public interface FriendShipPagingRepository {

    List<Tuple> findFriendList(User reqUser, FriendSearchType searchType, Long pageNum , String searchKeyword);
}
