package me.snaptime.social.data.repository;

import me.snaptime.social.common.FriendSearchType;
import me.snaptime.user.data.domain.User;

import java.util.List;

public interface FriendListFindRepository {

    List<Object> findFriendList(User reqUser, FriendSearchType searchType, Long pageNum , String searchKeyword);
}
