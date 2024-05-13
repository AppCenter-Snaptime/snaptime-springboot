package me.snaptime.user.data.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.response.userprofile.AlbumSnapResDto;
import me.snaptime.user.data.dto.response.userprofile.ProfileCntResDto;
import me.snaptime.user.data.repository.UserCustomRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static me.snaptime.snap.data.domain.QAlbum.album;
import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.user.data.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final UrlComponent urlComponent;

    //유저 앨범들과 앨범에 해당하는 스냅들 가져오는 메서드.
    @Override
    public List<AlbumSnapResDto> fidAlbumSnap(User reqUser) {
        List<Long> albumIdList = jpaQueryFactory
                .select(album.id).distinct()
                .from(user)
                .join(snap).on(user.id.eq(snap.user.id))
                .join(album).on(snap.album.id.eq(album.id))
                .where(user.id.eq(reqUser.getId()))
                .fetch();

        List<AlbumSnapResDto> albumSnapResDtoList = new ArrayList<>();

        for (Long albumId : albumIdList) {
            List<Tuple> albumSnapTwo = jpaQueryFactory
                    .select(album.name, snap.fileName, snap.isPrivate)
                    .from(snap)
                    .join(album).on(snap.album.id.eq(album.id))
                    .where(album.id.eq(albumId))
                    .orderBy(snap.createdDate.desc()) // 최근 생성일 기준으로 내림차순 정렬
                    .limit(2) // 최근 생성된 사진 2개만 선택
                    .fetch();


            //stream 사용하는 걸로 수정
            List<String> snapUrlList = albumSnapTwo.stream()
                    .map(tuple -> {
                        String fileName = tuple.get(snap.fileName);
                        Boolean isPrivate = tuple.get(snap.isPrivate);
                        return urlComponent.makePhotoURL(fileName, isPrivate);
                    })
                    .toList();

            String albumName = albumSnapTwo.get(0).get(album.name);

            albumSnapResDtoList.add(AlbumSnapResDto.builder()
                    .albumId(albumId)
                    .albumName(albumName)
                    .snapUrlList(snapUrlList)
                    .build());
        }

        return albumSnapResDtoList;
    }
}


