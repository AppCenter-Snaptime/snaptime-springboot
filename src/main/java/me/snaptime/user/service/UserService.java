package me.snaptime.user.service;

import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.dto.res.UserPagingResDto;

public interface UserService {
    UserFindResDto getUser(String email);
    UserPagingResDto findUserPageByName(String searchKeyword, Long pageNum);
    UserFindResDto updateUser(String email, UserUpdateReqDto userUpdateReqDto);
    void deleteUser(String email, String password);
    void updatePassword(String email, String password);
}
