package me.snaptime.social.serviceTest;

import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.common.FriendStatus;
import me.snaptime.social.data.domain.FriendShip;
import me.snaptime.social.data.repository.FriendRepository;
import me.snaptime.social.service.FriendService;
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
    private FriendService friendService;
    @Mock
    private FriendRepository friendRepository;
    @Mock
    private UserRepository userRepository;

    private FriendShip friendShip;
    private User user;

    @BeforeEach
    void beforeEach(){
        friendShip = FriendShip.builder()
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
        given(fromUser.getId()).willReturn(1l);
        given(toUser.getId()).willReturn(2l);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.ofNullable(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        friendService.sendFriendShipReq(1l,"testName");

        //then
        verify(userRepository,times(1)).findUserByName("testName");
        verify(userRepository,times(1)).findById(1l);
        verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
        verify(friendRepository,times(1)).save(any(FriendShip.class));
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(친구 조회 시 존재하지 않는 유저)")
    public void sendFriendReqTest2(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.empty());

        //when
        try{
            friendService.sendFriendShipReq(1l,"testName");
            fail("예외가 발생하지 않음");
        }catch (Exception ex){
            //then
            // assertThat(ex.getExceptionCode()).isEqualTo(예외코드);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findById(1l);
            verify(friendRepository,times(0)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(FriendShip.class));
        }
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(이미 친구추가 요청을 보냄)")
    public void sendFriendReqTest3(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        FriendShip friendShip = spy(this.friendShip);
        given(friendShip.getFriendStatus()).willReturn(FriendStatus.WATING);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friendShip));

        //when
        try{
            friendService.sendFriendShipReq(1l,"testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.WATING_FRIEND_REQ);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findById(1l);
            verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(FriendShip.class));
        }
    }

    @Test
    // 친구요청이 거절되면 일정 기간동안 친구요청을 다시보내는 것이 차단됨
    @DisplayName("친구추가 요청 테스트 : 실패(친구요청 거절됨)")
    public void sendFriendReqTest4(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        FriendShip friendShip = spy(this.friendShip);
        given(friendShip.getFriendStatus()).willReturn(FriendStatus.REJECTED);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friendShip));

        //when
        try{
            friendService.sendFriendShipReq(1l,"testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.REJECT_FRIEND_REQ);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findById(1l);
            verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(FriendShip.class));
        }
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(이미 친구관계인 유저)")
    public void sendFriendReqTest5(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        FriendShip friendShip = spy(this.friendShip);
        given(friendShip.getFriendStatus()).willReturn(FriendStatus.FRIEND);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.ofNullable(friendShip));

        //when
        try{
            friendService.sendFriendShipReq(1l,"testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ALREADY_FRIEND);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findById(1l);
            verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(FriendShip.class));
        }

    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(자신에게 친구요청을 보냄)")
    public void sendFriendReqTest7(){
        //given
        User fromUser = spy(user);
        User toUser = spy(user);
        given(fromUser.getId()).willReturn(1l);
        given(toUser.getId()).willReturn(1l);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(fromUser));
        given(userRepository.findUserByName(any(String.class))).willReturn(Optional.of(toUser));
        given(friendRepository.findByToUserAndFromUser(any(User.class),any(User.class))).willReturn(Optional.empty());

        //when
        try{
            friendService.sendFriendShipReq(1l,"testName");
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.SELF_FRIEND_REQ);
            verify(userRepository,times(1)).findUserByName("testName");
            verify(userRepository,times(1)).findById(1l);
            verify(friendRepository,times(1)).findByToUserAndFromUser(any(User.class),any(User.class));
            verify(friendRepository,times(0)).save(any(FriendShip.class));
        }
    }
}