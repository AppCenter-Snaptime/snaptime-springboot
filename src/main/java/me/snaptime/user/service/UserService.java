package me.snaptime.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.request.UserRequestDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.UserResponseDto;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new NoSuchElementException("해당 로그인 아이드의 유저를 찾을 수 없습니다."));

        return UserResponseDto.toDto(user);
    }

    public UserResponseDto signUp(UserRequestDto userRequestDto){

        User user = User.builder()
                .Id(null)
                .name(userRequestDto.name())
                .loginId(userRequestDto.loginId())
                .password(userRequestDto.password())
                .email(userRequestDto.email())
                .birthDay(userRequestDto.birthDay())
                .build();

        return UserResponseDto.toDto(userRepository.save(user));
    }

    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 id의 유저가 존재하지 않습니다."));
        userRepository.deleteById(user.getId());
    }

    public UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto){

        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 loginId의 유저가 존재하지 않습니다."));

        if (userUpdateDto.name() !=null && !userUpdateDto.name().isEmpty() && !userUpdateDto.name().equals("string")){
            user.updateUserName(userUpdateDto.name());
        }

        if (userUpdateDto.loginId() !=null && !userUpdateDto.loginId().isEmpty()&& !userUpdateDto.loginId().equals("string")){
            user.updateUserLoginId(userUpdateDto.loginId());
        }

        if (userUpdateDto.email() !=null && !userUpdateDto.email().isEmpty()&& !userUpdateDto.email().equals("string")){
            user.updateUserEmail(userUpdateDto.email());
        }

        if (userUpdateDto.birthDay() !=null && !userUpdateDto.birthDay().isEmpty()&& !userUpdateDto.birthDay().equals("string")){
            user.updateUserBirthDay(userUpdateDto.birthDay());
        }

        return UserResponseDto.toDto(userRepository.save(user));
    }

}
