package me.snaptime.album.service;

import me.snaptime.album.dto.req.CreateAlbumReqDto;
import me.snaptime.album.dto.res.FindAlbumResDto;
import me.snaptime.album.dto.res.FindAllAlbumsResDto;
import me.snaptime.album.dto.res.GetAllAlbumListResDto;
import me.snaptime.user.domain.User;

import java.util.List;

public interface AlbumService {
    List<FindAllAlbumsResDto> findAllAlbumsByEmail(String userEmail);
    FindAlbumResDto findAlbum(String userEmail, Long album_id);
    List<GetAllAlbumListResDto> getAlbumListByEmail(String userEmail);
    void createAlbum(CreateAlbumReqDto createAlbumReqDto, String userEmail);
    /*
    * 사용자 계정에 Non-Classification Album 을 생성하고 생성된 Album 의 Id를 반환한다.
    * */
    Long createNonClassificationAlbum(User user);
    /*
    * 클라이언트가 요청한 album_id가 유효한지 확인한다.
    * */
    boolean isAlbumExistById(Long album_id);
    /*
    * 사용자 계정에 Non-Classification Album 이 존재하는지 확인한다.
    * */
    boolean isNonClassificationExist(User user);
    /*
    * 사용자 계정의 Non-Classification Album 의 Id를 찾아서 반환한다.
    * */
    Long findUserNonClassificationId(User user);
    void modifyAlbumName(Long album_id, String album_name);
    void removeAlbum(String uId, Long album_id);

    /*
    * 인자로 uId와 album_id을 받아 album을 생성한 사용자가 현재 요청을 보낸 사용자와 일치하는지 확인하는 메소드입니다.
    * */
    void isUserHavePermission(User user, Long album_id);
}
