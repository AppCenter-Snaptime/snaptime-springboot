package me.snaptime.snap.repository;

import com.querydsl.core.Tuple;
import me.snaptime.common.config.JpaAuditingConfig;
import me.snaptime.common.config.QueryDslConfig;
import me.snaptime.snap.data.domain.Album;
import me.snaptime.snap.data.domain.Photo;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.PhotoRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.data.repository.impl.SnapPagingRepositoryImpl;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.social.data.repository.FriendShipRepository;
import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.user.data.domain.QUser.user;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
@TestPropertySource(locations = "classpath:application-test.yml")
public class SnapPagingRepositoryImplTest {

    @Autowired
    private SnapRepository snapRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendShipRepository friendShipRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private SnapPagingRepositoryImpl snapPagingRepository;
    @Autowired
    private PhotoRepository photoRepository;
    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    void init() {
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

        user1 = User.builder()
                .email("test1@google.com")
                .loginId("testLoginId1")
                .name("testName1")
                .password("1234")
                .birthDay(String.valueOf(LocalDateTime.now()))
                .profilePhoto(profilePhoto1)
                .build();
        user2 = User.builder()
                .email("test2@google.com")
                .loginId("testLoginId2")
                .name("testName2")
                .password("1234")
                .birthDay(String.valueOf(LocalDateTime.now()))
                .profilePhoto(profilePhoto2)
                .build();
        user3 = User.builder()
                .email("test3@google.com")
                .loginId("testLoginId3")
                .name("testName3")
                .password("1234")
                .birthDay(String.valueOf(LocalDateTime.now()))
                .profilePhoto(profilePhoto3)
                .build();
        user4 = User.builder()
                .email("test4@google.com")
                .loginId("testLoginId4")
                .name("testName4")
                .password("1234")
                .birthDay(String.valueOf(LocalDateTime.now()))
                .profilePhoto(profilePhoto4)
                .build();

        userRepository.saveAll(List.of(user1,user2,user3,user4));

        FriendShip friendShip1 = FriendShip.builder()
                .fromUser(user1)
                .toUser(user2)
                .friendStatus(FriendStatus.FOLLOW)
                .build();
        FriendShip friendShip2 = FriendShip.builder()
                .fromUser(user1)
                .toUser(user3)
                .friendStatus(FriendStatus.WAITING)
                .build();
        FriendShip friendShip3 = FriendShip.builder()
                .fromUser(user1)
                .toUser(user4)
                .friendStatus(FriendStatus.REJECTED)
                .build();
        friendShipRepository.saveAll(List.of(friendShip1,friendShip2,friendShip3));

        List<Snap> snaps = new ArrayList<>();
        List<User> users = new ArrayList<>(List.of(user1,user1,user1,user2,user4,user2,user3,user3,user3,user3,user4));
        for (int i = 1; i <= 11; i++) {
            Photo photo = Photo.builder()
                    .fileName("testFileName"+i)
                    .filePath("testFilePath"+i)
                    .fileType("testType")
                    .build();
            photoRepository.save(photo);
            Snap snap = Snap.builder()
                    .album(album)
                    .oneLineJournal("snap" + i + " 1줄일기")
                    .user(users.get(i-1))
                    .photo(photo)
                    .build();
            snaps.add(snap);
        }
        snapRepository.saveAll(snaps);
    }

    @Test
    @DisplayName("snap 페이징 조회 테스트 : 성공")
    public void findSnapPagingTest1(){
        // given

        // when
        List<Tuple> result = snapPagingRepository.findSnapPaging("testLoginId1",1L,user1);

        // then
        assertThat(result.size()).isEqualTo(9l);
        assertThat(result.get(0).get(snap.Id)).isEqualTo(10);
        assertThat(result.get(1).get(snap.Id)).isEqualTo(9);
        assertThat(result.get(2).get(snap.Id)).isEqualTo(8);
        assertThat(result.get(3).get(snap.Id)).isEqualTo(7);
        assertThat(result.get(4).get(snap.Id)).isEqualTo(6);
        assertThat(result.get(5).get(snap.Id)).isEqualTo(4);
        assertThat(result.get(6).get(snap.Id)).isEqualTo(3);
        assertThat(result.get(7).get(snap.Id)).isEqualTo(2);
        assertThat(result.get(8).get(snap.Id)).isEqualTo(1);

        assertThat(result.get(0).get(snap.photo.id)).isEqualTo(10);
        assertThat(result.get(1).get(snap.photo.id)).isEqualTo(9);
        assertThat(result.get(2).get(snap.photo.id)).isEqualTo(8);
        assertThat(result.get(3).get(snap.photo.id)).isEqualTo(7);
        assertThat(result.get(4).get(snap.photo.id)).isEqualTo(6);
        assertThat(result.get(5).get(snap.photo.id)).isEqualTo(4);
        assertThat(result.get(6).get(snap.photo.id)).isEqualTo(3);
        assertThat(result.get(7).get(snap.photo.id)).isEqualTo(2);
        assertThat(result.get(8).get(snap.photo.id)).isEqualTo(1);

        assertThat(result.get(0).get(user.name)).isEqualTo("testName3");
        assertThat(result.get(1).get(user.name)).isEqualTo("testName3");
        assertThat(result.get(2).get(user.name)).isEqualTo("testName3");
        assertThat(result.get(3).get(user.name)).isEqualTo("testName3");
        assertThat(result.get(4).get(user.name)).isEqualTo("testName2");
        assertThat(result.get(5).get(user.name)).isEqualTo("testName2");
        assertThat(result.get(6).get(user.name)).isEqualTo("testName1");
        assertThat(result.get(7).get(user.name)).isEqualTo("testName1");
        assertThat(result.get(8).get(user.name)).isEqualTo("testName1");
    }
}
