package me.snaptime.user.controller;

import com.google.gson.Gson;
import me.snaptime.config.SecurityConfig;
import me.snaptime.jwt.JwtProvider;
import me.snaptime.jwt.UserDetailsServiceImpl;
import me.snaptime.profile.service.impl.ProfileServiceImpl;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.dto.req.UserUpdateReqDto;
import me.snaptime.user.dto.res.UserFindResDto;
import me.snaptime.user.service.impl.SignServiceImpl;
import me.snaptime.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private SignServiceImpl signService;

    @MockBean
    private ProfileServiceImpl profileService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    //프로퍼티 값 주입을 위함. accessTokenValidTime, refreshTokenValidTime
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @WithMockUser(username = "kang@gmail.com",password = "test1234",roles = "USER")
    @DisplayName("유저 정보 조회 컨트롤러 테스트")
    void getUserTest() throws Exception{

        //given
        given(userService.getUser("kang@gmail.com")).willReturn(
                UserFindResDto.builder()
                        .name("홍길순")
                        .email("kang@gmail.com")
                        .nickName("kangg")
                        .build());

        //when
        mockMvc.perform(get("/users/my"))
                .andExpect(status().isOk())
                //json response 형식을 잘 봅시다.
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result.name").exists())
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.nickName").exists())
                .andDo(print());

        //then
        verify(userService,times(1)).getUser("kang@gmail.com");
    }

    @Test
    @DisplayName("유저 회원가입 컨트롤러 테스트")
    void signUpTest() throws Exception{

        //given
        UserReqDto userRequestDto = UserReqDto.builder()
                .email("kang@gmail.com")
                .password("test1234")
                .name("홍길순")
                .build();

        given(signService.signUp(any(UserReqDto.class)))
                .willReturn(UserFindResDto.builder()
                        .userId(1L)
                        .name("홍길순")
                        .email("kang@gmail.com")
                        .nickName("kangg")
                        .build());

        Gson gson = new Gson();
        String content = gson.toJson(userRequestDto);

        //when
        mockMvc.perform(post("/users/sign-up").content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result.userId").exists())
                .andExpect(jsonPath("$.result.name").exists())
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.nickName").exists())
                .andDo(print());

        //then
        verify(signService,times(1)).signUp(any(UserReqDto.class));
    }

    @Test
    @WithMockUser(username = "kang@gmail.com",password = "test1234",roles = "USER")
    @DisplayName("유저 수정 테스트")
    void updateUserTest() throws Exception{

        //given
        UserUpdateReqDto userUpdateDto = UserUpdateReqDto.builder()
                .name("홍길순")
                .nickName("kanghj")
                .build();

        given(userService.updateUser(eq("kang@gmail.com"),any(UserUpdateReqDto.class)))
                .willReturn(UserFindResDto.builder()
                        .name("홍길순")
                        .email("kang@gmail.com")
                        .nickName("kanghj")
                        .build());

        Gson gson = new Gson();
        String content = gson.toJson(userUpdateDto);

        //when
        mockMvc.perform(patch("/users")
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.nickName").exists())
                .andDo(print());

        //then
        verify(userService,times(1)).updateUser(eq("kang@gmail.com"),any(UserUpdateReqDto.class));
    }

    @Test
    @WithMockUser(username = "kang@gmail.com",password = "test1234",roles = "USER")
    @DisplayName("유저 삭제 테스트")
    void deleteUserTest() throws Exception{
        //given
        //when
        mockMvc.perform(delete("/users").param("password", "test1234"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService,times(1)).deleteUser("kang@gmail.com","test1234");
    }
}
