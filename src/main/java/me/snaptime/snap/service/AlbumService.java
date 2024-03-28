package me.snaptime.snap.service;

import me.snaptime.snap.data.dto.req.CreateAlbumReqDto;
import me.snaptime.snap.data.dto.res.FindAlbumInfoResDto;
import me.snaptime.snap.data.dto.res.FindAlbumResDto;

import java.util.List;

public interface AlbumService {

    List<FindAlbumResDto> findAllAlbumsByLoginId(String uid);
    FindAlbumResDto findAlbum(Long album_id);
    List<FindAlbumInfoResDto> findAlbumListByLoginId(String uid);
    void createAlbum(CreateAlbumReqDto createAlbumReqDto, String uid);
    void modifyAlbumName(Long album_id, String album_name);
    void removeAlbum(Long album_id);
}
