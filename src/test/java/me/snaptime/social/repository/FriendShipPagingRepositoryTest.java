package me.snaptime.social.repository;

import com.querydsl.core.Tuple;
import me.snaptime.common.component.impl.UrlComponentImpl;
import me.snaptime.common.config.JpaAuditingConfig;
import me.snaptime.common.config.QueryDslConfig;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.common.FriendSearchType;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static me.snaptime.user.data.domain.QUser.user;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
@TestPropertySource(locations = "classpath:application-test.yml")
public class FriendShipPagingRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendShipRepository friendShipRepository;
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
        List<User> saveUserList = new ArrayList<>();
        List<ProfilePhoto> savePrifilePhotoList = new ArrayList<>();
        List<FriendShip> saveFriendShipList = new ArrayList<>();
        saveUserList.add(reqUser);

        /*
            1~10은 reqUser -> tempUser
            11~20은 reqUser -> tempUser, tempUser -> reqUser
            21~30은 tempUser -> reqUser
        */
       for(int i=1;i<=30;i++){
           ProfilePhoto profilePhoto = ProfilePhoto.builder()
                   .profilePhotoPath("testPath")
                   .profilePhotoName("testProfileName1")
                   .build();
           User tempUser = User.builder()
                   .email("test"+i+"@google.com")
                   .loginId("testLoginId"+i)
                   .name("testName"+i)
                   .password("1234")
                   .profilePhoto(profilePhoto)
                   .birthDay(String.valueOf(LocalDateTime.now()))
                   .build();

           if(i<=10){
               FriendShip friendShip1 = FriendShip.builder()
                       .friendStatus(FriendStatus.FOLLOW)
                       .fromUser(reqUser)
                       .toUser(tempUser)
                       .build();
               FriendShip friendShip2 = FriendShip.builder()
                       .friendStatus(FriendStatus.WAITING)
                       .fromUser(tempUser)
                       .toUser(reqUser)
                       .build();
               saveFriendShipList.add(friendShip1);
               saveFriendShipList.add(friendShip2);
           }
           else if(i>10 && i<=20){
               FriendShip friendShip1 = FriendShip.builder()
                       .friendStatus(FriendStatus.FOLLOW)
                       .fromUser(reqUser)
                       .toUser(tempUser)
                       .build();
               FriendShip friendShip2 = FriendShip.builder()
                       .friendStatus(FriendStatus.FOLLOW)
                       .fromUser(tempUser)
                       .toUser(reqUser)
                       .build();
               saveFriendShipList.add(friendShip1);
               saveFriendShipList.add(friendShip2);
           }
           else{
               FriendShip friendShip1 = FriendShip.builder()
                       .friendStatus(FriendStatus.WAITING)
                       .fromUser(reqUser)
                       .toUser(tempUser)
                       .build();
               FriendShip friendShip2 = FriendShip.builder()
                       .friendStatus(FriendStatus.FOLLOW)
                       .fromUser(tempUser)
                       .toUser(reqUser)
                       .build();
               saveFriendShipList.add(friendShip1);
               saveFriendShipList.add(friendShip2);
           }

           saveUserList.add(tempUser);
           savePrifilePhotoList.add(profilePhoto);
       }
       userRepository.saveAll(saveUserList);
       friendShipRepository.saveAll(saveFriendShipList);
    }

    @Test
    @DisplayName("친구조회 테스트 -> 성공(팔로잉 조회)")
    public void findFriendTest1(){
        // given

        // when
        List<Tuple> result = friendShipRepository.findFriendList(reqUser, FriendSearchType.FOLLOWING,1L,null);

        // then
        assertThat(result.size()).isEqualTo(20);
        int index = 1;
        for(Tuple tuple : result){
            assertThat(tuple.get(user.loginId)).isEqualTo("testLoginId"+index);
            assertThat(tuple.get(user.profilePhoto.id)).isEqualTo(index+1);
            assertThat(tuple.get(user.name)).isEqualTo("testName"+index);
            index++;
        }
    }

    @Test
    @DisplayName("친구조회 테스트 -> 성공(팔로워 조회)")
    public void findFriendTest2(){
        // given

        // when
        List<Tuple> result = friendShipRepository.findFriendList(reqUser, FriendSearchType.FOLLOWER,1L,null);

        // then
        assertThat(result.size()).isEqualTo(20);
        int index = 11;
        for(Tuple tuple : result){
            assertThat(tuple.get(user.loginId)).isEqualTo("testLoginId"+index);
            assertThat(tuple.get(user.profilePhoto.id)).isEqualTo(index+1+31);
            assertThat(tuple.get(user.name)).isEqualTo("testName"+index);
            index++;
        }
    }

    @Test
    @DisplayName("친구조회 테스트 -> 성공(팔로우 조회 / 검색조회)")
    public void findFriendTest3(){
        // given

        // when
        List<Tuple> result = friendShipRepository.findFriendList(reqUser, FriendSearchType.FOLLOWER,1L,"20");

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).get(user.loginId)).isEqualTo("testLoginId20");
        assertThat(result.get(0).get(user.profilePhoto.id)).isEqualTo(20+62+1);
        assertThat(result.get(0).get(user.name)).isEqualTo("testName"+20);
    }

    @Test
    @DisplayName("친구조회 테스트 -> 실패(존재하지 않는 페이지)")
    public void findFriendTest4(){
        // given

        // when
        try{
            friendShipRepository.findFriendList(reqUser, FriendSearchType.FOLLOWER,10L,"20");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PAGE_NOT_FOUND);
        }

    }
}
