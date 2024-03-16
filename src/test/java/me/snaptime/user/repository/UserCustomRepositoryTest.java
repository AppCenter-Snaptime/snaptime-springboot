package me.snaptime.user.repository;

import me.snaptime.common.config.JpaAuditingConfig;
import me.snaptime.common.config.QueryDslConfig;
import me.snaptime.user.data.domain.ProfilePhoto;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.ProfilePhotoRepository;
import me.snaptime.user.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
//왜인지는 모르겠으나, main의 applicaiton.yml 파일에 영향을 받아, mysql설정이 적용된 상태에서 h2에 접근하다가 에러가 발생한 듯 보인다. 그래서 test-app..yml 파일의 우선순위를 높였다.
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public class UserCustomRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfilePhotoRepository profilePhotoRepository;

//    @Autowired
//    private AlbumRepository albumRepository;
//    @Autowired
//    private SnapRepository snapRepository;
//    @Autowired
//    private PhotoRepository photoRepository;

    @BeforeEach
    void init(){
        ProfilePhoto profilePhoto = ProfilePhoto.builder()
                .profilePhotoName("testProfile.png")
                .profilePhotoPath("/testProfile.png")
                .build();
        profilePhotoRepository.save(profilePhoto);

        User user = User.builder()
                .name("홍길순")
                .loginId("kang4746")
                .password("test1234")
                .email("strong@naver.com")
                .birthDay("1999-10-29")
                .profilePhoto(profilePhoto)
                .build();
        userRepository.save(user);

//        Album album1 = Album.builder()
//                .name("testAlbum1")
//                .build();
//        Album album2 = Album.builder()
//                .name("testAlbum2")
//                .build();
//        albumRepository.saveAll(List.of(album1,album2));
//
//        List<Snap> snaps = new ArrayList<>();
//
//        for (int i = 1; i<=4; i++){
//            Album album = (i % 2 == 0) ? album1 : album2;
//
//            Photo photo = Photo.builder()
//                    .fileName("testPhoto"+i)
//                    .filePath("/testPhoto"+i)
//                    .fileType("testPhoto/png")
//                    .build();
//
//            photoRepository.save(photo);
//            Snap snap = Snap.builder()
//                    .album(album)
//                    .oneLineJournal("testJournal"+i)
//                    .user(user)
//                    .photo(photo)
//                    .build();
//            snaps.add(snap);
//        }
//        snapRepository.saveAll(snaps);
    }

    @Test
    @DisplayName("유저 앨범들과 앨범에 해당하는 스냅들 불러오기 리포지토리 메서드 성공 테스트")
    public void findAlbumSnapTest(){
        //given



        //when

    }

}
