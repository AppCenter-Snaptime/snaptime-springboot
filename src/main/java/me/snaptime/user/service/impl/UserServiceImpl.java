package me.snaptime.user.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.domain.User;
import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserResDto;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResDto getUser(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return UserResDto.toDto(user);
    }

    public UserResDto updateUser(String loginId, UserUpdateReqDto userUpdateDto) {

        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (userUpdateDto.name() != null && !userUpdateDto.name().isEmpty()) {
            user.updateUserName(userUpdateDto.name());
        }

        if (userUpdateDto.email() != null && !userUpdateDto.email().isEmpty()) {
            user.updateUserEmail(userUpdateDto.email());
        }

        if (userUpdateDto.birthDay() != null && !userUpdateDto.birthDay().isEmpty()) {
            user.updateUserBirthDay(userUpdateDto.birthDay());
        }
        return UserResDto.toDto(user);
    }

    public void deleteUser(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        userRepository.deleteById(user.getId());
    }

    public void updatePassword(String loginId, String password){
        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.updateUserPassword(passwordEncoder.encode(password));
        }
        else{
            throw new CustomException(ExceptionCode.PASSWORD_DUPLICATE);
        }
    }
}