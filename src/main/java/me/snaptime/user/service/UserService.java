package me.snaptime.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.request.UserRequestDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.UserResponseDto;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return UserResponseDto.toDto(user);
    }

    @Transactional
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

    @Transactional
    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));
        userRepository.deleteById(user.getId());
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto){

        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));

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
