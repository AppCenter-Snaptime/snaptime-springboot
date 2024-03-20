package me.snaptime.snap.service;

import com.querydsl.core.Tuple;
import me.snaptime.snap.data.dto.res.FindSnapPagingResDto;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.snap.service.impl.SnapPagingServiceImpl;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static me.snaptime.snap.data.domain.QSnap.snap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SnapPagingServiceImplTest {

    @InjectMocks
    private SnapPagingServiceImpl snapPagingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SnapRepository snapRepository;
    private User reqUser;

    @BeforeEach
    void beforeEach(){
        reqUser = User.builder()
                .build();
    }

    @Test
    @DisplayName("스냅 페이징조회테스트 -> 성공")
    public void findSnapPagingTest1(){
        // given
        Tuple tuple1 = mock(Tuple.class);
        Tuple tuple2 = mock(Tuple.class);
        Tuple tuple3 = mock(Tuple.class);
        given(tuple1.get(snap.id)).willReturn(1L);
        given(tuple2.get(snap.id)).willReturn(2L);
        given(tuple3.get(snap.id)).willReturn(3L);
        given(tuple1.get(snap.oneLineJournal)).willReturn("일기1");
        given(tuple2.get(snap.oneLineJournal)).willReturn("일기2");
        given(tuple3.get(snap.oneLineJournal)).willReturn("일기3");
        given(tuple1.get(snap.fileName)).willReturn("fileName1");
        given(tuple2.get(snap.fileName)).willReturn("fileName2");
        given(tuple3.get(snap.fileName)).willReturn("fileName3");
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(reqUser));
        given(snapRepository.findSnapPaging(any(String.class),any(Long.class),any(User.class))).willReturn(List.of(tuple1,tuple2,tuple3));

        // when
        List<FindSnapPagingResDto> result = snapPagingService.findSnapPaging("testLoginId",1L);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).snapId()).isEqualTo(1);
        assertThat(result.get(1).snapId()).isEqualTo(2);
        assertThat(result.get(2).snapId()).isEqualTo(3);

        assertThat(result.get(0).oneLineJournal()).isEqualTo("일기1");
        assertThat(result.get(1).oneLineJournal()).isEqualTo("일기2");
        assertThat(result.get(2).oneLineJournal()).isEqualTo("일기3");

        assertThat(result.get(0).fileName()).isEqualTo("fileName1");
        assertThat(result.get(1).fileName()).isEqualTo("fileName2");
        assertThat(result.get(2).fileName()).isEqualTo("fileName3");

        verify(snapRepository,times(1)).findSnapPaging(any(String.class),any(Long.class),any(User.class));
        verify(userRepository,times(1)).findByLoginId(any(String.class));
    }

}
