package me.snaptime.snap.service;

import me.snaptime.snap.data.dto.req.CreateAlbumReqDto;
import me.snaptime.snap.data.dto.res.GetAllAlbumListResDto;
import me.snaptime.snap.data.dto.res.FindAlbumResDto;
import me.snaptime.snap.data.dto.res.FindAllAlbumsResDto;

import java.util.List;

public interface AlbumService {

    List<FindAllAlbumsResDto> findAllAlbumsByLoginId(String uid);
    FindAlbumResDto findAlbum(Long album_id);
    List<GetAllAlbumListResDto> getAlbumListByLoginId(String uid);
    void createAlbum(CreateAlbumReqDto createAlbumReqDto, String uid);
    void modifyAlbumName(Long album_id, String album_name);
    void removeAlbum(Long album_id);
}
