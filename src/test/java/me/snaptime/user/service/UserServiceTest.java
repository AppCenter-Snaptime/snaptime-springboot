package me.snaptime.user.service;

import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.request.UserRequestDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.UserResponseDto;
import me.snaptime.user.data.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User givenUser;

    @BeforeEach
    public void setUpTestSet() {
        givenUser = User.builder()
                .Id(1L)
                .name("홍길순")
                .loginId("kang4746")
                .password("test1234")
                .email("strong@gmail.com")
                .birthDay("1999-10-29")
                .build();
    }

    @Test
    @DisplayName("given_when_then 방식으로 getUser 서비스 성공 테스트")
    public void getUser() {
        //given
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(givenUser));
        //when
        UserResponseDto userResponseDto = userService.getUser(1L);

        //then
        Assertions.assertEquals(givenUser.getId(),userResponseDto.id());
        Assertions.assertEquals(givenUser.getName(),userResponseDto.name());
        Assertions.assertEquals(givenUser.getLoginId(),userResponseDto.loginId());
        Assertions.assertEquals(givenUser.getPassword(),userResponseDto.password());
        Assertions.assertEquals(givenUser.getEmail(),userResponseDto.email());
        Assertions.assertEquals(givenUser.getBirthDay(),userResponseDto.birthDay());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("given_when_then 방식으로 signUp 서비스 성공 테스트")
    public void signUp() {
        //given
        UserRequestDto givenRequest = UserRequestDto.builder()
                .name("홍길순")
                .loginId("kang4746")
                .password("test1234")
                .email("strong@gmail.com")
                .birthDay("1999-10-29")
                .build();


        //userRepository.save(any(User.class)) 메서드가 호출되면
        // 첫 번째 전달된 User 객체를 반환하도록(Mockito의 returnsFirstArg() 메서드를 사용하여) 설정하는 것입니다.
        Mockito.when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());
        //when
        UserResponseDto userResponseDto = userService.signUp(givenRequest);

        //then
        Assertions.assertEquals(givenRequest.name(),userResponseDto.name());
        Assertions.assertEquals(givenRequest.loginId(),userResponseDto.loginId());
        Assertions.assertEquals(givenRequest.password(),userResponseDto.password());
        Assertions.assertEquals(givenRequest.email(),userResponseDto.email());
        Assertions.assertEquals(givenRequest.birthDay(),userResponseDto.birthDay());

        verify(userRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("given_when_then 방식으로 deleteUser 서비스 성공 테스트")
    public void deleteUser() {
        //given
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(givenUser));
        //when
        userService.deleteUser(1L);

        //then
        verify(userRepository,times(1)).findById(1L);
        verify(userRepository,times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("given_when_then 방식으로 updateUser 서비스 성공 테스트")
    public void updateUser() {
        //given

        UserUpdateDto userUpdateDto = new UserUpdateDto("string","jun4746","strong@naver.com","string");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(givenUser));
        Mockito.when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());

        //when
        UserResponseDto userResponseDto = userService.updateUser(1L,userUpdateDto);

        //then
        Assertions.assertEquals(1L,userResponseDto.id());
        Assertions.assertEquals("홍길순",userResponseDto.name());
        Assertions.assertEquals("jun4746",userResponseDto.loginId());
        Assertions.assertEquals("strong@naver.com",userResponseDto.email());
        Assertions.assertEquals("1999-10-29",userResponseDto.birthDay());

        verify(userRepository,times(1)).findById(1L);
        verify(userRepository,times(1)).save(any());
    }
}