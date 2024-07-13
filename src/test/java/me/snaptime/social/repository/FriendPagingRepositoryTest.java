package me.snaptime.social.repository;

import com.querydsl.core.Tuple;
import me.snaptime.component.url.UrlComponentImpl;
import me.snaptime.config.JpaAuditingConfig;
import me.snaptime.config.QueryDslConfig;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.domain.Friend;
import me.snaptime.friend.repository.FriendRepository;
import me.snaptime.profilePhoto.domain.ProfilePhoto;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
@TestPropertySource(locations = "classpath:application-test.yml")
public class FriendPagingRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;
    private User reqUser;

    @MockBean
    private UrlComponentImpl urlComponent;

    @BeforeEach
    void init(){
        ProfilePhoto reqProfilePhoto = ProfilePhoto.builder()
                .profilePhotoPath("testPath")
                .profilePhotoName("testProfileName1")
                .build();
        reqUser = User.builder()
                .email("test@google.com")
                .loginId("testLoginId")
                .name("testName")
                .password("1234")
                .profilePhoto(reqProfilePhoto)
                .birthDay(String.valueOf(LocalDateTime.now()))
                .build();
        userRepository.save(reqUser);
        for(int i=0;i<5;i++){
            createFriend(reqUser, (long) i);
        }
    }

    @Test
    @DisplayName("친구조회 테스트 -> 성공(팔로잉 조회)")
    public void findFriendTest1(){
        // given

        // when
        List<Tuple> result = friendRepository.findFriendList(reqUser, FriendSearchType.FOLLOWING,1L,null);

        // then
        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("친구조회 테스트 -> 성공(팔로워 조회)")
    public void findFriendTest2(){
        // given

        // when
        List<Tuple> result = friendRepository.findFriendList(reqUser, FriendSearchType.FOLLOWER,1L,"");

        // then
        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("친구조회 테스트 -> 성공(팔로잉 조회 / 검색조회)")
    public void findFriendTest3(){
        // given

        // when
        List<Tuple> result = friendRepository.findFriendList(reqUser, FriendSearchType.FOLLOWING,1L,"testName1");

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("친구조회 테스트 -> 실패(존재하지 않는 페이지)")
    public void findFriendTest4(){
        // given

        // when
        try{
            friendRepository.findFriendList(reqUser, FriendSearchType.FOLLOWER,10L,"20");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PAGE_NOT_FOUND);
        }

    }

    private void createFriend(User reqUser, Long i){
        ProfilePhoto profilePhoto = ProfilePhoto.builder()
                .profilePhotoPath("testPath")
                .profilePhotoName("testProfileName1")
                .build();

        User user = User.builder()
                .email("test"+i+"@google.com")
                .loginId("test"+i+"LoginId")
                .name("testName"+i)
                .password("1234")
                .profilePhoto(profilePhoto)
                .birthDay(String.valueOf(LocalDateTime.now()))
                .build();
        userRepository.save(user);
        friendRepository.save(
                Friend.builder()
                        .sender(reqUser)
                        .receiver(user)
                        .build()
        );
        friendRepository.save(
                Friend.builder()
                        .sender(user)
                        .receiver(reqUser)
                        .build()
        );
    }
}
