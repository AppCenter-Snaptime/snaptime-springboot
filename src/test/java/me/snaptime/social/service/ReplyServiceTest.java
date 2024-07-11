package me.snaptime.social.service;

import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.reply.domain.ChildReply;
import me.snaptime.reply.domain.ParentReply;
import me.snaptime.reply.dto.req.AddChildReplyReqDto;
import me.snaptime.reply.dto.req.AddParentReplyReqDto;
import me.snaptime.reply.repository.ChildReplyRepository;
import me.snaptime.reply.repository.ParentReplyRepository;
import me.snaptime.reply.service.ReplyService;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.NextPageChecker;
import me.snaptime.util.TimeAgoCalculator;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.fail;

@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {

    @InjectMocks
    private ReplyService replyService;
    @Mock
    private ParentReplyRepository parentReplyRepository;
    @Mock
    private ChildReplyRepository childReplyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SnapRepository snapRepository;
    @Mock
    private NextPageChecker nextPageChecker;
    @Mock
    private TimeAgoCalculator timeAgoCalculator;

    private User user;
    private Snap snap;
    private ParentReply parentReply;

    @BeforeEach
    void beforeEach(){
        user = User.builder()
                .loginId("loginId1")
                .name("user1")
                .email("email1@google.com")
                .build();
        snap = Snap.builder()
                .build();
        parentReply = ParentReply.builder()
                .build();
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 성공")
    public void addParentReplyTest1(){
        //given
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user));
        given(snapRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(snap));

        //when
        replyService.addParentReply("loginId", new AddParentReplyReqDto("댓글내용",1L));

        //then
        verify(parentReplyRepository,times(1)).save(any(ParentReply.class));
        verify(snapRepository,times(1)).findById(any(Long.class));
        verify(userRepository,times(1)).findByLoginId(any(String.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 실패(존재하지 않는 snap)")
    public void addParentReplyTest2(){
        //given
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user));
        given(snapRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when
        try{
            replyService.addParentReply("loginId",new AddParentReplyReqDto("댓글내용",1L));
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.SNAP_NOT_EXIST);
            verify(parentReplyRepository,times(0)).save(any(ParentReply.class));
            verify(snapRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(1)).findByLoginId(any(String.class));
        }
    }

    @Test
    @DisplayName("대댓글 등록 테스트 -> 성공")
    public void addChildReplyTest1(){
        //given
        AddChildReplyReqDto addChildReplyReqDto =
                new AddChildReplyReqDto("댓글내용",1L,"태그유저loginId");
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user));
        given(parentReplyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(parentReply));

        //when
        replyService.addChildReply("loginId",addChildReplyReqDto);

        //then
        verify(parentReplyRepository,times(1)).findById(any(Long.class));
        verify(userRepository,times(2)).findByLoginId(any(String.class));
        verify(childReplyRepository,times(1)).save(any(ChildReply.class));
    }

    @Test
    @DisplayName("대댓글 등록 테스트 -> 실패(태그할 유저LoginId와 연결되는 유저가 존재하지 않음)")
    public void addChildReplyTest2(){
        //given
        AddChildReplyReqDto addChildReplyReqDto =
                new AddChildReplyReqDto("댓글내용",1L,"태그유저loginId");
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.empty());

        //when
        try{
            replyService.addChildReply("loginId",addChildReplyReqDto);
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_EXIST);
            verify(parentReplyRepository,times(0)).findById(any(Long.class));
            verify(userRepository,times(1)).findByLoginId(any(String.class));
            verify(childReplyRepository,times(0)).save(any(ChildReply.class));
        }
    }

    @Test
    @DisplayName("대댓글 등록 테스트 -> 실패(부모댓글이 존재하지 않음)")
    public void addChildReplyTest3(){
        //given
        AddChildReplyReqDto addChildReplyReqDto =
                new AddChildReplyReqDto("댓글내용",1L,"태그유저loginId");
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(user));
        given(parentReplyRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when
        try{
            replyService.addChildReply("loginId",addChildReplyReqDto);
            fail("예외가 발생하지 않음");
        }catch (CustomException ex){
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.REPLY_NOT_FOUND);
            verify(parentReplyRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(1)).findByLoginId(any(String.class));
            verify(childReplyRepository,times(0)).save(any(ChildReply.class));
        }
    }
}
