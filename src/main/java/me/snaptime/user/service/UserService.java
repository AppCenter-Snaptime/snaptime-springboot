package me.snaptime.user.service;

import me.snaptime.user.dto.req.UserUpdateDto;
import me.snaptime.user.dto.res.UserResDto;

public interface UserService {
    public UserResDto getUser(String loginId);
    public UserResDto updateUser(String loginId, UserUpdateDto userUpdateDto);
    public void deleteUser(String loginId);
    public void updatePassword(String loginId, String password);
}
