package me.snaptime.social.controller;

import com.google.gson.Gson;
import me.snaptime.config.SecurityConfig;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.jwt.JwtProvider;
import me.snaptime.jwt.UserDetailsServiceImpl;
import me.snaptime.reply.controller.ReplyController;
import me.snaptime.reply.dto.req.AddChildReplyReqDto;
import me.snaptime.reply.dto.req.AddParentReplyReqDto;
import me.snaptime.reply.service.ReplyService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ReplyController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ReplyControllerTest {

    @MockBean
    private ReplyService replyService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    //프로퍼티 값 주입을 위함. accessTokenValidTime, refreshTokenValidTime
    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("댓글 등록 테스트 -> 성공")
    public void addParentReplyTest1() throws Exception {
        //given
        Gson gson = new Gson();
        AddParentReplyReqDto addParentReplyReqDto =
                new AddParentReplyReqDto("댓글내용",1L);
        String requestBody = gson.toJson(addParentReplyReqDto);

        //when, then
        this.mockMvc.perform(post("/parent-replies")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("댓글등록이 성공했습니다."))
                .andDo(print());

        verify(replyService,times(1))
                .addParentReply(any(String.class),any(AddParentReplyReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 등록 테스트 -> 실패(댓글내용 공백)")
    public void addParentReplyTest2() throws Exception {
        //given
        Gson gson = new Gson();
        AddParentReplyReqDto addParentReplyReqDto =
                new AddParentReplyReqDto("",1L);
        String requestBody = gson.toJson(addParentReplyReqDto);

        //when, then
        this.mockMvc.perform(post("/parent-replies",1L)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.result.content").value("내용을 입력해주세요"))
                .andDo(print());

        verify(replyService,times(0))
                .addParentReply(any(String.class),any(AddParentReplyReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 등록 테스트 -> 실패(snapId 공백)")
    public void addParentReplyTest3() throws Exception {
        //given
        Gson gson = new Gson();
        AddParentReplyReqDto addParentReplyReqDto =
                new AddParentReplyReqDto("댓글내용",null);
        String requestBody = gson.toJson(addParentReplyReqDto);

        //when, then
        this.mockMvc.perform(post("/parent-replies")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.result.snapId").value("snapId를 입력해주세요."))
                .andDo(print());

        verify(replyService,times(0))
                .addParentReply(any(String.class),any(AddParentReplyReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 등록 테스트 -> 실패(존재하지 않는 snap)")
    public void addParentReplyTest4() throws Exception {
        //given
        Gson gson = new Gson();
        AddParentReplyReqDto addParentReplyReqDto =
                new AddParentReplyReqDto("댓글내용",1L);
        String requestBody = gson.toJson(addParentReplyReqDto);

        doThrow(new CustomException(ExceptionCode.SNAP_NOT_EXIST))
                .when(replyService).addParentReply(any(String.class),any(AddParentReplyReqDto.class));

        //when, then
        this.mockMvc.perform(post("/parent-replies")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("스냅이 존재하지 않습니다."))
                .andDo(print());

        verify(replyService,times(1))
                .addParentReply(any(String.class),any(AddParentReplyReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("대댓글 등록 테스트 -> 성공")
    public void addChildReplyTest1() throws Exception {
        //given
        Gson gson = new Gson();
        AddChildReplyReqDto addChildReplyReqDto =
                new AddChildReplyReqDto("댓글내용",1L,"태그유저loginId");
        String requestBody = gson.toJson(addChildReplyReqDto);

        //when, then
        this.mockMvc.perform(post("/child-replies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("대댓글등록이 성공했습니다."))
                .andDo(print());

        verify(replyService,times(1))
                .addChildReply(any(String.class),any(AddChildReplyReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("대댓글 등록 테스트 -> 실패(유효성검사 실패)")
    public void addChildReplyTest2() throws Exception {
        //given
        Gson gson = new Gson();
        AddChildReplyReqDto addChildReplyReqDto =
                new AddChildReplyReqDto("",null,"");
        String requestBody = gson.toJson(addChildReplyReqDto);

        //when, then
        this.mockMvc.perform(post("/child-replies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.result.parentReplyId").value("parentReplyId를 입력해주세요."))
                .andExpect(jsonPath("$.result.content").value("내용을 입력해주세요"))
                .andDo(print());

        verify(replyService,times(0))
                .addChildReply(any(String.class),any(AddChildReplyReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("대댓글 등록 테스트 -> 실패(태그할 유저의 loginId와 맞는 유저가 존재하지 않음)")
    public void addChildReplyTest3() throws Exception {
        //given
        Gson gson = new Gson();
        AddChildReplyReqDto addChildReplyReqDto =
                new AddChildReplyReqDto("댓글내용",1L,"태그유저loginId");
        String requestBody = gson.toJson(addChildReplyReqDto);

        doThrow(new CustomException(ExceptionCode.USER_NOT_EXIST))
                .when(replyService).addChildReply(any(String.class),any(AddChildReplyReqDto.class));

        //when, then
        this.mockMvc.perform(post("/child-replies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("사용자가 존재하지 않습니다."))
                .andDo(print());

        verify(replyService,times(1))
                .addChildReply(any(String.class),any(AddChildReplyReqDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("대댓글 등록 테스트 -> 실패(부모댓글이 존재하지 않음)")
    public void addChildReplyTest4() throws Exception {
        //given
        Gson gson = new Gson();
        AddChildReplyReqDto addChildReplyReqDto =
                new AddChildReplyReqDto("댓글내용",1L,"태그유저loginId");
        String requestBody = gson.toJson(addChildReplyReqDto);

        doThrow(new CustomException(ExceptionCode.REPLY_NOT_FOUND))
                .when(replyService).addChildReply(any(String.class),any(AddChildReplyReqDto.class));

        //when, then
        this.mockMvc.perform(post("/child-replies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("존재하지 않는 댓글입니다."))
                .andDo(print());

        verify(replyService,times(1))
                .addChildReply(any(String.class),any(AddChildReplyReqDto.class));
    }
}
