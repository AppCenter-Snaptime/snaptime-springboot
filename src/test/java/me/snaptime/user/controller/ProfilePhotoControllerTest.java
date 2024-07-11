package me.snaptime.user.controller;


import me.snaptime.profilePhoto.domain.ProfilePhoto;
import me.snaptime.profilePhoto.service.ProfilePhotoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfilePhoto.class)
public class ProfilePhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ProfilePhotoService profilePhotoService;

    @Test
    @DisplayName("프로필 사진 업로드 컨트롤러 테스트")
    public void uploadProfileToFileSystemTest() throws Exception {

    }

}
