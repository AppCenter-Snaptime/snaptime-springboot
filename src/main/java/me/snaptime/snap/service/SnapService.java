package me.snaptime.snap.service;

import me.snaptime.snap.dto.req.CreateSnapReqDto;
import me.snaptime.snap.dto.req.ModifySnapReqDto;
import me.snaptime.snap.dto.res.SnapDetailInfoResDto;

import java.util.List;

public interface SnapService {

    Long createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate, List<String> tagUserLoginIds, Long album_id);
    SnapDetailInfoResDto findSnap(Long id, String uId);
    Long modifySnap(Long snapId, ModifySnapReqDto modifySnapReqDto, String userUid, List<String> tagUserLoginIds, boolean isPrivate);
    void changeVisibility(Long snapId, String userUid, boolean isPrivate);
    void deleteSnap(Long id, String Uid);
    byte[] downloadPhotoFromFileSystem(String fileName, String uId, boolean isEncrypted);
    void relocateSnap(Long snapId, Long albumId, String uId);
}
