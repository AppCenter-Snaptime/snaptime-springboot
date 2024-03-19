package me.snaptime.snap.controller;

import me.snaptime.user.data.dto.request.UserRequestDto;
import me.snaptime.user.service.UserDetailsServiceImpl;
import me.snaptime.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = PhotoController.class)
public class PhotoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoController photoController;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserService userService;


    @DisplayName("Photo 조회 테스트")
    @WithMockUser(username = "mockUid",password = "test1234",roles = "USER")
    @Test
    public void findPhotoTest() throws Exception {
        // given
        userService.signUp(new UserRequestDto(
                "김원정", "mockUid", "test1234", "test@test.com", "990303"
        ));
        UserDetails userDetails = userDetailsService.loadUserByUsername("mockUid");
        // when
        mockMvc.perform(
                get("/photo?fileName=image.png&isEncrypted=true"))
                .andExpect(status().isOk())

                .andDo(print());
        // then
        verify(photoController).findPhoto("image.png", true, (UserDetails) SecurityMockMvcRequestPostProcessors.user(userDetails));
    }

    @DisplayName("Photo 조회 실패 테스트 (권한이 없음)")
    @Test
    public void findPhotoFailTest() throws Exception {
        // when
        mockMvc.perform(
                        get("/photo?fileName=image.png&isEncrypted=true"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

}
