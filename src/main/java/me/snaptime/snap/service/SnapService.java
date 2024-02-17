package me.snaptime.snap.service;

import me.snaptime.snap.data.dto.req.CreateSnapReqDto;
import me.snaptime.snap.data.dto.res.FindSnapResDto;

public interface SnapService {

    void createSnap(CreateSnapReqDto createSnapReqDto, String userUid);
    FindSnapResDto findSnap(Long id);
}
