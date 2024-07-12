package me.snaptime.social.service;

import com.querydsl.core.Tuple;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.common.FriendStatus;
import me.snaptime.friend.domain.Friend;
import me.snaptime.friend.dto.req.AcceptFollowReqDto;
import me.snaptime.friend.dto.res.FindFriendResDto;
import me.snaptime.friend.dto.res.FriendCntResDto;
import me.snaptime.friend.repository.FriendRepository;
import me.snaptime.friend.service.FriendService;
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
public class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;
    @Mock
    private FriendRepository friendRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UrlComponent urlComponent;
    @Mock
    private NextPageChecker nextPageChecker;

    private Friend friend;
    private User user1;


    @BeforeEach
    void beforeEach(){
        friend = Friend.builder()
                .friendStatus(FriendStatus.WAITING)
                .build();
        user1 = User.builder()
                .build();
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 성공")
    public void sendFriendReqTest1(){
        //given
        User fromUser = spy(user1);
        User toUser = spy(user1);
        given(fromUser.getId()).willReturn(1L);
        given(toUser.getId()).willReturn(2L);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.of(fromUser))
                .willReturn(Optional.ofNullable(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        friendService.sendFollow("loginId","testName");

        //then
        verify(userRepository,times(2)).findByLoginId(any(String.class));
        verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
        verify(friendRepository,times(2)).save(any(Friend.class));
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(친구 조회 시 존재하지 않는 유저)")
    public void sendFriendReqTest2(){
        //given
        User fromUser = spy(user1);
        User toUser = spy(user1);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(fromUser))
                .willReturn(Optional.empty());

        //when
        try{
            friendService.sendFollow("loginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_EXIST);
            verify(userRepository,times(2)).findByLoginId(any(String.class));
            verify(friendRepository,times(0)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(Friend.class));
        }
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(친구요청 거절됨)")
    public void sendFriendReqTest3(){
        //given
        User fromUser = spy(user1);
        User toUser = spy(user1);
        Friend friend = spy(this.friend);
        given(friend.getFriendStatus()).willReturn(FriendStatus.REJECTED);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(fromUser))
                .willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friend));

        //when
        try{
            friendService.sendFollow("loginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.REJECT_FRIEND_REQ);
            verify(userRepository,times(2)).findByLoginId(any(String.class));
            verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(Friend.class));
        }
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(이미 친구관계인 유저)")
    public void sendFriendReqTest4(){
        //given
        User fromUser = spy(user1);
        User toUser = spy(user1);
        Friend friend = spy(this.friend);
        given(friend.getFriendStatus()).willReturn(FriendStatus.FOLLOW);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(fromUser))
                .willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friend));

        //when
        try{
            friendService.sendFollow("loginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ALREADY_FOLLOW);
            verify(userRepository,times(2)).findByLoginId(any(String.class));
            verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(Friend.class));
        }

    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(자신에게 친구요청을 보냄)")
    public void sendFriendReqTest5(){
        //given
        User fromUser = spy(user1);
        User toUser = spy(user1);
        given(fromUser.getId()).willReturn(1L);
        given(toUser.getId()).willReturn(1L);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.of(fromUser))
                .willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        try{
            friendService.sendFollow("loginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.SELF_FRIEND_REQ);
            verify(userRepository,times(2)).findByLoginId(any(String.class));
            verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(Friend.class));
        }
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 수락성공")
    public void acceptFriendReqTest1(){
        //given
        User fromUser = spy(user1);
        User toUser = spy(user1);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(fromUser))
                .willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(toUser,fromUser)).willReturn(Optional.ofNullable(friend));

        //when
        String msg = friendService.acceptFollow("loginId",new AcceptFollowReqDto("testName",true));

        //then
        assertThat(msg).isEqualTo("팔로우 수락을 완료했습니다.");
        assertThat(friend.getFriendStatus()).isEqualTo(FriendStatus.FOLLOW);
        verify(friendRepository,times(1)).save(any(Friend.class));
        verify(userRepository,times(2)).findByLoginId(any(String.class));
        verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 거절성공")
    public void acceptFriendReqTest2(){
        //given
        User fromUser = spy(user1);
        User toUser = spy(user1);
        Friend friend = spy(this.friend);
        Friend rejectedfriend = spy(this.friend);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(fromUser))
                .willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(toUser,fromUser)).willReturn(Optional.ofNullable(friend));
        given(friendRepository.findByToUserAndFromUser(fromUser,toUser)).willReturn(Optional.ofNullable(rejectedfriend));

        //when
        String msg = friendService.acceptFollow("loginId",new AcceptFollowReqDto("testName",false));

        //then
        assertThat(msg).isEqualTo("팔로우 거절을 완료했습니다.");
        assertThat(rejectedfriend.getFriendStatus()).isEqualTo(FriendStatus.REJECTED);
        assertThat(friend.getFriendStatus()).isEqualTo(FriendStatus.WAITING);
        verify(friendRepository,times(1)).save(any(Friend.class));
        verify(friendRepository,times(1)).delete(any(Friend.class));
        verify(userRepository,times(2)).findByLoginId(any(String.class));
        verify(friendRepository,times(2)).findByToUserAndFromUser(any(User.class),any(User.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 실패(존재하지 않는 친구관계)")
    public void acceptFriendReqTest3(){
        //given
        User fromUser = spy(user1);
        User toUser = spy(user1);
        given(userRepository.findByLoginId(any(String.class)))
                .willReturn(Optional.ofNullable(fromUser))
                .willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(toUser,fromUser)).willReturn(Optional.empty());

        //when
        try{
            String msg = friendService.acceptFollow("loginId",new AcceptFollowReqDto("testName",false));
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FRIEND_NOT_EXIST);
            verify(friendRepository,times(0)).save(any(Friend.class));
            verify(friendRepository,times(0)).delete(any(Friend.class));
            verify(userRepository,times(2)).findByLoginId(any(String.class));
            verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
        }
    }

    @Test
    @DisplayName("팔로우 삭제테스트 : 성공")
    public void deleteFollowTest1(){
        //given
        Friend friend = spy(this.friend);
        given(friend.getFromUser()).willReturn(user1);
        given(friendRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(friend));
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user1));

        //when
        friendService.unFollow("loginId",1l);

        //then
        verify(friendRepository,times(1)).delete(any(Friend.class));
        verify(friendRepository,times(1)).findById(any(Long.class));
        verify(userRepository,times(1)).findByLoginId(any(String.class));
    }

    @Test
    @DisplayName("팔로우 삭제테스트 : 실패(존재하지 않는 팔로우)")
    public void deleteFollowTest2(){
        //given
        given(friendRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when
        try{
            friendService.unFollow("loginId",1l);
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FRIEND_NOT_EXIST);
            verify(friendRepository,times(0)).delete(any(Friend.class));
            verify(friendRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(0)).findByLoginId(any(String.class));
        }
    }

    @Test
    @DisplayName("팔로우 삭제테스트 : 실패(팔로우 삭제권한 없음)")
    // 다른사람의 팔로우를 삭제하려고 할 경우 팔로우삭제권한이 없기때문에 예외를 발생시킵니다.
    public void deleteFollowTest3(){
        //given
        User fromUser = spy(this.user1);
        User reqUser = spy(this.user1);
        Friend friend = spy(this.friend);
        given(fromUser.getId()).willReturn(2L);
        given(reqUser.getId()).willReturn(1L);
        given(friend.getFromUser()).willReturn(fromUser);
        given(friendRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(friend));
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(reqUser));

        //when
        try{
            friendService.unFollow("loginId",1l);
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_FRIENDSHIP);
            verify(friendRepository,times(0)).delete(any(Friend.class));
            verify(friendRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(1)).findByLoginId(any(String.class));
        }
    }

    @Test
    @DisplayName("친구 수 조회 : 성공")
    public void findFriendShipCntTest1(){
        // given
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user1));
        given(friendRepository.countByFromUserAndFriendStatus(any(User.class),any(FriendStatus.class))).willReturn(2l);
        given(friendRepository.countByToUserAndFriendStatus(any(User.class),any(FriendStatus.class))).willReturn(1l);

        // when
        FriendCntResDto friendCntResDto = friendService.findFriendCnt("loginId");

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
        FindFriendResDto result = friendService
                .findFriendList("loginId","targetLoginId",1L,FriendSearchType.FOLLOWER,"searchKeyword");

        // then
        assertThat(result.friendInfoList().get(0).loginId()).isEqualTo("testLoginId1");
        assertThat(result.friendInfoList().get(1).loginId()).isEqualTo("testLoginId2");
        assertThat(result.friendInfoList().get(2).loginId()).isEqualTo("testLoginId3");

        assertThat(result.friendInfoList().get(0).userName()).isEqualTo("name1");
        assertThat(result.friendInfoList().get(1).userName()).isEqualTo("name2");
        assertThat(result.friendInfoList().get(2).userName()).isEqualTo("name3");

        assertThat(result.friendInfoList().get(0).profilePhotoURL()).isEqualTo("profile1");
        assertThat(result.friendInfoList().get(1).profilePhotoURL()).isEqualTo("profile2");
        assertThat(result.friendInfoList().get(2).profilePhotoURL()).isEqualTo("profile3");

        assertThat(result.hasNextPage()).isFalse();

        verify(userRepository,times(4)).findByLoginId(any(String.class));
        verify(urlComponent,times(3)).makeProfileURL(any(Long.class));
        verify(friendRepository,times(1))
                .findFriendList(any(User.class),any(FriendSearchType.class),any(Long.class),any(String.class));

    }
}
