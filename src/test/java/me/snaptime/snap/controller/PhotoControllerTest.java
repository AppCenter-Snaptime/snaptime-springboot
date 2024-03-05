package me.snaptime.snap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.snaptime.snap.data.controller.PhotoController;
import me.snaptime.snap.service.PhotoService;
import me.snaptime.snap.util.EncryptionUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhotoController.class)
public class PhotoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PhotoService photoService;

    private final Long givenId = 1L;

    String testImagePath = "test_resource/image.jpg";
    ClassPathResource resource = new ClassPathResource(testImagePath);

    @DisplayName("Photo 가져오기 테스트")
    @Test
    public void findPhoto() throws Exception {
        SecretKey secretKey = EncryptionUtil.generateAESKey();
        given(photoService.downloadPhotoFromFileSystem(givenId, secretKey)).willReturn(
                resource.getInputStream().readAllBytes()
        );
        mockMvc.perform(
                get("/photo?id="+givenId+"&uId=abcd")
        ).andExpect(status().isOk())
                .andDo(print());

        verify(photoService).downloadPhotoFromFileSystem(givenId, secretKey);
    }
}
