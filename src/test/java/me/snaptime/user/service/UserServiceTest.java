package me.snaptime.user.service;

import me.snaptime.common.jwt.JwtProvider;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.request.SignInRequestDto;
import me.snaptime.user.data.dto.request.UserRequestDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.SignInResponseDto;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    private User givenUser;

    @BeforeEach
    public void setUpTestSet() {
        givenUser = User.builder()
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
        Mockito.when(userRepository.findByLoginId("kang4746"))
                .thenReturn(Optional.of(givenUser));
        //when
        UserResponseDto userResponseDto = userService.getUser("kang4746");

        //then
        Assertions.assertEquals(givenUser.getId(),userResponseDto.id());
        Assertions.assertEquals(givenUser.getName(),userResponseDto.name());
        Assertions.assertEquals(givenUser.getLoginId(),userResponseDto.loginId());
        Assertions.assertEquals(givenUser.getPassword(),userResponseDto.password());
        Assertions.assertEquals(givenUser.getEmail(),userResponseDto.email());
        Assertions.assertEquals(givenUser.getBirthDay(),userResponseDto.birthDay());

        verify(userRepository, times(1)).findByLoginId("kang4746");
    }

//    @Test
//    @DisplayName("given_when_then 방식으로 signUp 서비스 성공 테스트")
//    public void signUp() {
//        //given
//        UserRequestDto givenRequest = UserRequestDto.builder()
//                .name("홍길순")
//                .loginId("kang4746")
//                .password("test1234")
//                .email("strong@gmail.com")
//                .birthDay("1999-10-29")
//                .build();
//
//
//        //userRepository.save(any(User.class)) 메서드가 호출되면
//        // 첫 번째 전달된 User 객체를 반환하도록(Mockito의 returnsFirstArg() 메서드를 사용하여) 설정하는 것입니다.
//        Mockito.when(userRepository.save(any(User.class)))
//                .then(returnsFirstArg());
//        //when
//        UserResponseDto userResponseDto = userService.signUp(givenRequest);
//
//        //then
//        Assertions.assertEquals(givenRequest.name(),userResponseDto.name());
//        Assertions.assertEquals(givenRequest.loginId(),userResponseDto.loginId());
//        Assertions.assertEquals(passwordEncoder.encode(givenRequest.password()),userResponseDto.password());
//        Assertions.assertEquals(givenRequest.email(),userResponseDto.email());
//        Assertions.assertEquals(givenRequest.birthDay(),userResponseDto.birthDay());
//
//        verify(userRepository,times(1)).save(any());
//    }

    @Test
    @DisplayName("given_when_then 방식으로 signIn 서비스 성공 테스트")
    public void signIn(){
        //given
        SignInRequestDto signInRequestDto = SignInRequestDto.builder()
                .loginId("kang4746")
                .password("test1234")
                .build();

        Mockito.when(userRepository.findByLoginId("kang4746"))
                .thenReturn(Optional.of(givenUser));
        Mockito.when(passwordEncoder.matches(signInRequestDto.password(), givenUser.getPassword()))
                .thenReturn(true);
        Mockito.when(jwtProvider.createAccessToken(givenUser.getLoginId(), givenUser.getRoles()))
                .thenReturn("mockToken");

        //when
        SignInResponseDto signInResponseDto = userService.signIn(signInRequestDto);

        //then
        Assertions.assertEquals("mockToken",signInResponseDto.accessToken());
        Assertions.assertEquals(signInRequestDto.loginId(),givenUser.getLoginId());
        Assertions.assertEquals(signInRequestDto.password(),givenUser.getPassword());

        verify(userRepository,times(1)).findByLoginId("kang4746");
        verify(passwordEncoder,times(1)).matches(signInRequestDto.password(),givenUser.getPassword());
        verify(jwtProvider,times(1)).createAccessToken(givenUser.getLoginId(),givenUser.getRoles());

    }


    @Test
    @DisplayName("given_when_then 방식으로 deleteUser 서비스 성공 테스트")
    public void deleteUser() {
        //given
        User user = spy(givenUser);
        given(user.getId()).willReturn(1L);

        Mockito.when(userRepository.findByLoginId("kang4746"))
                .thenReturn(Optional.of(user));
        //when
        userService.deleteUser("kang4746");

        //then
        verify(userRepository,times(1)).findByLoginId("kang4746");
        verify(userRepository,times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("given_when_then 방식으로 updateUser 서비스 성공 테스트")
    public void updateUser() {
        //given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .loginId("jun4746")
                .name("string")
                .email("strong@naver.com")
                .birthDay("string")
                .build();

        Mockito.when(userRepository.findByLoginId("kang4746"))
                .thenReturn(Optional.of(givenUser));
        Mockito.when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());

        //when
        UserResponseDto userResponseDto = userService.updateUser("kang4746",userUpdateDto);

        //then
        Assertions.assertEquals("홍길순",userResponseDto.name());
        Assertions.assertEquals("jun4746",userResponseDto.loginId());
        Assertions.assertEquals("strong@naver.com",userResponseDto.email());
        Assertions.assertEquals("1999-10-29",userResponseDto.birthDay());

        verify(userRepository,times(1)).findByLoginId("kang4746");
        verify(userRepository,times(1)).save(any());
    }
}