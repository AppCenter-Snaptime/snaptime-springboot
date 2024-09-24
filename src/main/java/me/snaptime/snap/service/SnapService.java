package me.snaptime.snap.service;

import me.snaptime.snap.dto.req.CreateSnapReqDto;
import me.snaptime.snap.dto.req.ModifySnapReqDto;
import me.snaptime.snap.dto.res.SnapDetailInfoResDto;

import java.util.List;

public interface SnapService {

    Long createSnap(CreateSnapReqDto createSnapReqDto, String userEmail, boolean isPrivate, List<String> tagUserEmails, Long album_id);
    SnapDetailInfoResDto findSnap(Long id, String userEmail);
    Long modifySnap(Long snapId, ModifySnapReqDto modifySnapReqDto, String userEmail, List<String> tagUserEmails, boolean isPrivate);
    void changeVisibility(Long snapId, String userEmail, boolean isPrivate);
    void deleteSnap(Long id, String userEmail);
    byte[] downloadPhotoFromFileSystem(String fileName, String userEmail, boolean isEncrypted);
    void relocateSnap(Long snapId, Long albumId, String userEmail);
}
