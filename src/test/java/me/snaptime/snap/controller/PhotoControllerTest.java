package me.snaptime.snap.controller;

import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.service.SnapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

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
    private SnapService snapService;
    @MockBean
    private PhotoController photoController;

    String testImagePath = "test_resource/image.jpg";
    ClassPathResource resource = new ClassPathResource(testImagePath);


    @BeforeEach
    public void beforeEach() throws IOException {
        MockMultipartFile givenMockMultipartFile =
                new MockMultipartFile(
                        "image.png",
                        "/image/image.png",
                        "image/png",
                        resource.getInputStream()
                );

        CreateSnapReqDto givenCreateSnapReqDto =
                new CreateSnapReqDto(
                        "한 줄 일기",
                        givenMockMultipartFile,
                        ""
                        );
        snapService.createSnap(givenCreateSnapReqDto, "mockUid", true);
    }

    @DisplayName("Photo 조회 테스트")
    @WithMockUser(username = "mockUid",password = "test1234",roles = "USER")
    @Test
    public void findPhotoTest() throws Exception {
        // given
        // when
        mockMvc.perform(
                get("/photo?fileName=image.png&isEncrypted=true"))
                .andExpect(status().isOk())

                .andDo(print());
        // then
        verify(photoController).findPhoto("image.png", true);
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
