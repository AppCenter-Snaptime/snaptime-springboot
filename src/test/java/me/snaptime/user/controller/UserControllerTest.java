package me.snaptime.user.controller;

import com.google.gson.Gson;
import me.snaptime.common.config.SecurityConfig;
import me.snaptime.common.jwt.JwtProvider;
import me.snaptime.user.data.dto.request.UserReqDto;
import me.snaptime.user.data.dto.request.UserUpdateDto;
import me.snaptime.user.data.dto.response.UserResDto;
import me.snaptime.user.service.UserDetailsServiceImpl;
import me.snaptime.user.service.UserService;
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
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(username = "kang4746",password = "test1234",roles = "USER")
    @DisplayName("유저 정보 조회 컨트롤러 테스트")
    void getUserTest() throws Exception{

        //given
        given(userService.getUser("kang4746")).willReturn(
                UserResDto.builder()
                        .loginId("kang4746")
                        .email("strong@gmail.com")
                        .birthDay("1999-10-29")
                        .build());

        //when
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                //json response 형식을 잘 봅시다.
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result.loginId").exists())
                .andExpect(jsonPath("$.result.password").exists())
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.birthDay").exists())
                .andDo(print());

        //then
        verify(userService,times(1)).getUser("kang4746");
    }

    @Test
    @DisplayName("유저 회원가입 컨트롤러 테스트")
    void signUpTest() throws Exception{

        //given
        UserReqDto userRequestDto = UserReqDto.builder()
                .loginId("kang4746")
                .password("test1234")
                .name("홍길순")
                .email("strong@gmail.com")
                .birthDay("1999-10-29")
                .build();

        given(userService.signUp(any(UserReqDto.class)))
                .willReturn(UserResDto.builder()
                        .id(1L)
                        .loginId("kang4746")
                        .name("홍길순")
                        .email("strong@gmail.com")
                        .birthDay("1999-10-29")
                        .build());

        Gson gson = new Gson();
        String content = gson.toJson(userRequestDto);

        //when
        mockMvc.perform(post("/users/sign-up").content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.loginId").exists())
                .andExpect(jsonPath("$.result.password").exists())
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.birthDay").exists())
                .andDo(print());

        //then
        verify(userService,times(1)).signUp(any(UserReqDto.class));
    }

    @Test
    @WithMockUser(username = "kang4746",password = "test1234",roles = "USER")
    @DisplayName("유저 수정 테스트")
    void updateUserTest() throws Exception{

        //given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .loginId("jun4746")
                .name("")
                .email("strong@naver.com")
                .birthDay("")
                .build();

        given(userService.updateUser(eq("kang4746"),any(UserUpdateDto.class)))
                .willReturn(UserResDto.builder()
                        .loginId("kang4746")
                        .name("홍길순")
                        .email("strong@gmail.com")
                        .birthDay("1999-10-29")
                        .build());

        Gson gson = new Gson();
        String content = gson.toJson(userUpdateDto);

        //when
        mockMvc.perform(put("/users")
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result.loginId").exists())
                .andExpect(jsonPath("$.result.password").exists())
                .andExpect(jsonPath("$.result.email").exists())
                .andExpect(jsonPath("$.result.birthDay").exists())
                .andDo(print());

        //then
        verify(userService,times(1)).updateUser(eq("kang4746"),any(UserUpdateDto.class));
    }

    @Test
    @WithMockUser(username = "kang4746",password = "test1234",roles = "USER")
    @DisplayName("유저 삭제 테스트")
    void deleteUserTest() throws Exception{
        //given
        //when
        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService,times(1)).deleteUser("kang4746");
    }
}
