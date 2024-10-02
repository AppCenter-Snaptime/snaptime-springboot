package me.snaptime.snap.repository;

import com.querydsl.core.Tuple;
import me.snaptime.album.domain.Album;
import me.snaptime.album.repository.AlbumRepository;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.config.JpaAuditingConfig;
import me.snaptime.config.QueryDslConfig;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.domain.Friend;
import me.snaptime.friend.repository.FriendRepository;
import me.snaptime.profilePhoto.domain.ProfilePhoto;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.AssertionErrors.fail;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SnapPagingRepositoryImplTest {

    @Autowired
    private SnapRepository snapRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private AlbumRepository albumRepository;
    private User reqUser;

    @MockBean
    private UrlComponent urlComponent;

    @BeforeEach
    void init(){
        Album album = Album.builder()
                .name("testAlbum")
                .build();
        albumRepository.save(album);
        ProfilePhoto profilePhoto1 = ProfilePhoto.builder()
                .profilePhotoPath("testPath")
                .profilePhotoName("testProfileName1")
                .build();
        ProfilePhoto profilePhoto2 = ProfilePhoto.builder()
                .profilePhotoPath("testPath")
                .profilePhotoName("testProfileName2")
                .build();
        ProfilePhoto profilePhoto3 = ProfilePhoto.builder()
                .profilePhotoPath("testPath")
                .profilePhotoName("testProfileName3")
                .build();
        ProfilePhoto profilePhoto4 = ProfilePhoto.builder()
                .profilePhotoPath("testPath")
                .profilePhotoName("testProfileName4")
                .build();

        reqUser = User.builder()
                .email("test1@google.com")
                .password("1234")
                .name("testName1")
                .nickName("test1g")
                .profilePhoto(profilePhoto1)
                .build();
        User user2 = User.builder()
                .email("test2@google.com")
                .password("1234")
                .name("testName2")
                .nickName("test2g")
                .profilePhoto(profilePhoto2)
                .build();
        User user3 = User.builder()
                .email("test3@google.com")
                .password("1234")
                .name("testName3")
                .nickName("test3g")
                .profilePhoto(profilePhoto3)
                .build();
        User user4 = User.builder()
                .email("test4@google.com")
                .password("1234")
                .name("testName4")
                .nickName("test4g")
                .profilePhoto(profilePhoto4)
                .build();

        userRepository.saveAll(List.of(reqUser,user2,user3,user4));

        Friend friend1 = Friend.builder()
                .sender(reqUser)
                .receiver(user2)
                .build();
        Friend friend2 = Friend.builder()
                .sender(reqUser)
                .receiver(user3)
                .build();

        friendRepository.saveAll(List.of(friend1, friend2));
        createSnap(reqUser,album,false);
        createSnap(reqUser,album,true);
        createSnap(reqUser,album,false);
        createSnap(reqUser,album,true);
        createSnap(user2,album,false);
        createSnap(user2,album,true);
        createSnap(user3,album,false);
        createSnap(user3,album,true);
        createSnap(user4,album,false);

    }

    @Test
    @DisplayName("snap 페이징 조회 테스트 : 성공")
    public void findSnapPagingTest1(){
        // given

        // when
        List<Tuple> result = snapRepository.findSnapPage(1L,reqUser);

        // then
        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("snap 페이징 조회 테스트 : 실패(존재하지 않는 페이지)")
    public void findSnapPagingTest2(){
        // given

        // when,then
        try{
            snapRepository.findSnapPage(10L,reqUser);
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PAGE_NOT_FOUND);
        }
    }

    private void createSnap(User user, Album album,boolean isPrivate){
        snapRepository.save(
                Snap.builder()
                        .album(album)
                        .isPrivate(isPrivate)
                        .oneLineJournal("1줄일기")
                        .user(user)
                        .fileName("fileName")
                        .filePath("testPath")
                        .fileType("testType")
                        .build()
        );
    }
}
