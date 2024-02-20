package me.snaptime.social.controllerTest;

import com.google.gson.Gson;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.social.data.controller.FriendShipController;
import me.snaptime.social.data.dto.req.AcceptFollowReqDto;
import me.snaptime.social.service.FriendShipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FriendShipController.class)
public class FriendShipControllerTest {

    @MockBean
    private FriendShipService friendShipService;
    @Autowired
    private MockMvc mockMvc;

    private AcceptFollowReqDto acceptFollowReqDto;


    @Test
    @DisplayName("팔로우 요청테스트 -> (성공)")
    public void sendFollowReq1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserName","followName"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("팔로우가 완료되었습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @DisplayName("팔로우 요청테스트 -> (실패 : 존재하지 않는 유저)")
    public void sendFollowReq2() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.USER_NOT_FOUND)).when(friendShipService).sendFriendShipReq(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserName","followName"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 유저입니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @DisplayName("팔로우 요청테스트 -> (실패 : 이미 팔로우한 유저)")
    public void sendFollowReq3() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.ALREADY_FOLLOW)).when(friendShipService).sendFriendShipReq(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserName","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("이미 팔로우관계입니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @DisplayName("팔로우 요청테스트 -> (실패 : 전에 팔로우요청이 거절됨)")
    public void sendFollowReq4() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.REJECT_FRIEND_REQ)).when(friendShipService).sendFriendShipReq(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserName","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("팔로우요청이 거절되었습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @DisplayName("팔로우 요청테스트 -> (실패 : 자기 자신에게 팔로우요청을 보냄)")
    public void sendFollowReq5() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.SELF_FRIEND_REQ)).when(friendShipService).sendFriendShipReq(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserName","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("자신에게 친구추가 요청을 보낼 수 없습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @DisplayName("팔로우 요청테스트 -> (실패 : 요청 파라미터 공백)")
    public void sendFollowReq6() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserName",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("올바르지 않은 입력값입니다."))
                .andExpect(jsonPath("$.result.fromUserName").value("팔로우요청을 보낼 유저의 이름을 입력해주세요."))
                .andDo(print());

        verify(friendShipService,times(0)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 -> (수락성공)")
    public void acceptFollowReq1() throws Exception {
        //given
        acceptFollowReqDto = new AcceptFollowReqDto("testName",true);
        Gson gson = new Gson();
        String requestBody = gson.toJson(acceptFollowReqDto);
        given(friendShipService.acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class))).willReturn("팔로우 수락을 완료했습니다.");


        //when, then
        this.mockMvc.perform(post("/friends/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("팔로우 수락을 완료했습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 -> (거절성공)")
    public void acceptFollowReq2() throws Exception {
        //given
        acceptFollowReqDto = new AcceptFollowReqDto("testName",false);
        Gson gson = new Gson();
        String requestBody = gson.toJson(acceptFollowReqDto);
        given(friendShipService.acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class))).willReturn("팔로우 거절을 완료했습니다.");

        //when, then
        this.mockMvc.perform(post("/friends/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("팔로우 거절을 완료했습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 -> (실패 : 유효성검사 실패)")
    public void acceptFollowReq3() throws Exception {
        //given
        acceptFollowReqDto = new AcceptFollowReqDto("",null);
        Gson gson = new Gson();
        String requestBody = gson.toJson(acceptFollowReqDto);

        //when, then
        this.mockMvc.perform(post("/friends/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.result.fromUserName").value("유저 이름을 입력해주세요."))
                .andExpect(jsonPath("$.result.isAccept").value("수락여부를 보내주세요."))
                .andDo(print());

        verify(friendShipService,times(0)).acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 -> (실패 : 존재하지 않는 유저)")
    public void acceptFollowReq4() throws Exception {
        //given
        acceptFollowReqDto = new AcceptFollowReqDto("test",true);
        Gson gson = new Gson();
        String requestBody = gson.toJson(acceptFollowReqDto);
        given(friendShipService.acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class))).willThrow(new CustomException(ExceptionCode.USER_NOT_FOUND));

        //when, then
        this.mockMvc.perform(post("/friends/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 유저입니다."))
                .andDo(print());

        verify(friendShipService,times(1)).acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class));
    }

    @Test
    @DisplayName("팔로우 수락테스트 -> (실패 : 존재하지 않는 친구관계)")
    public void acceptFollowReq5() throws Exception {
        //given
        acceptFollowReqDto = new AcceptFollowReqDto("test",true);
        Gson gson = new Gson();
        String requestBody = gson.toJson(acceptFollowReqDto);
        given(friendShipService.acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class))).willThrow(new CustomException(ExceptionCode.FRIENDSHIP_NOT_FOUND));

        //when, then
        this.mockMvc.perform(post("/friends/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 친구입니다."))
                .andDo(print());

        verify(friendShipService,times(1)).acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class));
    }
}
