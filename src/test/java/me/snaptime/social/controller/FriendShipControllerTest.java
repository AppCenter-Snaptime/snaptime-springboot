package me.snaptime.social.controller;

import com.google.gson.Gson;
import me.snaptime.common.config.SecurityConfig;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.common.jwt.JwtProvider;
import me.snaptime.social.common.FriendSearchType;
import me.snaptime.social.data.dto.req.AcceptFollowReqDto;
import me.snaptime.social.service.FriendShipService;
import me.snaptime.user.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FriendShipController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class FriendShipControllerTest {

    @MockBean
    private FriendShipService friendShipService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    private AcceptFollowReqDto acceptFollowReqDto;


    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (성공)")
    public void sendFollowReq1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserLoginId","followName"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("팔로우가 완료되었습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 존재하지 않는 유저)")
    public void sendFollowReq2() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.USER_NOT_EXIST)).when(friendShipService).sendFriendShipReq(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserLoginId","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("사용자가 존재하지 않습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 이미 팔로우한 유저)")
    public void sendFollowReq3() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.ALREADY_FOLLOW)).when(friendShipService).sendFriendShipReq(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserLoginId","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("이미 팔로우관계입니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 전에 팔로우요청이 거절됨)")
    public void sendFollowReq4() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.REJECT_FRIEND_REQ)).when(friendShipService).sendFriendShipReq(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserLoginId","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("팔로우요청이 거절되었습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 자기 자신에게 팔로우요청을 보냄)")
    public void sendFollowReq5() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.SELF_FRIEND_REQ)).when(friendShipService).sendFriendShipReq(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserLoginId","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("자신에게 친구추가 요청을 보낼 수 없습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 요청 파라미터 공백)")
    public void sendFollowReq6() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromUserLoginId",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("팔로우요청을 보낼 유저의 이름을 입력해주세요."))
                .andDo(print());

        verify(friendShipService,times(0)).sendFriendShipReq(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
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
                .andExpect(jsonPath("$.result.fromUserLoginId").value("유저의 LoginId를 입력해주세요."))
                .andExpect(jsonPath("$.result.isAccept").value("수락여부를 보내주세요."))
                .andDo(print());

        verify(friendShipService,times(0))
                .acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 수락테스트 -> (실패 : 존재하지 않는 유저)")
    public void acceptFollowReq4() throws Exception {
        //given
        acceptFollowReqDto = new AcceptFollowReqDto("test",true);
        Gson gson = new Gson();
        String requestBody = gson.toJson(acceptFollowReqDto);
        given(friendShipService.acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class)))
                .willThrow(new CustomException(ExceptionCode.USER_NOT_EXIST));

        //when, then
        this.mockMvc.perform(post("/friends/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("사용자가 존재하지 않습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 수락테스트 -> (실패 : 존재하지 않는 친구관계)")
    public void acceptFollowReq5() throws Exception {
        //given
        acceptFollowReqDto = new AcceptFollowReqDto("test",true);
        Gson gson = new Gson();
        String requestBody = gson.toJson(acceptFollowReqDto);
        given(friendShipService.acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class))).willThrow(new CustomException(ExceptionCode.FRIENDSHIP_NOT_EXIST));

        //when, then
        this.mockMvc.perform(post("/friends/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 친구입니다."))
                .andDo(print());

        verify(friendShipService,times(1)).acceptFriendShipReq(any(String.class),any(AcceptFollowReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제테스트 -> 성공")
    public void deleteFollowTest1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(delete("/friends/{friendShipId}","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("팔로우삭제가 완료되었습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).deleteFriendShip(any(String.class),any(Long.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제테스트 -> 실패(PathVariable 타입예외)")
    public void deleteFollowTest2() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(delete("/friends/{friendShipId}","string")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("friendShipId이 Long타입이여야 합니다."))
                .andDo(print());

        verify(friendShipService,times(0)).deleteFriendShip(any(String.class),any(Long.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제테스트 -> 실패(존재하지 않는 팔로우)")
    public void deleteFollowTest3() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.FRIENDSHIP_NOT_EXIST))
                .when(friendShipService).deleteFriendShip(any(String.class),any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/friends/{friendShipId}","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 친구입니다."))
                .andDo(print());

        verify(friendShipService,times(1)).deleteFriendShip(any(String.class),any(Long.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제테스트 -> 실패(팔로우 삭제권한 없음)")
    public void deleteFollowTest4() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.ACCESS_FAIL_FRIENDSHIP))
                .when(friendShipService).deleteFriendShip(any(String.class),any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/friends/{friendShipId}","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("해당 친구에 대한 권한이 없습니다."))
                .andDo(print());

        verify(friendShipService,times(1)).deleteFriendShip(any(String.class),any(Long.class));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 성공(팔로잉 조회,검색키워드 없는경우)")
    public void findFriendListTest1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","FOLLOWING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("친구조회가 완료되었습니다."))
                .andDo(print());

        verify(friendShipService,times(1))
                .findFriendList(any(String.class),any(Long.class),any(FriendSearchType.class),eq(null));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 성공(팔로워 조회,검색키워드 없는경우)")
    public void findFriendListTest2() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","FOLLOWER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("친구조회가 완료되었습니다."))
                .andDo(print());

        verify(friendShipService,times(1))
                .findFriendList(any(String.class),any(Long.class),any(FriendSearchType.class),eq(null));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 성공(검색키워드 있는경우)")
    public void findFriendListTest3() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","FOLLOWING")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("친구조회가 완료되었습니다."))
                .andDo(print());

        verify(friendShipService,times(1))
                .findFriendList(any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 실패(ENUM타입 예외)")
    public void findFriendListTest4() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","TEST")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("friendSearchType이 FriendSearchType타입이여야 합니다."))
                .andDo(print());

        verify(friendShipService,times(0))
                .findFriendList(any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 실패(PathVariable타입 예외)")
    public void findFriendListTest5() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}","test")
                        .param("friendSearchType","FOLLOWER")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("pageNum이 Long타입이여야 합니다."))
                .andDo(print());

        verify(friendShipService,times(0))
                .findFriendList(any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 실패(FriendSearchType값 null)")
    public void findFriendListTest6() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("friendSearchType:팔로잉과 팔로워중 어느 친구목록을 조회할 지 입력해주세요."))
                .andDo(print());

        verify(friendShipService,times(0))
                .findFriendList(any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 실패(존재하지 않는 페이지)")
    public void findFriendListTest7() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.PAGE_NOT_FOUND))
                .when(friendShipService).findFriendList(any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","FOLLOWER")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 페이지입니다."))
                .andDo(print());

        verify(friendShipService,times(1))
                .findFriendList(any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }
}
