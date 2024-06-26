package me.snaptime.snap.service;

import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.req.ModifySnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;

public interface SnapService {

    Long createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate);
    FindSnapResDto findSnap(Long id, String uId);
    void modifySnap(ModifySnapReqDto modifySnapReqDto, String userUid, boolean isPrivate);
    void makeRelationSnapAndAlbum(Long snap_id, Long album_id);
    void changeVisibility(Long snapId, String userUid, boolean isPrivate);
    void deleteSnap(Long id, String Uid);
    byte[] downloadPhotoFromFileSystem(String fileName, String uId, boolean isEncrypted);
}
