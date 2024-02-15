package me.snaptime.Social.ServiceTest;

import me.snaptime.Social.common.FriendStatus;
import me.snaptime.Social.data.domain.Friend;
import me.snaptime.Social.data.repository.FriendRepository;
import me.snaptime.Social.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;
    @Mock
    private FriendRepository friendRepository;

    private Friend friend;
    @BeforeEach
    void beforeEach(){
        friend = Friend.builder()
                .build();
    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 성공")
    public void sendFriendReqTest1(){

    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(친구 조회 시 존재하지 않는 유저)")
    public void sendFriendReqTest2(){

    }

    @Test
    @DisplayName("친구추가 요청 테스트 : 실패(이미 친구추구 요청을 보냄)")
    public void sendFriendReqTest3(){

    }

    @Test
    // 친구요청이 거절되면 일정 기간동안 친구요청을 다시보내는 것이 차단됨
    @DisplayName("친구추가 요청 테스트 : 실패(친구요청 거절됨)")
    public void sendFriendReqTest4(){

    }
}
