package me.snaptime.snap.service;

import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.service.impl.SnapServiceImpl;
import me.snaptime.snap.util.FileNameGenerator;
import me.snaptime.user.data.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SnapServiceImplTest {
    @Mock
    private PhotoService photoService;

    @Mock
    private SnapRepository snapRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private SnapServiceImpl snapServiceImpl;

    String testImagePath = "test_resource/image.jpg";
    ClassPathResource resource = new ClassPathResource(testImagePath);

    @DisplayName("스냅 저장 테스트")
    @Test
    @Transactional
    public void createSnapTest() throws IOException {
        // given
        MultipartFile givenMultipartFile = new MockMultipartFile(
                "image",
                "image",
                "image/jpeg",
                resource.getInputStream().readAllBytes()
        );

        CreateSnapReqDto givenDto = new CreateSnapReqDto(
            "한줄일기", givenMultipartFile, ""
        );

        String fileName = FileNameGenerator.generatorName(givenMultipartFile.getOriginalFilename());

        String givenUid = "test";
        Photo expectPhoto = Photo.builder()
                .id(1L)
                .fileName(fileName)
                .filePath("C:\\Image\\" + fileName)
                .fileType(givenMultipartFile.getContentType())
                .build();
        given(photoService.uploadPhotoToFileSystem(givenMultipartFile)).willReturn(expectPhoto);
        given(userRepository.findByLoginId(givenUid)).willReturn(null);
        given(albumRepository.findByName(givenDto.album())).willReturn(null);
        given(snapRepository.save(Mockito.any(Snap.class))).willReturn(
                Snap.builder()
                        .id(1L)
                        .album(null)
                        .photo(expectPhoto)
                .user(null)
                .oneLineJournal(givenDto.oneLineJournal())
                .build()
        );
        // when
        snapServiceImpl.createSnap(givenDto, givenUid);
    }

    @DisplayName("스냅 가져오기 테스트")
    @Test
    @Transactional
    public void findSnapTest() {
        // given
        Long givenId = 1L;
        Photo expectedPhoto = Photo.builder()
                .id(1L)
                        .filePath(null)
                        .fileName("fileName")
                        .fileType(null)
                        .build();
        Snap expectedSnap = Snap.builder()
                .id(1L)
                .oneLineJournal("한줄일기")
                .user(null)
                .photo(expectedPhoto)
                .album(null)
                .build();
        FindSnapResDto expectedDto = FindSnapResDto.builder()
                .id(1L)
                .oneLineJournal("한줄일기")
                .userUid(null)
                .photoId(1L)
                .albumName(null)
                .build();
        given(snapRepository.findById(givenId)).willReturn(Optional.ofNullable(expectedSnap));
        // when
        FindSnapResDto result = snapServiceImpl.findSnap(1L);
        // then
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.albumName(), result.albumName());
        assertEquals(expectedDto.userUid(), result.userUid());
        assertEquals(expectedDto.photoId(), result.photoId());
        assertEquals(expectedDto.oneLineJournal(), result.oneLineJournal());
    }
}
