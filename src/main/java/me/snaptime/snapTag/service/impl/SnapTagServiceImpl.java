package me.snaptime.snapTag.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snapTag.domain.SnapTag;
import me.snaptime.snapTag.dto.res.FindTagUserResDto;
import me.snaptime.snapTag.repository.SnapTagRepository;
import me.snaptime.snapTag.service.SnapTagService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnapTagServiceImpl implements SnapTagService {

    private final SnapTagRepository snapTagRepository;
    private final UserRepository userRepository;
    private final SnapRepository snapRepository;

    @Transactional
    public void addTagUser(List<String> tagUserLoginIdList, Snap snap){

        List<SnapTag> snapTagList = tagUserLoginIdList.stream().map( loginId -> {
            User tagedUser = userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            return SnapTag.builder()
                    .snap(snap)
                    .tagUser(tagedUser)
                    .build();
        }).collect(Collectors.toList());

        snapTagRepository.saveAll(snapTagList);
    }

    @Transactional
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

    @Transactional
    public void modifyTagUser(List<String> tagUserLoginIdList, Snap snap){

        List<SnapTag> snapTagList = snapTagRepository.findBySnap(snap);

        // 태그유저 삭제
        snapTagRepository.deleteAll( findDeleteTagUserList(snapTagList,tagUserLoginIdList) );

        // 태그유저 추가
       snapTagRepository.saveAll( findNewTagUserList(tagUserLoginIdList, snap) );
    }

    @Transactional
    public void deleteAllTagUser(Snap snap){
        snapTagRepository.deleteAll(snapTagRepository.findBySnap(snap));
    }

    public List<FindTagUserResDto> findTagUserList(Long snapId){
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));

        List<SnapTag> snapTagList = snapTagRepository.findBySnap(snap);
        return snapTagList.stream().map( snapTag -> FindTagUserResDto.toDto(snapTag)).collect(Collectors.toList());
    }

    // 삭제된 태그유저 추출
    private List<SnapTag> findDeleteTagUserList(List<SnapTag> snapTagList, List<String> tagUserLoginIdList){

        return snapTagList.stream()
                .filter(snapTag -> !tagUserLoginIdList.contains(snapTag.getTagUser().getLoginId()))
                .collect(Collectors.toList());
    }

    // 새롭게 추가된 태그유저 추출
    private List<SnapTag> findNewTagUserList(List<String> tagUserLoginIdList, Snap snap){

        return tagUserLoginIdList.stream().map( loginId-> {

            User user = userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            if(!snapTagRepository.existsBySnapAndTagUser(snap,user)){
                return SnapTag.builder()
                        .snap(snap)
                        .tagUser(user)
                        .build();
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
