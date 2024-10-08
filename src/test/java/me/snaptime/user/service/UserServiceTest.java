package me.snaptime.user.service;

import me.snaptime.album.service.AlbumService;
import me.snaptime.jwt.JwtProvider;
import me.snaptime.jwt.redis.RefreshToken;
import me.snaptime.jwt.redis.RefreshTokenRepository;
import me.snaptime.profilePhoto.domain.ProfilePhoto;
import me.snaptime.profilePhoto.repository.ProfilePhotoRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.dto.req.SignInReqDto;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.SignInResDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.user.service.impl.SignServiceImpl;
import me.snaptime.user.service.impl.UserServiceImpl;
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
    private UserServiceImpl userService;

    @InjectMocks
    private SignServiceImpl signService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfilePhotoRepository profilePhotoRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AlbumService albumService;

    private User givenUser;

    @BeforeEach
    public void setUpTestSet() {
        givenUser = User.builder()
                .name("홍길순")
                .email("kang@gmail.com")
                .password("test1234")
                .nickName("kangg")
                .build();
    }

    @Test
    @DisplayName("given_when_then 방식으로 getUser 서비스 성공 테스트")
    public void getUser() {
        //given
        Mockito.when(userRepository.findByEmail("kang@gmail.com"))
                .thenReturn(Optional.of(givenUser));
        //when
        UserFindResDto userFindResDto = userService.getUser("kang@gmail.com");

        //then
        Assertions.assertEquals(givenUser.getUserId(), userFindResDto.userId());
        Assertions.assertEquals(givenUser.getName(), userFindResDto.name());
        Assertions.assertEquals(givenUser.getEmail(), userFindResDto.email());
        Assertions.assertEquals(givenUser.getNickName(), userFindResDto.nickName());

        verify(userRepository, times(1)).findByEmail("kang@gmail.com");
    }

    @Test
    @DisplayName("given_when_then 방식으로 signUp 서비스 성공 테스트")
    public void signUp() {
        //given
        UserReqDto givenRequest = UserReqDto.builder()
                .name("홍길순")
                .email("kang@gmail.com")
                .password("test1234")
                .build();


        //userRepository.save(any(User.class)) 메서드가 호출되면
        // 첫 번째 전달된 User 객체를 반환하도록(Mockito의 returnsFirstArg() 메서드를 사용하여) 설정하는 것입니다.
        Mockito.when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());
        Mockito.when(profilePhotoRepository.save(any(ProfilePhoto.class)))
                .then(returnsFirstArg());
        given(albumService.createNonClassificationAlbum(any(User.class))).willReturn(1L);
        //when
        UserFindResDto userFindResDto = signService.signUp(givenRequest);

        //then
        Assertions.assertEquals(givenRequest.name(), userFindResDto.name());
        Assertions.assertEquals(givenRequest.email(), userFindResDto.email());

        verify(userRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("given_when_then 방식으로 signIn 서비스 성공 테스트")
    public void signIn(){
        //given
        SignInReqDto signInReqDto = SignInReqDto.builder()
                .email("kang@gmail.com")
                .password("test1234")
                .build();

        Mockito.when(userRepository.findByEmail("kang@gmail.com"))
                .thenReturn(Optional.of(givenUser));
        Mockito.when(passwordEncoder.matches(signInReqDto.password(), givenUser.getPassword()))
                .thenReturn(true);
        Mockito.when(jwtProvider.createAccessToken(givenUser.getUserId(),givenUser.getEmail(), givenUser.getRoles()))
                .thenReturn("mockAccessToken");
        Mockito.when(jwtProvider.createRefreshToken(givenUser.getUserId(),givenUser.getEmail(), givenUser.getRoles()))
                .thenReturn("mockRefreshToken");
        Mockito.when(refreshTokenRepository.save(any(RefreshToken.class)))
                .then(returnsFirstArg());

        //when
        SignInResDto signInResDto = signService.signIn(signInReqDto);

        //then
        Assertions.assertEquals("mockAccessToken",signInResDto.accessToken());
        Assertions.assertEquals("mockRefreshToken",signInResDto.refreshToken());
        Assertions.assertEquals(signInReqDto.email(),givenUser.getEmail());
        Assertions.assertEquals(signInReqDto.password(),givenUser.getPassword());

        verify(userRepository,times(1)).findByEmail("kang@gmail.com");
        verify(passwordEncoder,times(1)).matches(signInReqDto.password(),givenUser.getPassword());
        verify(jwtProvider,times(1)).createAccessToken(givenUser.getUserId(),givenUser.getEmail(),givenUser.getRoles());

    }


    @Test
    @DisplayName("given_when_then 방식으로 deleteUser 서비스 성공 테스트")
    public void deleteUser() {
        //given
        User user = spy(givenUser);
        given(user.getUserId()).willReturn(1L);

        Mockito.when(userRepository.findByEmail("kang@gmail.com"))
                .thenReturn(Optional.of(user));

        Mockito.when(passwordEncoder.matches("test1234", user.getPassword()))
                .thenReturn(true);

        //when
        userService.deleteUser("kang@gmail.com","test1234");

        //then
        verify(userRepository,times(1)).findByEmail("kang@gmail.com");
        verify(userRepository,times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("given_when_then 방식으로 updateUser 서비스 성공 테스트")
    public void updateUser() {
        //given
        UserUpdateReqDto userUpdateDto = UserUpdateReqDto.builder()
                .name("")
                .nickName("kanghj")
                .build();

        Mockito.when(userRepository.findByEmail("kang@gmail.com"))
                .thenReturn(Optional.of(givenUser));

        //when
        UserFindResDto userResponseDto = userService.updateUser("kang@gmail.com",userUpdateDto);

        //then
        Assertions.assertEquals("홍길순",userResponseDto.name());
        Assertions.assertEquals("kang@gmail.com",userResponseDto.email());
        Assertions.assertEquals("kanghj",userResponseDto.nickName());
        verify(userRepository,times(1)).findByEmail("kang@gmail.com");
    }
}