package me.snaptime.social.serviceTest;

import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.social.data.dto.req.AcceptFollowReqDto;
import me.snaptime.social.data.dto.res.FriendCntResDto;
import me.snaptime.social.data.repository.FriendShipRepository;
import me.snaptime.social.service.FriendShipService;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.fail;

@ExtendWith(MockitoExtension.class)
public class FriendShipServiceTest {

    @InjectMocks
    private FriendShipService friendShipService;
    @Mock
    private FriendShipRepository friendShipRepository;
    @Mock
    private UserRepository userRepository;

    private FriendShip friendShip;
    private User user;

    @BeforeEach
    void beforeEach(){
        friendShip = FriendShip.builder()
                .friendStatus(FriendStatus.WAITING)
                .build();
        user = User.builder()
                .build();
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 성공")
    public void sendFriendReqTest1(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        given(fromUser.getId()).willReturn(1L);
        given(toUser.getId()).willReturn(2L);
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.of(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.ofNullable(toUser));
        given(friendShipRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        friendShipService.sendFriendShipReq("loginId","testName");

        //then
        verify(userRepository,times(1)).findUserByName("testName");
        verify(userRepository,times(1)).findByLoginId("loginId");
        verify(friendShipRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
        verify(friendShipRepository,times(2)).save(any(FriendShip.class));
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(친구 조회 시 존재하지 않는 유저)")
    public void sendFriendReqTest2(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.empty());

        //when
        try{
            friendShipService.sendFriendShipReq("loginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findByLoginId("loginId");
            verify(friendShipRepository,times(0)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendShipRepository,times(0)).save(any(FriendShip.class));
        }
    }

    @Test
    // 친구요청이 거절되면 일정 기간동안 친구요청을 다시보내는 것이 차단됨
    @DisplayName("친구추가 요청 테스트 : 실패(친구요청 거절됨)")
    public void sendFriendReqTest3(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        FriendShip friendShip = spy(this.friendShip);
        given(friendShip.getFriendStatus()).willReturn(FriendStatus.REJECTED);
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendShipRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friendShip));

        //when
        try{
            friendShipService.sendFriendShipReq("loginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.REJECT_FRIEND_REQ);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findByLoginId("loginId");
            verify(friendShipRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendShipRepository,times(0)).save(any(FriendShip.class));
        }
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(이미 친구관계인 유저)")
    public void sendFriendReqTest4(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        FriendShip friendShip = spy(this.friendShip);
        given(friendShip.getFriendStatus()).willReturn(FriendStatus.FOLLOW);
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendShipRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friendShip));

        //when
        try{
            friendShipService.sendFriendShipReq("loginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ALREADY_FOLLOW);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findByLoginId("loginId");
            verify(friendShipRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendShipRepository,times(0)).save(any(FriendShip.class));
        }

    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(자신에게 친구요청을 보냄)")
    public void sendFriendReqTest5(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        given(fromUser.getId()).willReturn(1L);
        given(toUser.getId()).willReturn(1L);
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.of(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendShipRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        try{
            friendShipService.sendFriendShipReq("loginId","testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.SELF_FRIEND_REQ);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findByLoginId("loginId");
            verify(friendShipRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendShipRepository,times(0)).save(any(FriendShip.class));
        }
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 수락성공")
    public void acceptFriendReqTest1(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendShipRepository.findByToUserAndFromUser(toUser,fromUser)).willReturn(Optional.ofNullable(friendShip));

        //when
        String msg = friendShipService.acceptFriendShipReq("loginId",new AcceptFollowReqDto("testName",true));

        //then
        assertThat(msg).isEqualTo("팔로우 수락을 완료했습니다.");
        assertThat(friendShip.getFriendStatus()).isEqualTo(FriendStatus.FOLLOW);
        verify(friendShipRepository,times(1)).save(any(FriendShip.class));
        verify(userRepository,times(1)).findUserByName("testName");
        verify(userRepository,times(1)).findByLoginId("loginId");
        verify(friendShipRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 거절성공")
    public void acceptFriendReqTest2(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        FriendShip friendShip = spy(this.friendShip);
        FriendShip rejectedfriendShip = spy(this.friendShip);
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendShipRepository.findByToUserAndFromUser(toUser,fromUser)).willReturn(Optional.ofNullable(friendShip));
        given(friendShipRepository.findByToUserAndFromUser(fromUser,toUser)).willReturn(Optional.ofNullable(rejectedfriendShip));

        //when
        String msg = friendShipService.acceptFriendShipReq("loginId",new AcceptFollowReqDto("testName",false));

        //then
        assertThat(msg).isEqualTo("팔로우 거절을 완료했습니다.");
        assertThat(rejectedfriendShip.getFriendStatus()).isEqualTo(FriendStatus.REJECTED);
        assertThat(friendShip.getFriendStatus()).isEqualTo(FriendStatus.WAITING);
        verify(friendShipRepository,times(1)).save(any(FriendShip.class));
        verify(friendShipRepository,times(1)).delete(any(FriendShip.class));
        verify(userRepository,times(1)).findUserByName("testName");
        verify(userRepository,times(1)).findByLoginId("loginId");
        verify(friendShipRepository,times(2)).findByToUserAndFromUser(any(User.class),any(User.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 : 실패(존재하지 않는 친구관계)")
    public void acceptFriendReqTest3(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendShipRepository.findByToUserAndFromUser(toUser,fromUser)).willReturn(Optional.empty());

        //when
        try{
            String msg = friendShipService.acceptFriendShipReq("loginId",new AcceptFollowReqDto("testName",false));
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FRIENDSHIP_NOT_FOUND);
            verify(friendShipRepository,times(0)).save(any(FriendShip.class));
            verify(friendShipRepository,times(0)).delete(any(FriendShip.class));
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findByLoginId("loginId");
            verify(friendShipRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
        }
    }

    @Test
    @DisplayName("팔로우 삭제테스트 : 성공")
    public void deleteFollowTest1(){
        //given
        FriendShip friendShip = spy(this.friendShip);
        given(friendShip.getFromUser()).willReturn(user);
        given(friendShipRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(friendShip));
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user));

        //when
        friendShipService.deleteFriendShip("loginId",1l);

        //then
        verify(friendShipRepository,times(1)).delete(any(FriendShip.class));
        verify(friendShipRepository,times(1)).findById(any(Long.class));
        verify(userRepository,times(1)).findByLoginId(any(String.class));
    }

    @Test
    @DisplayName("팔로우 삭제테스트 : 실패(존재하지 않는 팔로우)")
    public void deleteFollowTest2(){
        //given
        given(friendShipRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when
        try{
            friendShipService.deleteFriendShip("loginId",1l);
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FRIENDSHIP_NOT_FOUND);
            verify(friendShipRepository,times(0)).delete(any(FriendShip.class));
            verify(friendShipRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(0)).findByLoginId(any(String.class));
        }
    }

    @Test
    @DisplayName("팔로우 삭제테스트 : 실패(팔로우 삭제권한 없음)")
    // 다른사람의 팔로우를 삭제하려고 할 경우 팔로우삭제권한이 없기때문에 예외를 발생시킵니다.
    public void deleteFollowTest3(){
        //given
        User fromUser = this.user;
        FriendShip friendShip = spy(this.friendShip);
        given(friendShip.getFromUser()).willReturn(fromUser);
        given(friendShipRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(friendShip));
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user));

        //when
        try{
            friendShipService.deleteFriendShip("loginId",1l);
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_FRIENDSHIP);
            verify(friendShipRepository,times(0)).delete(any(FriendShip.class));
            verify(friendShipRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(1)).findByLoginId(any(String.class));
        }
    }

    @Test
    @DisplayName("친구 수 조회 : 성공")
    public void findFriendShipCntTest1(){
        // given
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user));
        given(friendShipRepository.countByFromUserAndFriendStatus(any(User.class),any(FriendStatus.class))).willReturn(2l);
        given(friendShipRepository.countByToUserAndFriendStatus(any(User.class),any(FriendStatus.class))).willReturn(1l);

        // when
        FriendCntResDto friendCntResDto = friendShipService.findFriendShipCnt("loginId");

        // then
        assertThat(friendCntResDto.followerCnt()).isEqualTo(1l);
        assertThat(friendCntResDto.followingCnt()).isEqualTo(2l);
    }
}

