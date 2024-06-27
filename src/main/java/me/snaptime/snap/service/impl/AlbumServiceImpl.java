package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Album;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.dto.req.CreateAlbumReqDto;
import me.snaptime.snap.data.dto.res.GetAllAlbumListResDto;
import me.snaptime.snap.data.dto.res.FindAlbumResDto;
import me.snaptime.snap.data.dto.res.FindAllAlbumsResDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.service.AlbumService;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
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

    @Value(value = "${nonClassification.name}")
    private String nonClassificationName;

    @Override
    @Transactional(readOnly = true)
    public List<FindAllAlbumsResDto> findAllAlbumsByLoginId(String uid) {
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        List<Album> foundAlbums = albumRepository.findAlbumsByUser(foundUser);
        return foundAlbums.stream().map(album -> {
            Optional<Snap> firstSnapOptional = album.getSnap().isEmpty() ? Optional.empty() : Optional.of(album.getSnap().get(0));
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
    public FindAlbumResDto findAlbum(String uId, Long album_id) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        haveAuthority(uId, foundAlbum);
        return FindAlbumResDto.builder()
                .id(foundAlbum.getId())
                .name(foundAlbum.getName())
                .snap(foundAlbum.getSnap().stream()
                        .sorted(Comparator.comparing(Snap::getId).reversed())
                        .map(snap ->
                                FindSnapResDto.entityToResDto(
                                        snap,
                                        urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate()),
                                        urlComponent.makeProfileURL(snap.getUser().getProfilePhoto().getId())
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
    public boolean isNonClassificationExist(String uId) {
        User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        List<Album> foundAlbums = albumRepository.findAlbumsByUser(foundUser);
        return foundAlbums.stream().anyMatch(album -> Objects.equals(album.getName(), nonClassificationName));
    }

    @Override
    public Long findUserNonClassificationId(String uid) {
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        List<Album> foundAlbums = albumRepository.findAlbumsByUser(foundUser);
        return foundAlbums.stream().filter(album -> Objects.equals(album.getName(), nonClassificationName)).findFirst().map(Album::getId).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetAllAlbumListResDto> getAlbumListByLoginId(String uid) {
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        return albumRepository.findAlbumsByUser(foundUser).stream().map(album -> GetAllAlbumListResDto.builder().id(album.getId()).name(album.getName()).build()).toList();
    }

    @Override
    @Transactional
    public void createAlbum(CreateAlbumReqDto createAlbumReqDto, String uid) {
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        albumRepository.save(Album.builder().name(createAlbumReqDto.name()).user(foundUser).build());
    }

    @Override
    public Long createNonClassificationAlbum(String uid) {
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        Album result = albumRepository.save(Album.builder().name(nonClassificationName).user(foundUser).build());
        return result.getId();
    }

    @Override
    @Transactional
    public void modifyAlbumName(Long album_id, String album_name) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        foundAlbum.updateAlbumNameByString(album_name);
    }

    @Override
    public boolean isUserHavePermission(Long album_id, String uid) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        return foundAlbum.getUser() == foundUser;
    }

    @Override
    @Transactional
    public void removeAlbum(String uId, Long album_id) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        haveAuthority(uId, foundAlbum);
    }

    /*
    * 인자로 uId와 album을 받아 album을 생성한 사용자가 현재 요청을 보낸 사용자와 일치하는지 확인하는 메소드입니다.
    * */
    private void haveAuthority(String uId, Album album) {
        User foundUser = userRepository.findByLoginId(uId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        if(!(album.getUser() == foundUser)){
            throw new CustomException(ExceptionCode.ALBUM_USER_NOT_MATCH);
        }
    }
}
