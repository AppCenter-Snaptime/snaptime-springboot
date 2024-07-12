package me.snaptime.snap.service;

import com.querydsl.core.Tuple;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.snap.dto.res.FindSnapPagingResDto;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snap.service.impl.SnapPagingServiceImpl;
import me.snaptime.snapLike.service.SnapLikeService;
import me.snaptime.snapTag.service.SnapTagService;
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

import static me.snaptime.snap.domain.QSnap.snap;
import static me.snaptime.user.domain.QUser.user;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SnapPagingServiceImplTest {

    @InjectMocks
    private SnapPagingServiceImpl snapPagingServiceImpl;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SnapRepository snapRepository;
    @Mock
    private UrlComponent urlComponent;
    @Mock
    private SnapTagService snapTagService;
    @Mock
    private SnapLikeService snapLikeService;
    @Mock
    private NextPageChecker nextPageChecker;
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
        given(urlComponent.makeProfileURL(any(Long.class)))
                .willReturn("profile1")
                .willReturn("profile2")
                .willReturn("profile3");
        given(urlComponent.makePhotoURL(any(String.class),any(Boolean.class)))
                .willReturn("photoURL1")
                .willReturn("photoURL2")
                .willReturn("photoURL3");
        given(tuple1.get(snap.id)).willReturn(1L);
        given(tuple2.get(snap.id)).willReturn(2L);
        given(tuple3.get(snap.id)).willReturn(3L);
        given(tuple1.get(user.profilePhoto.id)).willReturn(1L);
        given(tuple2.get(user.profilePhoto.id)).willReturn(2L);
        given(tuple3.get(user.profilePhoto.id)).willReturn(3L);
        given(tuple1.get(snap.oneLineJournal)).willReturn("일기1");
        given(tuple2.get(snap.oneLineJournal)).willReturn("일기2");
        given(tuple3.get(snap.oneLineJournal)).willReturn("일기3");
        given(tuple1.get(snap.fileName)).willReturn("fileName1");
        given(tuple2.get(snap.fileName)).willReturn("fileName2");
        given(tuple3.get(snap.fileName)).willReturn("fileName3");
        given(userRepository.findByLoginId(any(String.class))).willReturn(Optional.ofNullable(reqUser));
        given(snapRepository.findSnapPaging(any(String.class),any(Long.class),any(User.class))).willReturn(List.of(tuple1,tuple2,tuple3));

        // when
        FindSnapPagingResDto result = snapPagingServiceImpl.findSnapPaging("testLoginId",1L);

        // then
        assertThat(result.snapPagingInfoList().size()).isEqualTo(3);
        assertThat(result.snapPagingInfoList().get(0).snapId()).isEqualTo(1);
        assertThat(result.snapPagingInfoList().get(1).snapId()).isEqualTo(2);
        assertThat(result.snapPagingInfoList().get(2).snapId()).isEqualTo(3);

        assertThat(result.snapPagingInfoList().get(0).oneLineJournal()).isEqualTo("일기1");
        assertThat(result.snapPagingInfoList().get(1).oneLineJournal()).isEqualTo("일기2");
        assertThat(result.snapPagingInfoList().get(2).oneLineJournal()).isEqualTo("일기3");

        assertThat(result.snapPagingInfoList().get(0).snapPhotoURL()).isEqualTo("photoURL1");
        assertThat(result.snapPagingInfoList().get(1).snapPhotoURL()).isEqualTo("photoURL2");
        assertThat(result.snapPagingInfoList().get(2).snapPhotoURL()).isEqualTo("photoURL3");

        verify(snapRepository,times(1)).findSnapPaging(any(String.class),any(Long.class),any(User.class));
        verify(userRepository,times(1)).findByLoginId(any(String.class));

    }

}
