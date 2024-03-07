package me.snaptime.snap.service;

import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.req.ModifySnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;

public interface SnapService {

    void createSnap(CreateSnapReqDto createSnapReqDto, String userUid, boolean isPrivate);
    FindSnapResDto findSnap(Long id);
    void modifySnap(ModifySnapReqDto modifySnapReqDto, String userUid, boolean isPrivate);
    void changeVisibility(Long snapId, String userUid, boolean isPrivate);
    void deleteSnap(Long id, String Uid);
}
