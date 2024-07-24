package me.snaptime.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.alarm.dto.res.AlarmInfo;
import me.snaptime.alarm.dto.res.FindAlarmsDto;
import me.snaptime.alarm.repository.FollowAlarmRepository;
import me.snaptime.alarm.repository.ReplyAlarmRepository;
import me.snaptime.alarm.repository.SnapAlarmRepository;
import me.snaptime.alarm.service.AlarmService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.service.FriendService;
import me.snaptime.reply.dto.res.FindParentReplyResDto;
import me.snaptime.reply.service.ReplyService;
import me.snaptime.snap.dto.res.SnapDetailInfoDto;
import me.snaptime.snap.dto.res.SnapInfoDto;
import me.snaptime.snap.service.SnapService;
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
    private final FriendService friendService;
    private final SnapService snapService;
    private final ReplyService replyService;
    private final UrlComponent urlComponent;


    @Override
    @Transactional
    public SnapDetailInfoDto readSnapAlarm(String reqLoginId, Long snapAlarmId) {

        SnapAlarm snapAlarm = snapAlarmRepository.findById(snapAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqLoginId,snapAlarm.getReceiver().getLoginId());

        snapAlarm.readAlarm();
        snapAlarmRepository.save(snapAlarm);

        return snapService.findSnap(snapAlarm.getSnap().getId(), reqLoginId);
    }

    @Override
    @Transactional
    public String readFollowAlarm(String reqLoginId, Long followAlarmId, boolean isAccept) {
        FollowAlarm followAlarm = followAlarmRepository.findById(followAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqLoginId, followAlarm.getReceiver().getLoginId());

        String msg = friendService.acceptFollow(followAlarm.getSender(), followAlarm.getReceiver(), isAccept);
        followAlarm.readAlarm();
        followAlarmRepository.save(followAlarm);

        return msg;
    }

    @Override
    @Transactional
    public FindParentReplyResDto readReplyAlarm(String reqLoginId, Long replyAlarmId) {
        ReplyAlarm replyAlarm = replyAlarmRepository.findById(replyAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(reqLoginId, replyAlarm.getReceiver().getLoginId());

        replyAlarm.readAlarm();
        replyAlarmRepository.save(replyAlarm);

        return replyService.findParentReplyPage(replyAlarm.getSnap().getId(), 1L);
    }

    @Override
    public FindAlarmsDto findAlarms(String reqLoginId) {
        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return FindAlarmsDto.toDto(findSortedAlarms(reqUser,false), findSortedAlarms(reqUser,true));
    }

    @Override
    public Long findNotReadAlarmCnt(String reqLoginId) {
        User reqUser = userRepository.findByLoginId(reqLoginId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        return followAlarmRepository.countByReceiverAndIsRead(reqUser,false)+
                snapAlarmRepository.countByReceiverAndIsRead(reqUser, false)+
                replyAlarmRepository.countByReceiverAndIsRead(reqUser, false);
    }

    @Override
    @Transactional
    public void deleteAlarm(String reqLoginId, Long alarmId, AlarmType alarmType) {

        // 팔로우 알림일 경우 거절처리 후 삭제
        if(alarmType == AlarmType.FOLLOW){
            FollowAlarm followAlarm = followAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqLoginId, followAlarm.getReceiver().getLoginId());
            friendService.acceptFollow(followAlarm.getSender(), followAlarm.getReceiver(), false);
        }
        // 댓글알림일 경우 바로삭제
        else if(alarmType == AlarmType.REPLY){
            ReplyAlarm replyAlarm = replyAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqLoginId, replyAlarm.getReceiver().getLoginId());
            replyAlarmRepository.delete(replyAlarm);
        }
        // 스냅(스냅태그, 좋아요)에 대한 알림일 경우 바로 삭제
        else{
            SnapAlarm snapAlarm = snapAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(reqLoginId, snapAlarm.getReceiver().getLoginId());
            snapAlarmRepository.delete(snapAlarm);
        }
    }

    // 자신한테 온 알림인지 여부체크
    private void isMyAlarm(String reqLoginId, String alarmReceiverLoginId){

        if(!reqLoginId.equals(alarmReceiverLoginId))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_ALARM);

    }

    // 알림을 최신순으로 정렬하여 조회합니다.
    private List<AlarmInfo> findSortedAlarms(User reqUser, boolean isRead){

        List<AlarmInfo> alarmInfos = new ArrayList<>();

        List<FollowAlarm> followAlarms = followAlarmRepository.findByReceiverAndIsRead(reqUser,isRead);
        List<ReplyAlarm> replyAlarms = replyAlarmRepository.findByReceiverAndIsRead(reqUser,isRead);
        List<SnapAlarm> snapAlarms = snapAlarmRepository.findByReceiverAndIsRead(reqUser,isRead);

        followAlarms.forEach(followAlarm -> {

            User sender = followAlarm.getSender();
            String profilePhotoURL = urlComponent.makeProfileURL(sender.getProfilePhoto().getProfilePhotoId());
            String timeAgo = TimeAgoCalculator.findTimeAgo(followAlarm.getCreatedDate());

            AlarmInfo alarmInfo = AlarmInfo.toDtoByFollowAlarm(profilePhotoURL, timeAgo, followAlarm);
            alarmInfos.add(alarmInfo);
        });

        replyAlarms.forEach(replyAlarm -> {

            User sender = replyAlarm.getSender();
            String profilePhotoURL = urlComponent.makeProfileURL(sender.getProfilePhoto().getProfilePhotoId());
            String snapPhotoURL = urlComponent.makePhotoURL(replyAlarm.getSnap().getFileName(),false);
            String timeAgo = TimeAgoCalculator.findTimeAgo(replyAlarm.getCreatedDate());

            AlarmInfo alarmInfo = AlarmInfo.toDtoByReplyAlarm(profilePhotoURL, snapPhotoURL, timeAgo, replyAlarm);
            alarmInfos.add(alarmInfo);
        });

        snapAlarms.forEach(snapAlarm -> {

            User sender = snapAlarm.getSender();
            String profilePhotoURL = urlComponent.makeProfileURL(sender.getProfilePhoto().getProfilePhotoId());
            String snapPhotoURL = urlComponent.makePhotoURL(snapAlarm.getSnap().getFileName(),false);
            String timeAgo = TimeAgoCalculator.findTimeAgo(snapAlarm.getCreatedDate());

            AlarmInfo alarmInfo = AlarmInfo.toDtoBySnapAlarm(profilePhotoURL, snapPhotoURL, timeAgo, snapAlarm);
            alarmInfos.add(alarmInfo);
        });

        alarmInfos.sort(Comparator.comparing(AlarmInfo::getCreatedDate).reversed());
        return alarmInfos;
    }

}
