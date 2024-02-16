package me.snaptime.snap.service;

import me.snaptime.snap.data.dto.req.CreateSnapReqDto;

public interface SnapService {

    void createSnap(CreateSnapReqDto createSnapReqDto, String userUid);
}
