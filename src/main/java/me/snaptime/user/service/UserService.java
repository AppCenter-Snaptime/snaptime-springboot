package me.snaptime.user.service;

import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserResDto;

public interface UserService {
    UserResDto getUser(String loginId);
    UserResDto updateUser(String loginId, UserUpdateReqDto userUpdateReqDto);
    void deleteUser(String loginId);
    void updatePassword(String loginId, String password);
}
