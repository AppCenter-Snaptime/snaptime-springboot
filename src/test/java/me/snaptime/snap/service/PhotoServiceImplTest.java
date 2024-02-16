package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.repository.PhotoRepository;
import me.snaptime.snap.service.impl.PhotoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceImplTest {
    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private PhotoServiceImpl photoServiceImpl;

    String testImagePath = "test_resource/image.jpg";
    ClassPathResource resource = new ClassPathResource(testImagePath);

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @DisplayName("사진 저장 테스트")
    @Test
    public void uploadPhotoTest() throws IOException {
        // given
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));

        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "image",
                "image/jpeg",
                resource.getInputStream().readAllBytes()
        );
        String filePath = FOLDER_PATH + imageFile.getOriginalFilename() + currentTime;

        Photo savedPhoto = Photo.builder()
                .id(1L)
                .fileName(imageFile.getOriginalFilename())
                .fileType(imageFile.getContentType())
                .filePath(filePath)
                .build();

        given(photoRepository.save(Mockito.any(Photo.class))).willReturn(savedPhoto);

        // when
        photoServiceImpl.uploadImageToFileSystem(imageFile);

    }
}
