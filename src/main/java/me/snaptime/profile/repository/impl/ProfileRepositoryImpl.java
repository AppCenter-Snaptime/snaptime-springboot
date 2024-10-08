package me.snaptime.profile.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.profile.dto.res.AlbumSnapResDto;
import me.snaptime.profile.dto.res.ProfileTagSnapResDto;
import me.snaptime.profile.repository.ProfileRepository;
import me.snaptime.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.snaptime.album.domain.QAlbum.album;
import static me.snaptime.snap.domain.QSnap.snap;
import static me.snaptime.snapTag.domain.QSnapTag.snapTag;
import static me.snaptime.user.domain.QUser.user;


@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final UrlComponent urlComponent;

    //유저 앨범들과 앨범에 해당하는 스냅들 가져오는 메서드.
    //앨범에 snap이 존재하지 않아도 return 할 list에 album만 추가하고 나머지 null처리
    //snap에 isPrivate이 존재한다, 내가 조회 -> 전부 리턴 | 남이 조회 -> isPrivate= True 인 snap 제외
    @Override
    public List<AlbumSnapResDto> findAlbumSnap(User targetUser, Boolean checkPermission) {
        List<Tuple> albums = jpaQueryFactory
                .select(album.id, album.name).distinct()
                .from(user)
                .join(album).on(user.userId.eq(album.user.userId))
                .where(user.userId.eq(targetUser.getUserId()))
                .fetch();

        Map<Long,String> albumMap = new HashMap<>();

        albums.forEach(tuple ->{
            Long albumId = tuple.get(album.id);
            String albumName = tuple.get(album.name);
            albumMap.put(albumId,albumName);
        });

        List<AlbumSnapResDto> albumSnapResDtos = new ArrayList<>();

        //snap이 없어도 album은 존재할 수 있기 때문에 album 수 만큼 반복한다.
        for (Long albumId : albumMap.keySet()) {
            List<Tuple> albumSnapTwo = jpaQueryFactory
                    .select(album.name, snap.fileName, snap.isPrivate)
                    .from(album)
                    .leftJoin(snap).on(album.id.eq(snap.album.id))
                    .where(whereBuilder(albumId, checkPermission))
                    .orderBy(snap.createdDate.desc()) // 최근 생성일 기준으로 내림차순 정렬
                    .limit(2) // 최근 생성된 사진 2개만 선택
                    .fetch();

            //stream 사용하는 걸로 수정
            List<String> snapUrls = albumSnapTwo.stream()
                    .map(tuple -> {
                        String fileName = tuple.get(snap.fileName);
                        Boolean isPrivate = tuple.get(snap.isPrivate);
                        return urlComponent.makePhotoURL(fileName, isPrivate);
                    })
                    .toList();

            //다른 사람의 프로필 검색 일 때, snap이 없거나, private이면 앨범도 private
            if(!checkPermission && snapUrls.isEmpty()){
                continue;
            }

            String albumName = albumMap.get(albumId);

            albumSnapResDtos.add(AlbumSnapResDto.builder()
                    .albumId(albumId)
                    .albumName(albumName)
                    .snapUrlList(snapUrls)
                    .build());
        }

        return albumSnapResDtos;
    }

    @Override
    public List<ProfileTagSnapResDto> findTagSnap(User targetUser) {
        List<Tuple> tagSnaps = jpaQueryFactory
                .select(snap.id,snap.user.email, snap.fileName, snap.isPrivate, snap.createdDate).distinct()
                .from(snap)
                .join(snapTag).on(snapTag.snap.id.eq(snap.id))
                .where(snapTag.tagUser.email.eq(targetUser.getEmail()))
                .orderBy(snap.createdDate.desc())
                .fetch();

        List<ProfileTagSnapResDto> tagSnapUrls = tagSnaps.stream()
                .map(tuple -> {
                    return ProfileTagSnapResDto.builder()
                            .taggedSnapId(tuple.get(snap.id))
                            .snapOwnEmail(tuple.get(snap.user.email))
                            .taggedSnapUrl(urlComponent.makePhotoURL(tuple.get(snap.fileName), tuple.get(snap.isPrivate)))
                            .build();
                })
                .toList();

        return tagSnapUrls;
    }

    // 자신이 자신의 profile을 조회할 때, 자신이 다른사람의 profile을 조회할 때를 구별하기 위함.
    private BooleanBuilder whereBuilder(Long albumId, Boolean checkPermission){
        BooleanBuilder builder = new BooleanBuilder();

        if(checkPermission){
            builder.and(album.id.eq(albumId));
            builder.and(snap.fileName.isNotNull());
        }
        else{
            builder.and(album.id.eq(albumId));
            builder.and(snap.isPrivate.isFalse());
            builder.and(snap.fileName.isNotNull());
        }
        return builder;
    }
}


