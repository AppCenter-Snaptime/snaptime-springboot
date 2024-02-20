package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.repository.PhotoRepository;
import me.snaptime.snap.service.impl.PhotoServiceImpl;
import me.snaptime.snap.util.FileNameGenerator;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles
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
        MultipartFile imageFile = new MockMultipartFile(
                "image",
        "image",
                "image/jpeg",
                resource.getInputStream().readAllBytes()
        );

        String fileName = FileNameGenerator.generatorName(imageFile.getOriginalFilename());
        String filePath = FOLDER_PATH + fileName;

        Photo expectedPhoto = Photo.builder()
                .id(1L)
                .fileName(imageFile.getOriginalFilename())
                .fileType(imageFile.getContentType())
                .filePath(filePath)
                .build();

        given(photoRepository.save(Mockito.any(Photo.class))).willReturn(expectedPhoto);

        /*// when
        Photo result = photoServiceImpl.uploadPhotoToFileSystem(imageFile);

        // then
        assertEquals(expectedPhoto.getFileName(), result.getFileName());
        assertEquals(expectedPhoto.getFilePath(), result.getFilePath());
        assertEquals(expectedPhoto.getId(), result.getId());
        assertEquals(expectedPhoto.getFileType(), result.getFileType());*/

    }

//    @DisplayName("사진 다운로드 테스트")
//    @Test
//    public void downloadPhotoTest() throws IOException {
//        // given
//        Long givenId = 1L;
//        byte[] expectedPhotoByte = {};
//        Photo expectedPhoto = Photo.builder()
//                .id(1L)
//                .fileName("image")
//                .filePath("C:\\Image\\0217170430779asdasdasdsdasd.png")
//                .fileType(null)
//                .build();
//        given(photoRepository.findById(givenId)).willReturn(Optional.ofNullable(expectedPhoto));
//        // when
//        byte[] result = photoServiceImpl.downloadPhotoFromFileSystem(givenId);
//        // then
//        assertEquals(expectedPhotoByte, result);
//    }
}
