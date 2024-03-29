package me.snaptime.user.service;

import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.response.ProfilePhotoResDto;
import me.snaptime.user.data.repository.ProfilePhotoRepository;
import me.snaptime.user.data.repository.UserRepository;
import me.snaptime.user.util.ProfilePhotoNameGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfilePhotoServiceTest {

    @InjectMocks
    private ProfilePhotoService profilePhotoService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfilePhotoRepository profilePhotoRepository;
    private String deleteFilePath;

    private User givenUser;
    private ProfilePhoto profilePhoto;

    private String FOLDER_PATH = "null";

    @BeforeEach
    public void setUpTest(){
        profilePhoto = ProfilePhoto.builder().build();

        givenUser =  User.builder().build();
    }

    //테스트 코드 실행 후, root 폴더에 null + 생성시간 파일이 생성됨
    @AfterEach
    public void deleteFile(){
        File file = new File(deleteFilePath);

        if(file.exists()){
            file.delete();
        }
    }

    @Test
    @DisplayName("given_when_then 방식으로 프로필 사진 수정 서비스 성공 테스트")
    void updatePhotoToFileSystemTest() throws Exception {
        //given
        /*mock객체로 upload할 파일 생성*/
        User mockuser = spy(givenUser);
        ProfilePhoto mockProfile = spy(profilePhoto);

        MultipartFile updateFile = new MockMultipartFile(
                "profile",
                "profile",
                "profile/jpeg",
                new byte[]{});

        String fileName = ProfilePhotoNameGenerator.generatorProfilePhotoName(updateFile.getOriginalFilename());
        String filePath = FOLDER_PATH + fileName;

        /*업데이트 할 프로필 사진*/
        ProfilePhoto updateProfilePhoto = ProfilePhoto.builder()
                .profilePhotoName(updateFile.getOriginalFilename())
                .profilePhotoPath(filePath)
                .build();

        given(mockuser.getProfilePhoto()).willReturn(mockProfile);
        given(mockProfile.getId()).willReturn(1L);
        given(mockProfile.getProfilePhotoName()).willReturn("image");
        given(mockProfile.getProfilePhotoPath()).willReturn("/image");

        Mockito.when(userRepository.findByLoginId("kang4746"))
                .thenReturn(Optional.of(mockuser));
        Mockito.when(profilePhotoRepository.findById(1L))
                .thenReturn(Optional.of(mockProfile));
        Mockito.when(profilePhotoRepository.save(any(ProfilePhoto.class))).thenReturn(updateProfilePhoto);

        //when
        ProfilePhotoResDto responseDto = profilePhotoService.updatePhotoFromFileSystem("kang4746", updateFile);

        //then
        Assertions.assertEquals(updateProfilePhoto.getProfilePhotoName(),responseDto.profilePhotoName());
        Assertions.assertEquals(updateProfilePhoto.getProfilePhotoPath(),responseDto.profilePhotoPath());

        verify(userRepository,times(1)).findByLoginId("kang4746");
        verify(profilePhotoRepository,times(1)).findById(1L);
        verify(profilePhotoRepository,times(1)).save(any());

        //생성된 null 파일을 지우기 위해 경로를 저장
        //after
        deleteFilePath = responseDto.profilePhotoPath();
    }


//    @Test
//    @DisplayName("given_when_then 방식으로 프로필 사진 upload 서비스 성공 테스트")
//    void uploadPhotoToFileSystemTest() throws Exception {
//        //given
//        /*mock객체로 upload할 파일 생성*/
//        MultipartFile uploadFile = new MockMultipartFile(
//                "profile",
//                "profile",
//                "profile/jpeg",
//                new byte[]{});
//
//        String fileName = ProfilePhotoNameGenerator.generatorProfilePhotoName(uploadFile.getOriginalFilename());
//        String filePath = FOLDER_PATH + fileName;
//
//        ProfilePhoto givenProfilePhoto = ProfilePhoto.builder()
//                .user(givenUser)
//                .profilePhotoName(uploadFile.getOriginalFilename())
//                .profilePhotoPath(filePath)
//                .build();
//
//        Long uploadUserId = 1L;
//        /*업로드를 위한 유저 조회*/
//        Mockito.when(userRepository.findById(uploadUserId))
//                .thenReturn(Optional.of(givenUser));
//        /*프로필 저장*/
//        Mockito.when(profilePhotoRepository.save(any(ProfilePhoto.class)))
//                .thenReturn(givenProfilePhoto);
//
//        //when
//        ProfilePhotoResponseDto responseDto = profilePhotoService.uploadPhotoToFileSystem(uploadUserId, uploadFile);
//
//        //then
//        Assertions.assertEquals(givenProfilePhoto.getProfilePhotoName(),responseDto.profilePhotoName());
//        Assertions.assertEquals(givenProfilePhoto.getProfilePhotoPath(),responseDto.profilePhotoPath());
//        Assertions.assertEquals(givenProfilePhoto.getUser().getId(),responseDto.userId());
//
//        verify(userRepository, times(1)).findById(uploadUserId);
//        verify(profilePhotoRepository,times(1)).save(any());
//
//        //생성된 null 파일을 지우기 위해 경로를 저장
//        //after
//        deleteFilePath = responseDto.profilePhotoPath();
//    }

    /* 구현 실패 */
//    @Test
//    @DisplayName("given_when_then 방식으로 프로필 사진 삭제 서비스 성공 테스트")
//    void deletePhotoFromFileSystem() throws Exception {
//        //given
//        MultipartFile deleteFile = new MockMultipartFile(
//                "profile",
//                "profile",
//                "profile/jpeg",
//                resource.getInputStream().readAllBytes());
//
//        String fileName = FileNameGenerator.generatorName(deleteFile.getOriginalFilename());
//        //FOLDER_PATH 가 Null이다.
//        String filePath = FOLDER_PATH + fileName;
//        deleteFile.transferTo(new File(filePath));
//
//        /*삭제 할 프로필 사진*/
//        ProfilePhoto deleteProfilePhoto = ProfilePhoto.builder()
//                .Id(1L)
//                .user(givenUser)
//                .profilePhotoName(deleteFile.getOriginalFilename())
//                .profilePhotoPath(filePath)
//                .build();
//
//        Mockito.when(userRepository.findById(1L))
//                .thenReturn(Optional.of(givenUser));
//        Mockito.when(profilePhotoRepository.findProfilePhotoByUser(givenUser)).thenReturn(Optional.of(deleteProfilePhoto));
//
//        //when
//        profilePhotoService.deletePhotoFromFileSystem(1L);
//
//        //then
//
//        verify(userRepository,times(1)).findById(1L);
//        verify(profilePhotoRepository,times(1)).findProfilePhotoByUser(givenUser);
//        verify(profilePhotoRepository,times(1)).deleteById(1L);
//    }
}
