package me.snaptime.album.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.album.domain.Album;
import me.snaptime.album.dto.req.CreateAlbumReqDto;
import me.snaptime.album.dto.res.FindAlbumResDto;
import me.snaptime.album.dto.res.FindAllAlbumsResDto;
import me.snaptime.album.dto.res.GetAllAlbumListResDto;
import me.snaptime.album.repository.AlbumRepository;
import me.snaptime.album.service.AlbumService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.dto.res.SnapInfoResDto;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final UrlComponent urlComponent;
    private final SnapRepository snapRepository;

    @Value(value = "${nonClassification.name}")
    private String nonClassificationName;

    @Override
    @Transactional(readOnly = true)
    public List<FindAllAlbumsResDto> findAllAlbumsByEmail(String userEmail) {
        User foundUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        List<Album> foundAlbums = albumRepository.findAlbumsByUser(foundUser);
        return foundAlbums.stream().map(album -> {
            int lastNumber = album.getSnap().size();
            Optional<Snap> firstSnapOptional = album.getSnap().isEmpty() ? Optional.empty() : Optional.of(album.getSnap().get(lastNumber-1));
            String photoUrl = firstSnapOptional.map(snap -> urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate())).orElse(null);
            return FindAllAlbumsResDto.builder()
                    .id(album.getId())
                    .name(album.getName())
                    .photoUrl(photoUrl)
                    .build();
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FindAlbumResDto findAlbum(String userEmail, Long album_id) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        User foundUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        try {
            isUserHavePermission(foundUser, album_id);
        } catch (CustomException e) {
            /*
            * 유저가 없거나, 권한이 없으면 공개인 것만 유저에게 반환한다.
            * */
            return FindAlbumResDto.builder()
                    .id(foundAlbum.getId())
                    .name(foundAlbum.getName())
                    .snap(foundAlbum.getSnap().stream()
                            .sorted(Comparator.comparing(Snap::getId).reversed())
                            .filter( snap -> !snap.isPrivate())
                            .map(snap ->
                                    SnapInfoResDto.entityToResDto(
                                            snap,
                                            urlComponent.makePhotoURL(snap.getFileName(), false),
                                            urlComponent.makeProfileURL(snap.getUser().getProfilePhoto().getProfilePhotoId())
                                    )
                            )
                            .collect(Collectors.toList()))
                    .build();
        }

        return FindAlbumResDto.builder()
                .id(foundAlbum.getId())
                .name(foundAlbum.getName())
                .snap(foundAlbum.getSnap().stream()
                        .sorted(Comparator.comparing(Snap::getId).reversed())
                        .map(snap ->
                                SnapInfoResDto.entityToResDto(
                                        snap,
                                        urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate()),
                                        urlComponent.makeProfileURL(snap.getUser().getProfilePhoto().getProfilePhotoId())
                                )
                        )
                        .collect(Collectors.toList()))
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAlbumExistById(Long album_id) {
       return albumRepository.findById(album_id).isPresent();
    }

    @Override
    public boolean isNonClassificationExist(User user) {
        List<Album> foundAlbums = albumRepository.findAlbumsByUser(user);
        return foundAlbums.stream().anyMatch(album -> Objects.equals(album.getName(), nonClassificationName));
    }

    @Override
    public Long findUserNonClassificationId(User user) {
        List<Album> foundAlbums = albumRepository.findAlbumsByUser(user);
        return foundAlbums.stream().filter(album -> Objects.equals(album.getName(), nonClassificationName)).findFirst().map(Album::getId).orElseThrow(() -> new CustomException(ExceptionCode.NON_CLASSIFICATION_ALBUM_IS_NOT_EXIST));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetAllAlbumListResDto> getAlbumListByEmail(String userEmail) {
        User foundUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        return albumRepository.findAlbumsByUser(foundUser).stream().map(album -> GetAllAlbumListResDto.builder().id(album.getId()).name(album.getName()).build()).toList();
    }

    @Override
    @Transactional
    public void createAlbum(CreateAlbumReqDto createAlbumReqDto, String userEmail) {
        User foundUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        albumRepository.save(Album.builder().name(createAlbumReqDto.name()).user(foundUser).build());
    }

    @Override
    public Long createNonClassificationAlbum(User user) {
        Album result = albumRepository.save(Album.builder().name(nonClassificationName).user(user).build());
        return result.getId();
    }

    @Override
    @Transactional
    public void modifyAlbumName(Long album_id, String album_name) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        foundAlbum.updateAlbumNameByString(album_name);
    }

    @Override
    @Transactional
    public void removeAlbum(String userEmail, Long album_id) {
        User foundUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        isUserHavePermission(foundUser, album_id);
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        // 사용자가 가지고 있는 non-classification id를 가져온다.
        Long foundNonClassificationId = findUserNonClassificationId(foundUser);
        if  (foundNonClassificationId.equals(album_id)) {
            throw new CustomException(ExceptionCode.NOT_DELETE_NON_CLASSIFICATION_ALBUM);
        }
        Album nonClassificationAlbum = albumRepository.findById(foundNonClassificationId).orElseThrow(() -> new CustomException(ExceptionCode.NON_CLASSIFICATION_ALBUM_IS_NOT_EXIST));
        // 앨범과 연관관계가 맺어져있는 snap들의 목록을 가져온다
        List<Snap> snapList = foundAlbum.getSnap();
        for (Snap snap: snapList) {
            // non-classification Snap과 앨범을 연관관계 맺어준다.
            snap.associateAlbum(nonClassificationAlbum);
        }
        // DB에 반영한다.
        snapRepository.saveAll(snapList);
        albumRepository.delete(foundAlbum);
    }

    /*
    * 인자로 uId와 album을 받아 album을 생성한 사용자가 현재 요청을 보낸 사용자와 일치하는지 확인하는 메소드입니다.
    * */
    @Override
    public void isUserHavePermission(User user, Long album_id) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        if(!(Objects.equals(foundAlbum.getUser().getUserId(), user.getUserId()))){
            throw new CustomException(ExceptionCode.ALBUM_USER_NOT_MATCH);
        }
    }
}
