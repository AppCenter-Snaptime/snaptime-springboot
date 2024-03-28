package me.snaptime.snap.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.component.UrlComponent;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Album;
import me.snaptime.snap.data.dto.req.CreateAlbumReqDto;
import me.snaptime.snap.data.dto.res.FindAlbumInfoResDto;
import me.snaptime.snap.data.dto.res.FindAlbumResDto;
import me.snaptime.snap.data.repository.AlbumRepository;
import me.snaptime.snap.service.AlbumService;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final UrlComponent urlComponent;
    @Override
    @Transactional(readOnly = true)
    public List<FindAlbumResDto> findAllAlbumsByLoginId(String uid) {
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        List<Album> foundAlbums = albumRepository.findAlbumsByUser(foundUser);
        return foundAlbums.stream().map(album -> FindAlbumResDto.builder()
                .id(album.getId())
                .name(album.getName())
                .photoUrl(
                        album.getSnap().stream().map(snap -> urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate())).collect(Collectors.toList())
                ).build()
        ).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FindAlbumResDto findAlbum(Long album_id) {
        Album foundAlbum = albumRepository.findById(album_id).orElseThrow(() -> new CustomException(ExceptionCode.ALBUM_NOT_EXIST));
        return FindAlbumResDto.builder()
                .id(foundAlbum.getId())
                .name(foundAlbum.getName())
                .photoUrl(foundAlbum.getSnap().stream().map(snap -> urlComponent.makePhotoURL(snap.getFileName(), snap.isPrivate())).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindAlbumInfoResDto> findAlbumListByLoginId(String uid) {
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        return albumRepository.findAlbumsByUser(foundUser).stream().map(album -> FindAlbumInfoResDto.builder().id(album.getId()).name(album.getName()).build()).toList();
    }

    @Override
    @Transactional
    public void createAlbum(CreateAlbumReqDto createAlbumReqDto, String uid) {
        User foundUser = userRepository.findByLoginId(uid).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
        albumRepository.save(Album.builder().name(createAlbumReqDto.name()).user(foundUser).build());
    }

    @Override
    @Transactional
    public void modifyAlbumName(Long album_id, String album_name) {

    }

    @Override
    @Transactional
    public void removeAlbum(Long album_id) {

    }
}
