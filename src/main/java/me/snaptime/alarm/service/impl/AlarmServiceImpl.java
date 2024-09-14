package me.snaptime.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.alarm.dto.res.AlarmFindAllResDto;
import me.snaptime.alarm.dto.res.AlarmInfoResDto;
import me.snaptime.alarm.repository.FollowAlarmRepository;
import me.snaptime.alarm.repository.ReplyAlarmRepository;
import me.snaptime.alarm.repository.SnapAlarmRepository;
import me.snaptime.alarm.service.AlarmService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.domain.User;
import me.snaptime.user.repository.UserRepository;
import me.snaptime.util.TimeAgoCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmServiceImpl implements AlarmService {

    private final SnapAlarmRepository snapAlarmRepository;
    private final FollowAlarmRepository followAlarmRepository;
    private final ReplyAlarmRepository replyAlarmRepository;
    private final UserRepository userRepository;
    private final UrlComponent urlComponent;


    @Override
    @Transactional
    public void readSnapAlarm(String reqEmail, Long snapAlarmId) {

        SnapAlarm snapAlarm = snapAlarmRepository.findById(snapAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqEmail,snapAlarm.getReceiver().getEmail());

        snapAlarm.readAlarm();
        snapAlarmRepository.save(snapAlarm);
    }

    @Override
    @Transactional
    public void readFollowAlarm(String reqEmail, Long followAlarmId) {
        FollowAlarm followAlarm = followAlarmRepository.findById(followAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqEmail, followAlarm.getReceiver().getEmail());


        followAlarm.readAlarm();
        followAlarmRepository.save(followAlarm);
    }

    @Override
    @Transactional
    public void readReplyAlarm(String reqEmail, Long replyAlarmId) {
        ReplyAlarm replyAlarm = replyAlarmRepository.findById(replyAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqEmail, replyAlarm.getReceiver().getEmail());

        replyAlarm.readAlarm();
        replyAlarmRepository.save(replyAlarm);
    }

    @Override
    public AlarmFindAllResDto findAlarms(String reqEmail) {
        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return AlarmFindAllResDto.toDto(findSortedAlarms(reqUser,false), findSortedAlarms(reqUser,true));
    }

    @Override
    public Long findNotReadAlarmCnt(String reqEmail) {
        User reqUser = userRepository.findByEmail(reqEmail)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return followAlarmRepository.countByReceiverAndIsRead(reqUser,false)+
                snapAlarmRepository.countByReceiverAndIsRead(reqUser, false)+
                replyAlarmRepository.countByReceiverAndIsRead(reqUser, false);
    }

    @Override
    @Transactional
    public void deleteAlarm(String reqEmail, Long alarmId, AlarmType alarmType) {

        // 팔로우 알림일 경우
        if(alarmType == AlarmType.FOLLOW){
            FollowAlarm followAlarm = followAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqEmail, followAlarm.getReceiver().getEmail());
            followAlarmRepository.delete(followAlarm);
        }
        // 댓글알림일 경우
        else if(alarmType == AlarmType.REPLY){
            ReplyAlarm replyAlarm = replyAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqEmail, replyAlarm.getReceiver().getEmail());
            replyAlarmRepository.delete(replyAlarm);
        }
        // 스냅(스냅태그, 좋아요)에 대한 알림일 경우
        else{
            SnapAlarm snapAlarm = snapAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqEmail, snapAlarm.getReceiver().getEmail());
            snapAlarmRepository.delete(snapAlarm);
        }
    }

    // 자신한테 온 알림인지 여부체크
    private void isMyAlarm(String reqEmail, String alarmReceiverEmail){

        if(!reqEmail.equals(alarmReceiverEmail))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_ALARM);

    }

    // 알림을 최신순으로 정렬하여 조회합니다.
    private List<AlarmInfoResDto> findSortedAlarms(User reqUser, boolean isRead){

        List<AlarmInfoResDto> alarmInfoResDtos = new ArrayList<>();

        List<FollowAlarm> followAlarms = followAlarmRepository.findByReceiverAndIsRead(reqUser,isRead);
        List<ReplyAlarm> replyAlarms = replyAlarmRepository.findByReceiverAndIsRead(reqUser,isRead);
        List<SnapAlarm> snapAlarms = snapAlarmRepository.findByReceiverAndIsRead(reqUser,isRead);

        followAlarms.forEach(followAlarm -> {

            User sender = followAlarm.getSender();
            String profilePhotoURL = urlComponent.makeProfileURL(sender.getProfilePhoto().getProfilePhotoId());
            String timeAgo = TimeAgoCalculator.findTimeAgo(followAlarm.getCreatedDate());

            AlarmInfoResDto alarmInfoResDto = AlarmInfoResDto.toDtoByFollowAlarm(profilePhotoURL, timeAgo, followAlarm);
            alarmInfoResDtos.add(alarmInfoResDto);
        });

        replyAlarms.forEach(replyAlarm -> {

            User sender = replyAlarm.getSender();
            String profilePhotoURL = urlComponent.makeProfileURL(sender.getProfilePhoto().getProfilePhotoId());
            String snapPhotoURL = urlComponent.makePhotoURL(replyAlarm.getSnap().getFileName(),replyAlarm.getSnap().isPrivate());
            String timeAgo = TimeAgoCalculator.findTimeAgo(replyAlarm.getCreatedDate());

            AlarmInfoResDto alarmInfoResDto = AlarmInfoResDto.toDtoByReplyAlarm(profilePhotoURL, snapPhotoURL, timeAgo, replyAlarm);
            alarmInfoResDtos.add(alarmInfoResDto);
        });

        snapAlarms.forEach(snapAlarm -> {

            User sender = snapAlarm.getSender();
            String profilePhotoURL = urlComponent.makeProfileURL(sender.getProfilePhoto().getProfilePhotoId());
            String snapPhotoURL = urlComponent.makePhotoURL(snapAlarm.getSnap().getFileName(),snapAlarm.getSnap().isPrivate());
            String timeAgo = TimeAgoCalculator.findTimeAgo(snapAlarm.getCreatedDate());

            AlarmInfoResDto alarmInfoResDto = AlarmInfoResDto.toDtoBySnapAlarm(profilePhotoURL, snapPhotoURL, timeAgo, snapAlarm);
            alarmInfoResDtos.add(alarmInfoResDto);
        });

        alarmInfoResDtos.sort(Comparator.comparing(AlarmInfoResDto::getCreatedDate).reversed());
        return alarmInfoResDtos;
    }

}
