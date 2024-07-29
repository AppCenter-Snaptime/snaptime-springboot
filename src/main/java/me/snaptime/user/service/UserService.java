package me.snaptime.user.service;

import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.dto.res.UserPagingResDto;

public interface UserService {
    UserFindResDto getUser(String loginId);
    UserPagingResDto findUserPageByName(String searchKeyword, Long pageNum);
    UserFindResDto updateUser(String loginId, UserUpdateReqDto userUpdateReqDto);
    void deleteUser(String loginId);
    void updatePassword(String loginId, String password);
}
