package me.snaptime.snap.service;

import me.snaptime.snap.data.dto.req.CreateAlbumReqDto;
import me.snaptime.snap.data.dto.res.GetAllAlbumListResDto;
import me.snaptime.snap.data.dto.res.FindAlbumResDto;
import me.snaptime.snap.data.dto.res.FindAllAlbumsResDto;

import java.util.List;

public interface AlbumService {
    List<FindAllAlbumsResDto> findAllAlbumsByLoginId(String uid);
    FindAlbumResDto findAlbum(String uId, Long album_id);
    List<GetAllAlbumListResDto> getAlbumListByLoginId(String uid);
    void createAlbum(CreateAlbumReqDto createAlbumReqDto, String uid);
    /*
    * 사용자 계정에 Non-Classification Album 을 생성하고 생성된 Album 의 Id를 반환한다.
    * */
    Long createNonClassificationAlbum(String uid);
    /*
    * 클라이언트가 요청한 album_id가 유효한지 확인한다.
    * */
    boolean isAlbumExistById(Long album_id);
    /*
    * 사용자 계정에 Non-Classification Album 이 존재하는지 확인한다.
    * */
    boolean isNonClassificationExist(String uid);
    /*
    * 사용자 계정의 Non-Classification Album 의 Id를 찾아서 반환한다.
    * */
    Long findUserNonClassificationId(String uid);
    void modifyAlbumName(Long album_id, String album_name);
    boolean isUserHavePermission(Long album_id, String uid);
    void removeAlbum(String uId, Long album_id);
}
