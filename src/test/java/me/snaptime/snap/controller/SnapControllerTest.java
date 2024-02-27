package me.snaptime.snap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.snaptime.snap.data.controller.SnapController;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.service.SnapService;
import me.snaptime.user.data.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SnapController.class)
public class SnapControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SnapService snapService;

    String testImagePath = "test_resource/image.jpg";
    ClassPathResource resource = new ClassPathResource(testImagePath);

    @DisplayName("Snap 저장하기 테스트")
    @Test
    public void createSnapTest() throws Exception {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("name", "filename1.jpg", "image/png", resource.getInputStream().readAllBytes());
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("oneLineJournal", "한 줄 일기");
        formData.add("album", "");
        // when
        mockMvc.perform(MockMvcRequestBuilders.multipart("/snap")
                        .file(multipartFile)
                        .params(formData)
        ).andExpect(status().isCreated())
                .andDo(print());
        // then
        verify(snapService).createSnap(any(CreateSnapReqDto.class), eq("abcd"));
    }

    @DisplayName("Snap 가져오기 테스트")
    @Test
    public void findSnapTest() throws Exception {
        // given
        Snap expectedSnap = Snap.builder()
                .id(1L)
                .album(null)
                .photo(Photo.builder()
                        .fileName("파일 이름")
                        .id(1L)
                        .filePath("/path")
                        .fileType("image/png")
                        .build())
                .user(User.builder()
                        .id(1L)
                        .password("1234")
                        .name("김원정")
                        .email("test@test.com")
                        .birthDay("990303")
                        .loginId("test")
                        .build())
                .oneLineJournal("한 줄 일기")
                .build();
        FindSnapResDto findSnapResDto = FindSnapResDto.entityToResDto(expectedSnap);
        given(snapService.findSnap(1L)).willReturn(findSnapResDto);
        // when
        mockMvc.perform(get("/snap/" + 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.result").exists())
                .andDo(print());
        // then
        verify(snapService).findSnap(1L);
    }
}
