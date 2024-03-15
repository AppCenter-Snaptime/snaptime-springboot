package me.snaptime.user.data.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.dto.response.userprofile2.AlbumSnapResDto;
import me.snaptime.user.data.repository.UserCustomRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.snaptime.snap.data.domain.QAlbum.album;
import static me.snaptime.snap.data.domain.QSnap.snap;
import static me.snaptime.user.data.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    //유저 앨범들과 앨범에 해당하는 스냅들 가져오는 메서드.
    @Override
    public List<AlbumSnapResDto> fidAlbumSnap(User reqUser) {
        List<Tuple> tuples1 = jpaQueryFactory
                .select(album.id, album.name, snap.id)
                .from(user)
                .join(snap).on(user.id.eq(snap.user.id))
                .join(album).on(snap.album.id.eq(album.id))
                .where(user.id.eq(reqUser.getId()))
                .fetch();

        //중복되는 album이름을 가진 튜플들을 중복 없이 distinctTuples에 넣는다.
        Set<Long> albumSet = new HashSet<>();

        for (Tuple tuple : tuples1) {
            Long albumId = tuple.get(album.id);
            albumSet.add(albumId);
        }

        List<Long> albumIdList = new ArrayList<>(albumSet);

        List<AlbumSnapResDto> albumSnaps = new ArrayList<>();

        for (Long albumId : albumIdList) {
            List<Tuple> tuples2 = jpaQueryFactory
                    .select(album.id, album.name, snap.id)
                    .from(user)
                    .join(snap).on(user.id.eq(snap.user.id))
                    .join(album).on(snap.album.id.eq(album.id))
                    .where(album.id.eq(albumId))
                    .orderBy(snap.createdDate.desc()) // 최근 생성일 기준으로 내림차순 정렬
                    .limit(2) // 최근 생성된 사진 2개만 선택
                    .fetch();

            List<Long> snapIdList = new ArrayList<>();

            tuples2.forEach(tuple -> {
                snapIdList.add(tuple.get(snap.id));
            });

            String albumName = tuples2.get(0).get(album.name);

            albumSnaps.add(AlbumSnapResDto.builder()
                    .albumId(albumId)
                    .albumName(albumName)
                    .snapIdList(snapIdList)
                    .build());
        }

        return albumSnaps;
    }

    /* 유저 프로필 cnt 내용 제외 전부 불러오는 메서드 */
    //    @Override
//    public UserProfileResDto findUserProfile1(User reqUser) {
//
//        //해당 튜플에, 유저 id, 유저 이름, 유저 프로필 사진 id, 앨범 id, 앨범 이름이 들어가있다.
//        List<Tuple> tuples1 = jpaQueryFactory
//                .select(user.id, user.name, user.profilePhoto.id, album.id, album.name)
//                .from(user)
//                .join(snap).on(user.id.eq(snap.user.id))
//                .rightJoin(album).on(snap.album.id.eq(album.id))
//                .where(user.id.eq(reqUser.getId()))
//                .fetch();
//
//
//        //중복되는 album이름을 가진 튜플들을 중복 없이 distinctTuples에 넣는다.
//        Set<Long> uniqueAlbumIds = new HashSet<>();
//        List<Tuple> distinctTuples = new ArrayList<>();
//
//        for (Tuple tuple : tuples1) {
//            Long albumId = tuple.get(album.id);
//            // album.id가 이미 존재하는지 확인 후 없다면 리스트에 추가
//            // 이로서 album이 중복되는것을 막을 수 있다.
//            if (!uniqueAlbumIds.contains(albumId)) {
//                uniqueAlbumIds.add(albumId);
//                distinctTuples.add(tuple);
//            }
//        }
//
//        //해당 유저에 해당하는 album을 list형식으로 받기 위함.
//        List<Long> albums = new ArrayList<>();
//        //튜플을 순회하며 album이름들을 list형식으로 추가한다. 위에서 중복되는 album이름을 제거했기에 순차적으로 리스트를 채운다.
//        distinctTuples.forEach(tuple -> {
//            albums.add(tuple.get(album.id));
//        });
//
//        //해당하는 유저는 1명이기 때문에. 첫번쨰 튜플에서 유저 id, 유저 이름, 프로필 사진 id, 앨범 이름을 가져온다.
//        List<AlbumAndPhotosResDto> albumPhotos = new ArrayList<>();
//
//
//        //앨범의 개수를 알 수 없기 때문에, 반복문 사용
//        for (Long albumId : albums) {
//            List<Tuple> tuples2 = jpaQueryFactory
//                    .select(album.id, album.name, photo.id)
//                    .from(album)
//                    .rightJoin(snap).on(album.id.eq(snap.album.id))
//                    .rightJoin(photo).on(snap.photo.id.eq(photo.id))
//                    .where(album.id.eq(albumId))
//                    .orderBy(snap.createdDate.desc()) // 최근 생성일 기준으로 내림차순 정렬
//                    .limit(2) // 최근 생성된 사진 2개만 선택
//                    .fetch();
//
//            //앨범 당 사진들을 담을 List
//            List<Long> recentPhotos = new ArrayList<>();
//
//            //해당 앨범의 최근 사진 2개를 리스트에 추가한다.
//            tuples2.forEach(tuple -> {
//                recentPhotos.add(tuple.get(photo.id));
//            });
//
//            //앨범 이름도 해당 for문당 하나이기에 그냥 첫번째 튜플에서 가져온다
//            String albumName = tuples2.get(0).get(album.name);
//
//            //앨범과 해당 앨범의 사진을 짝지어서 dto 리스트 형식으로 만든다.
//            albumPhotos.add(AlbumAndPhotosResDto.builder()
//                    .albumId(albumId)
//                    .albumName(albumName)
//                    .photoIdList(recentPhotos)
//                    .build());
//        }
//
//        //해당하는 유저는 1명이기 때문에. 첫번쨰 튜플에서 유저 id, 유저 이름, 프로필 사진 id, 앨범 이름을 가져온다.
//        Tuple onlyOneUser = tuples1.get(0);
//        Long userId = onlyOneUser.get(user.id);
//        String userName = onlyOneUser.get(user.name);
//        Long profilePhotoId = onlyOneUser.get(user.profilePhoto.id);
//
//        return UserProfileResDto.builder()
//                .userId(userId)
//                .userName(userName)
//                .profilePhotoId(profilePhotoId)
//                .albumAndPhotos(albumPhotos) //해당 유저의 앨범-사진들 dto리스트
//                .build();
//    }
}


