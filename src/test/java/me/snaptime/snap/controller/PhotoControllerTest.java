package me.snaptime.snap.controller;

import me.snaptime.snap.service.impl.SnapServiceImpl;
import me.snaptime.user.dto.req.UserReqDto;
import me.snaptime.user.service.SignService;
import me.snaptime.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = PhotoController.class)
public class PhotoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SnapServiceImpl snapService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private SignService signService;


    @DisplayName("Photo 조회 테스트")
    @WithMockUser(username = "kang@gmail.com",password = "test1234",roles = "USER")
    @Test
    public void findPhotoTest() throws Exception {
        // given
        signService.signUp(new UserReqDto(
                "김원정", "kang@gmail.com", "test1234"));
        byte[] emptyByte = {};

        given(snapService.downloadPhotoFromFileSystem("image.png", "kang@gmail.com", true)).willReturn(emptyByte);
        // when
        mockMvc.perform(
                get("/photo?fileName=image.png&isEncrypted=true"))
                .andExpect(status().isOk())
                .andDo(print());
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
