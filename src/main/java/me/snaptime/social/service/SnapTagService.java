package me.snaptime.social.service;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.customs.CustomException;
import me.snaptime.common.exception.customs.ExceptionCode;
import me.snaptime.snap.data.domain.Snap;
import me.snaptime.snap.data.repository.SnapRepository;
import me.snaptime.social.data.domain.SnapTag;
import me.snaptime.social.data.dto.res.FindTagUserResDto;
import me.snaptime.social.data.repository.snapTag.SnapTagRepository;
import me.snaptime.user.data.domain.User;
import me.snaptime.user.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnapTagService {

    private final SnapTagRepository snapTagRepository;
    private final UserRepository userRepository;
    private final SnapRepository snapRepository;

    @Transactional
    // snap에 태그유저를 등록합니다.
    public void addTagUser(List<String> tagUserLoginIdList, Long snapId){

        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        snapTagRepository.saveAll(
                tagUserLoginIdList.stream().map( loginId -> {
                    User tagedUser = userRepository.findByLoginId(loginId)
                            .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));
                    return SnapTag.builder()
                            .snap(snap)
                            .tagUser(tagedUser)
                            .build();
                }).collect(Collectors.toList())
        );
    }

    @Transactional
    // snap에 태그된 유저를 삭제합니다.
    public void deleteTagUser(List<String> tagUserLoginIdList, Long snapId){

        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        snapTagRepository.deleteAll(
                tagUserLoginIdList.stream().map( loginId -> {
                    User user = userRepository.findByLoginId(loginId)
                            .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

                    return snapTagRepository.findBySnapAndTagUser(snap,user)
                            .orElseThrow(() -> new CustomException(ExceptionCode.SNAPTAG_NOT_EXIST));
                }).collect(Collectors.toList())
        );
    }

    // 스냅에 태그된 유저들의 정보를 가져옵니다.
    public List<FindTagUserResDto> findTagUserList(Long snapId){
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        List<SnapTag> snapTagList = snapTagRepository.findBySnap(snap);
        return snapTagList.stream().map( snapTag -> {

            return FindTagUserResDto.toDto(snapTag);
        }).collect(Collectors.toList());
    }
}
