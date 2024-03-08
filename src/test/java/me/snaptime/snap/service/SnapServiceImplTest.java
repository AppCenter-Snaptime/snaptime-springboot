package me.snaptime.snap.service;

import me.snaptime.snap.component.EncryptionComponent;
import me.snaptime.snap.component.FileComponent;
import me.snaptime.snap.data.domain.Encryption;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.EncryptionRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.service.impl.SnapServiceImpl;
import me.snaptime.snap.util.EncryptionUtil;
import me.snaptime.user.data.domain.User;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class SnapServiceImplTest {
    @Mock
    private FileComponent fileComponent;

    @Mock
    private SnapRepository snapRepository;

    @Mock
    private EncryptionComponent encryptionComponent;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private EncryptionRepository encryptionRepository;

    @InjectMocks
    private SnapServiceImpl snapServiceImpl;

    String testImagePath = "test_resource/image.jpg";
    ClassPathResource resource = new ClassPathResource(testImagePath);

    @DisplayName("스냅 저장 테스트")
    @Test
    @Transactional
    public void createSnapTest() throws Exception {
        // given
        MultipartFile givenMultipartFile = new MockMultipartFile(
                "image", "image", "image/jpeg", resource.getInputStream().readAllBytes()
        );
        CreateSnapReqDto givenCreateSnapReqDto = new CreateSnapReqDto(
            "한 줄 일기", givenMultipartFile, ""
        );
        boolean givenPrivate = true;
        User expectedUser = spy(User.builder()
                .loginId("abcd")
                .email("test@test.com")
                .birthDay("990303")
                .password("1234")
                .name("김원정")
                .build());
        Photo expectedPhoto = spy(Photo.builder()
                .filePath("/image/image.png")
                .fileName("image")
                .fileType("image/jpeg")
                .build());
        Snap expectedSnap = spy(Snap.builder()
                .isPrivate(givenPrivate)
                .album(null)
                .oneLineJournal(givenCreateSnapReqDto.oneLineJournal())
                .build());
        Encryption expectedEncryption = spy(Encryption.builder()
                .user(expectedUser)
                .secretKey(EncryptionUtil.generateAESKey())
                .build());
        given(userRepository.findByLoginId("abcd")).willReturn(Optional.ofNullable(expectedUser));
        given(snapRepository.save(Mockito.any(Snap.class))).willReturn(expectedSnap);
        given(encryptionComponent.setEncryption(expectedUser)).willReturn(expectedEncryption);
        // when
        snapServiceImpl.createSnap(givenCreateSnapReqDto, "abcd", givenPrivate);
        // then
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
