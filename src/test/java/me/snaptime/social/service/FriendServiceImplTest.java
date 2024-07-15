package me.snaptime.social.service;

import com.querydsl.core.Tuple;
import me.snaptime.alarm.service.CreateAlarmService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.domain.Friend;
import me.snaptime.friend.dto.res.FindFriendResDto;
import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.friend.repository.FriendRepository;
import me.snaptime.friend.service.impl.FriendServiceImpl;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.NextPageChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static me.snaptime.user.domain.QUser.user;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.fail;

@ExtendWith(MockitoExtension.class)
public class FriendServiceImplTest {

    @InjectMocks
    private FriendServiceImpl friendServiceImpl;
    @Mock
    private FriendRepository friendRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UrlComponent urlComponent;
    @Mock
    private NextPageChecker nextPageChecker;
    @Mock
    private CreateAlarmService createAlarmService;

    private Friend friend;
    private User user1;


    @BeforeEach
    void beforeEach(){
        friend = Friend.builder()
                .build();

        user1 = User.builder()
                .build();
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 성공")
    public void sendFriendReqTest1(){
        //given
        User sender = spy(user1);
        User receiver = spy(user1);
        given(sender.getId()).willReturn(1L);
        given(receiver.getId()).willReturn(2L);

        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.of(sender))
                .willReturn(Optional.ofNullable(receiver));

        given(friendRepository.findBySenderAndReceiver(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        friendServiceImpl.sendFollow("senderLoginId","testName");

        //then
        verify(userRepository,times(2)).findByLoginId(any(String.class));
        verify(friendRepository,times(1)).findBySenderAndReceiver(any(User.class),any(User.class));
        verify(friendRepository,times(1)).save(any(Friend.class));
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(친구 조회 시 존재하지 않는 유저)")
    public void sendFriendReqTest2(){
        //given
        User sender = spy(user1);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(sender))
                .willReturn(Optional.empty());

        //when
        try{
            friendServiceImpl.sendFollow("senderLoginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_EXIST);
            verify(userRepository,times(2)).findByLoginId(any(String.class));
            verify(friendRepository,times(0)).findBySenderAndReceiver(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(Friend.class));
        }
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(이미 친구관계인 유저)")
    public void sendFriendReqTest3(){
        //given
        User sender = spy(user1);
        User receiver = spy(user1);
        Friend friend = spy(this.friend);

        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(sender))
                .willReturn(Optional.of(receiver));

        given(friendRepository.findBySenderAndReceiver(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friend));

        //when
        try{
            friendServiceImpl.sendFollow("senderLoginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ALREADY_FOLLOW);
            verify(userRepository,times(2)).findByLoginId(any(String.class));
            verify(friendRepository,times(1)).findBySenderAndReceiver(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(Friend.class));
        }

    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(자신에게 친구요청을 보냄)")
    public void sendFriendReqTest5(){
        //given
        User sender = spy(user1);
        User receiver = spy(user1);
        given(sender.getId()).willReturn(1L);
        given(receiver.getId()).willReturn(1L);

        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.of(sender))
                .willReturn(Optional.of(receiver));

        given(friendRepository.findBySenderAndReceiver(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        try{
            friendServiceImpl.sendFollow("senderLoginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.SELF_FRIEND_REQ);
            verify(userRepository,times(2)).findByLoginId(any(String.class));
            verify(friendRepository,times(1)).findBySenderAndReceiver(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(Friend.class));
        }
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 수락성공")
    public void acceptFriendReqTest1(){
        //given
        given(friendRepository.findBySenderAndReceiver(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friend));

        //when
        String msg = friendServiceImpl.acceptFollow(user1,user1,true);

        //then
        assertThat(msg).isEqualTo("팔로우 수락을 완료했습니다.");
        verify(friendRepository,times(1)).save(any(Friend.class));
        verify(friendRepository,times(0)).delete(any(Friend.class));
        verify(friendRepository,times(1)).findBySenderAndReceiver(any(User.class),any(User.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 거절성공")
    public void acceptFriendReqTest2(){
        //given
        given(friendRepository.findBySenderAndReceiver(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friend));

        //when
        String msg = friendServiceImpl.acceptFollow(user1,user1,false);

        //then
        assertThat(msg).isEqualTo("팔로우 거절을 완료했습니다.");
        verify(friendRepository,times(1)).delete(any(Friend.class));
        verify(friendRepository,times(0)).save(any(Friend.class));
        verify(friendRepository,times(1)).findBySenderAndReceiver(any(User.class),any(User.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 실패(존재하지 않는 친구관계)")
    public void acceptFriendReqTest3(){
        //given
        given(friendRepository.findBySenderAndReceiver(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        try{
            String msg = friendServiceImpl.acceptFollow(user1,user1,true);
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FRIEND_REQ_NOT_FOUND);
            verify(friendRepository,times(0)).save(any(Friend.class));
            verify(friendRepository,times(0)).delete(any(Friend.class));
            verify(friendRepository,times(1)).findBySenderAndReceiver(any(User.class),any(User.class));
        }
    }

    @Test
    @DisplayName("팔로우 삭제테스트 : 성공")
    public void deleteFollowTest1(){
        //given
        User deletor = spy(user1);
        User deletedUser = spy(user1);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(deletor))
                .willReturn(Optional.of(deletedUser));

        given(friendRepository.findBySenderAndReceiver(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friend));

        //when
        friendServiceImpl.unFollow("senderLoginId","deletedUserLoginId");

        //then
        verify(friendRepository,times(1)).delete(any(Friend.class));
        verify(friendRepository,times(1)).findBySenderAndReceiver(any(User.class),any(User.class));
        verify(userRepository,times(2)).findByLoginId(any(String.class));
    }

    @Test
    @DisplayName("팔로우 삭제테스트 : 실패(존재하지 않는 팔로우)")
    public void deleteFollowTest2(){
        //given
        User deletor = spy(user1);
        User deletedUser = spy(user1);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(deletor))
                .willReturn(Optional.of(deletedUser));

        given(friendRepository.findBySenderAndReceiver(any(User.class),any(User.class))).willReturn(Optional.empty());
        //when
        try{
            friendServiceImpl.unFollow("senderLoginId","deletedUserLoginId");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FRIEND_NOT_EXIST);
            verify(friendRepository,times(0)).delete(any(Friend.class));
            verify(friendRepository,times(1)).findBySenderAndReceiver(any(User.class),any(User.class));
            verify(userRepository,times(2)).findByLoginId(any(String.class));
        }
    }

    @Test
    @DisplayName("친구 수 조회 : 성공")
    public void findFriendShipCntTest1(){
        // given
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user1));
        given(friendRepository.countBySender(any(User.class))).willReturn(2l);
        given(friendRepository.countByReceiver(any(User.class))).willReturn(1l);

        // when
        FriendCntResDto friendCntResDto = friendServiceImpl.findFriendCnt("loginId");

        // then
        assertThat(friendCntResDto.followerCnt()).isEqualTo(1l);
        assertThat(friendCntResDto.followingCnt()).isEqualTo(2l);
    }

    @Test
    @DisplayName("친구목록 조회테스트 : 성공")
    public void findFriendListTest1(){
        // given
        Tuple tuple1 = mock(Tuple.class);
        Tuple tuple2 = mock(Tuple.class);
        Tuple tuple3 = mock(Tuple.class);
        given(urlComponent.makeProfileURL(any(Long.class)))
                .willReturn("profile1")
                .willReturn("profile2")
                .willReturn("profile3");
        given(tuple1.get(user.loginId)).willReturn("testLoginId1");
        given(tuple2.get(user.loginId)).willReturn("testLoginId2");
        given(tuple3.get(user.loginId)).willReturn("testLoginId3");

        given(tuple1.get(user.profilePhoto.id)).willReturn(4L);
        given(tuple2.get(user.profilePhoto.id)).willReturn(5L);
        given(tuple3.get(user.profilePhoto.id)).willReturn(6L);

        given(tuple1.get(user.name)).willReturn("name1");
        given(tuple2.get(user.name)).willReturn("name2");
        given(tuple3.get(user.name)).willReturn("name3");

        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user1));
        given(friendRepository.findFriendList(any(User.class),any(FriendSearchType.class),any(Long.class),any(String.class)))
                .willReturn(List.of(tuple1,tuple2,tuple3));

        // when
        FindFriendResDto result = friendServiceImpl
                .findFriendList("writerLoginId","targetLoginId",1L,FriendSearchType.FOLLOWER,"searchKeyword");

        // then
        assertThat(result.friendInfoList().get(0).foundLoginId()).isEqualTo("testLoginId1");
        assertThat(result.friendInfoList().get(1).foundLoginId()).isEqualTo("testLoginId2");
        assertThat(result.friendInfoList().get(2).foundLoginId()).isEqualTo("testLoginId3");

        assertThat(result.friendInfoList().get(0).foundUserName()).isEqualTo("name1");
        assertThat(result.friendInfoList().get(1).foundUserName()).isEqualTo("name2");
        assertThat(result.friendInfoList().get(2).foundUserName()).isEqualTo("name3");

        assertThat(result.friendInfoList().get(0).profilePhotoURL()).isEqualTo("profile1");
        assertThat(result.friendInfoList().get(1).profilePhotoURL()).isEqualTo("profile2");
        assertThat(result.friendInfoList().get(2).profilePhotoURL()).isEqualTo("profile3");

        assertThat(result.hasNextPage()).isFalse();

        verify(userRepository,times(5)).findByLoginId(any(String.class));
        verify(urlComponent,times(3)).makeProfileURL(any(Long.class));
        verify(friendRepository,times(1))
                .findFriendList(any(User.class),any(FriendSearchType.class),any(Long.class),any(String.class));

    }


}

