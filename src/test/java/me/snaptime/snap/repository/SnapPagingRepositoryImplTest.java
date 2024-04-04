package me.snaptime.snap.repository;

import com.querydsl.core.Tuple;
import me.snaptime.common.config.JpaAuditingConfig;
import me.snaptime.common.config.QueryDslConfig;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Album;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.social.data.repository.friendShip.FriendShipRepository;
import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.user.data.domain.QUser.user;
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
    private FriendShipRepository friendShipRepository;
    @Autowired
    private AlbumRepository albumRepository;
    private User reqUser;

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
                .loginId("testLoginId1")
                .name("testName1")
                .password("1234")
                .birthDay(String.valueOf(LocalDateTime.now()))
                .profilePhoto(profilePhoto1)
                .build();
        User user2 = User.builder()
                .email("test2@google.com")
                .loginId("testLoginId2")
                .name("testName2")
                .password("1234")
                .birthDay(String.valueOf(LocalDateTime.now()))
                .profilePhoto(profilePhoto2)
                .build();
        User user3 = User.builder()
                .email("test3@google.com")
                .loginId("testLoginId3")
                .name("testName3")
                .password("1234")
                .birthDay(String.valueOf(LocalDateTime.now()))
                .profilePhoto(profilePhoto3)
                .build();
        User user4 = User.builder()
                .email("test4@google.com")
                .loginId("testLoginId4")
                .name("testName4")
                .password("1234")
                .birthDay(String.valueOf(LocalDateTime.now()))
                .profilePhoto(profilePhoto4)
                .build();

        userRepository.saveAll(List.of(reqUser,user2,user3,user4));

        FriendShip friendShip1 = FriendShip.builder()
                .fromUser(reqUser)
                .toUser(user2)
                .friendStatus(FriendStatus.FOLLOW)
                .build();
        FriendShip friendShip2 = FriendShip.builder()
                .fromUser(reqUser)
                .toUser(user3)
                .friendStatus(FriendStatus.WAITING)
                .build();
        FriendShip friendShip3 = FriendShip.builder()
                .fromUser(reqUser)
                .toUser(user4)
                .friendStatus(FriendStatus.REJECTED)
                .build();
        FriendShip friendShip4 = FriendShip.builder()
                .friendStatus(FriendStatus.FOLLOW)
                .fromUser(user3)
                .toUser(reqUser)
                .build();
        friendShipRepository.saveAll(List.of(friendShip1,friendShip2,friendShip3,friendShip4));

        List<Snap> snaps = new ArrayList<>();
        List<User> users = new ArrayList<>(List.of(reqUser,reqUser,user2,user2,user4,user2,user3,user3,user3,user3,user4));
        for (int i = 1; i <= 11; i++) {
            if(i==6){
                Snap snap = Snap.builder()
                        .album(album)
                        .isPrivate(true)
                        .oneLineJournal("snap" + i + " 1줄일기")
                        .user(users.get(i-1))
                        .fileName("fileName"+i)
                        .filePath("testPath")
                        .fileType("testType")
                        .build();
                snaps.add(snap);
            }
            else{
                Snap snap = Snap.builder()
                        .album(album)
                        .isPrivate(false)
                        .oneLineJournal("snap" + i + " 1줄일기")
                        .user(users.get(i-1))
                        .fileName("fileName"+i)
                        .filePath("testPath")
                        .fileType("testType")
                        .build();
                snaps.add(snap);
            }
        }
        snapRepository.saveAll(snaps);
    }

    @Test
    @DisplayName("snap 페이징 조회 테스트 : 성공")
    public void findSnapPagingTest1(){
        // given

        // when
        List<Tuple> result = snapRepository.findSnapPaging("testLoginId1",1L,reqUser);

        // then
        assertThat(result.size()).isEqualTo(4);
        int index=4;
        for(Tuple tuple:result){
            assertThat(tuple.get(snap.id)).isEqualTo(index);
            assertThat(tuple.get(snap.fileName)).isEqualTo("fileName"+index);
            if(index > 2){
                assertThat(tuple.get(user.name)).isEqualTo("testName2");
            }
            else if(index <= 2){
                assertThat(tuple.get(user.name)).isEqualTo("testName1");
            }

            index--;
        }

    }

    @Test
    @DisplayName("snap 페이징 조회 테스트 : 실패(존재하지 않는 페이지)")
    public void findSnapPagingTest2(){
        // given

        // when,then
        try{
            snapRepository.findSnapPaging("testLoginId1",10L,reqUser);
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PAGE_NOT_FOUND);
        }
    }

}
