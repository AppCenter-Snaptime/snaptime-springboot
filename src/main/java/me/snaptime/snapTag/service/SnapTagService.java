package me.snaptime.snapTag.service;

import me.snaptime.snap.domain.Snap;
import me.snaptime.snapTag.dto.res.FindTagUserResDto;

import java.util.List;

public interface SnapTagService {

    // snap에 태그유저를 등록합니다.
    void addTagUser(List<String> tagUserLoginIds, Snap snap);

    // 스냅 수정 시 태그정보를 갱신합니다.
    void modifyTagUser(List<String> tagUserLoginIds, Snap snap);

    // 스냅에 저장된 모든 태그정보를 삭제합니다.
    void deleteAllTagUser(Snap snap);

    // 스냅에 태그된 유저들의 정보를 가져옵니다.
    List<FindTagUserResDto> findTagUsers(Long snapId);
}
