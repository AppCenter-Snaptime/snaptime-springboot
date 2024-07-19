package me.snaptime.social.controller;

import me.snaptime.config.SecurityConfig;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.common.FriendSearchType;
import me.snaptime.friend.controller.FriendController;
import me.snaptime.friend.service.FriendService;
import me.snaptime.jwt.JwtProvider;
import me.snaptime.jwt.UserDetailsServiceImpl;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FriendController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class FriendControllerTest {

    @MockBean
    private FriendService friendService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (성공)")
    public void sendFollowReq1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("receiverLoginId","followName"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("팔로우가 완료되었습니다."))
                .andDo(print());

        verify(friendService,times(1)).sendFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 존재하지 않는 유저)")
    public void sendFollowReq2() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.USER_NOT_EXIST)).when(friendService).sendFollow(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("receiverLoginId","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("사용자가 존재하지 않습니다."))
                .andDo(print());

        verify(friendService,times(1)).sendFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 이미 팔로우한 유저)")
    public void sendFollowReq3() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.ALREADY_FOLLOW)).when(friendService).sendFollow(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("receiverLoginId","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("이미 팔로우관계입니다."))
                .andDo(print());

        verify(friendService,times(1)).sendFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 전에 팔로우요청이 거절됨)")
    public void sendFollowReq4() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.REJECT_FRIEND_REQ)).when(friendService).sendFollow(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("receiverLoginId","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("팔로우요청이 거절되었습니다."))
                .andDo(print());

        verify(friendService,times(1)).sendFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 자기 자신에게 팔로우요청을 보냄)")
    public void sendFollowReq5() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.SELF_FRIEND_REQ)).when(friendService).sendFollow(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("receiverLoginId","followName"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("자신에게 친구추가 요청을 보낼 수 없습니다."))
                .andDo(print());

        verify(friendService,times(1)).sendFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 요청테스트 -> (실패 : 요청 파라미터 공백)")
    public void sendFollowReq6() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(post("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("receiverLoginId",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("팔로우요청을 보낼 유저의 이름을 입력해주세요."))
                .andDo(print());

        verify(friendService,times(0)).sendFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제테스트 -> 성공")
    public void deleteFollowTest1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(delete("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("deletedUserLoginId","testLoginId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("팔로우삭제가 완료되었습니다."))
                .andDo(print());

        verify(friendService,times(1)).unFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제테스트 -> 실패(파라미터 공백)")
    public void deleteFollowTest2() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(delete("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("deletedUserLoginId",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("언팔로우할 유저의 loginId를 입력해주세요."))
                .andDo(print());

        verify(friendService,times(0)).unFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제테스트 -> 실패(존재하지 않는 팔로우)")
    public void deleteFollowTest3() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.FRIEND_NOT_EXIST))
                .when(friendService).unFollow(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(delete("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("deletedUserLoginId","testLoginId"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 친구입니다."))
                .andDo(print());

        verify(friendService,times(1)).unFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("팔로우 삭제테스트 -> 실패(팔로우 삭제권한 없음)")
    public void deleteFollowTest4() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.ACCESS_FAIL_FRIENDSHIP))
                .when(friendService).unFollow(any(String.class),any(String.class));

        //when, then
        this.mockMvc.perform(delete("/friends")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("deletedUserLoginId","testLoginId"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.msg").value("해당 친구에 대한 권한이 없습니다."))
                .andDo(print());

        verify(friendService,times(1)).unFollow(any(String.class),any(String.class));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 성공(팔로잉 조회,검색키워드 없는경우)")
    public void findFriendListTest1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","FOLLOWING")
                        .param("targetLoginId","tempLoginId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("친구조회가 완료되었습니다."))
                .andDo(print());

        verify(friendService,times(1))
                .findFriends(any(String.class),any(String.class),any(Long.class),any(FriendSearchType.class),eq(null));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 성공(팔로워 조회,검색키워드 없는경우)")
    public void findFriendListTest2() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","FOLLOWER")
                        .param("targetLoginId","tempLoginId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("친구조회가 완료되었습니다."))
                .andDo(print());

        verify(friendService,times(1))
                .findFriends(any(String.class),any(String.class),any(Long.class),any(FriendSearchType.class),eq(null));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 성공(검색키워드 있는경우)")
    public void findFriendListTest3() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","FOLLOWING")
                        .param("targetLoginId","tempLoginId")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("친구조회가 완료되었습니다."))
                .andDo(print());

        verify(friendService,times(1))
                .findFriends(any(String.class),any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 실패(ENUM타입 예외)")
    public void findFriendListTest4() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","TEST")
                        .param("targetLoginId","tempLoginId")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("friendSearchType이 FriendSearchType타입이여야 합니다."))
                .andDo(print());

        verify(friendService,times(0))
                .findFriends(any(String.class),any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 실패(PathVariable타입 예외)")
    public void findFriendListTest5() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}","test")
                        .param("friendSearchType","FOLLOWER")
                        .param("targetLoginId","tempLoginId")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("pageNum이 Long타입이여야 합니다."))
                .andDo(print());

        verify(friendService,times(0))
                .findFriends(any(String.class),any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 실패(FriendSearchType값 null)")
    public void findFriendListTest6() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","")
                        .param("targetLoginId","tempLoginId")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("friendSearchType:팔로잉과 팔로워중 어느 친구목록을 조회할 지 입력해주세요."))
                .andDo(print());

        verify(friendService,times(0))
                .findFriends(any(String.class), any(String.class),any(Long.class),any(FriendSearchType.class),eq("박"));
    }

    @Test
    @WithMockUser
    @DisplayName("친구목록 조회테스트 -> 실패(존재하지 않는 페이지)")
    public void findFriendListTest7() throws Exception {
        //given
        doThrow(new CustomException(ExceptionCode.PAGE_NOT_FOUND))
                .when(friendService).findFriends(any(String.class),any(String.class),any(Long.class),
                                                        any(FriendSearchType.class),eq("박"));

        //when, then
        this.mockMvc.perform(get("/friends/{pageNum}",1L)
                        .param("friendSearchType","FOLLOWER")
                        .param("targetLoginId","tempLoginId")
                        .param("searchKeyword","박")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 페이지입니다."))
                .andDo(print());

        verify(friendService,times(1))
                .findFriends(any(String.class),any(String.class),any(Long.class),any(FriendSearchType.class),
                                eq("박"));
    }
}
