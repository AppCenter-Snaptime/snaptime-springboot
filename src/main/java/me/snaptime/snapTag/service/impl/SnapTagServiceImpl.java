package me.snaptime.snapTag.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.service.AlarmAddService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.service.FriendService;
import me.snaptime.snap.domain.Snap;
import me.snaptime.snap.repository.SnapRepository;
import me.snaptime.snapTag.domain.SnapTag;
import me.snaptime.snapTag.dto.res.TagUserFindResDto;
import me.snaptime.snapTag.repository.SnapTagRepository;
import me.snaptime.snapTag.service.SnapTagService;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final AlarmAddService alarmAddService;
    private final UrlComponent urlComponent;
    private final FriendService friendService;

    @Override
    @Transactional
    public void addTagUser(List<String> tagUserEmails, Snap snap){

        if(tagUserEmails == null)
            tagUserEmails = new ArrayList<>();

        List<SnapTag> snapTags = tagUserEmails.stream().map( tagUserEmail -> {

            User tagUser = userRepository.findByEmail(tagUserEmail)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            // 셀프태그인지 체크
            checkSelfTag(snap,tagUserEmail);

            alarmAddService.createSnapAlarm(snap.getUser(), tagUser,snap, AlarmType.SNAPTAG);
            return SnapTag.builder()
                    .snap(snap)
                    .tagUser(tagUser)
                    .build();
        }).collect(Collectors.toList());

        snapTagRepository.saveAll(snapTags);
    }

    @Override
    @Transactional
    public void modifyTagUser(List<String> tagUserEmails, Snap snap){

        if(tagUserEmails == null)
            tagUserEmails = new ArrayList<>();

        List<SnapTag> snapTags = snapTagRepository.findBySnap(snap);

        // 태그유저 삭제
        snapTagRepository.deleteAll( findDeletedTagUsers(snapTags,tagUserEmails) );

        // 태그유저 추가
       snapTagRepository.saveAll( findNewTagUsers(tagUserEmails, snap) );
    }

    @Override
    @Transactional
    public void deleteAllTagUser(Snap snap){
        snapTagRepository.deleteAll(snapTagRepository.findBySnap(snap));
    }

    @Override
    public List<TagUserFindResDto> findTagUsers(Long snapId, String reqEmail){
        Snap snap = snapRepository.findById(snapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SNAP_NOT_EXIST));
        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        List<SnapTag> snapTags = snapTagRepository.findBySnap(snap);

        return snapTags.stream().map( snapTag ->{
            User tagUser = snapTag.getTagUser();

            String tagUserProfileUrl = urlComponent.makeProfileURL(tagUser.getProfilePhoto().getProfilePhotoId());
            boolean isFollow = friendService.checkIsFollow(reqUser, tagUser);
            return TagUserFindResDto.toDto(snapTag, tagUserProfileUrl, isFollow);
        }).collect(Collectors.toList());
    }

    // 삭제된 태그유저 추출
    private List<SnapTag> findDeletedTagUsers(List<SnapTag> snapTags, List<String> tagUserEmails){

        return snapTags.stream()
                .filter(snapTag -> !tagUserEmails.contains(snapTag.getTagUser().getEmail()))
                .collect(Collectors.toList());
    }

    // 새롭게 추가된 태그유저 추출
    private List<SnapTag> findNewTagUsers(List<String> tagUserEmails, Snap snap){

        return tagUserEmails.stream().map( tagUserEmail-> {

            User tagUser = userRepository.findByEmail(tagUserEmail)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

            if(!snapTagRepository.existsBySnapAndTagUser(snap,tagUser)){
                // 셀프태그인지 체크
                checkSelfTag(snap,tagUserEmail);

                alarmAddService.createSnapAlarm(snap.getUser(), tagUser, snap, AlarmType.SNAPTAG);
                return SnapTag.builder()
                        .snap(snap)
                        .tagUser(tagUser)
                        .build();
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void checkSelfTag(Snap snap, String tagUserEmail){

        if(snap.getUser().getEmail().equals(tagUserEmail))
            throw new CustomException(ExceptionCode.CAN_NOT_SELF_TAG);
    }
}
