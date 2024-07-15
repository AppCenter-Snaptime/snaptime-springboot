package me.snaptime.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;
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
import me.snaptime.snap.dto.res.FindSnapResDto;
import me.snaptime.snap.service.SnapService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmServiceImpl implements AlarmService {

    private final SnapAlarmRepository snapAlarmRepository;
    private final FollowAlarmRepository followAlarmRepository;
    private final ReplyAlarmRepository replyAlarmRepository;
    private final FriendService friendService;
    private final SnapService snapService;
    private final ReplyService replyService;
    private final UrlComponent urlComponent;


    @Override
    @Transactional
    public FindSnapResDto readSnapAlarm(String loginId, Long snapAlarmId) {
        SnapAlarm snapAlarm = snapAlarmRepository.findById(snapAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(loginId,snapAlarm.getReceiver().getLoginId());

        snapAlarm.readAlarm();
        snapAlarmRepository.save(snapAlarm);

        return snapService.findSnap(snapAlarm.getSnap().getId(), loginId);
    }

    @Override
    @Transactional
    public String readFollowAlarm(String loginId, Long followAlarmId, boolean isAccept) {
        FollowAlarm followAlarm = followAlarmRepository.findById(followAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(loginId, followAlarm.getReceiver().getLoginId());

        String msg = friendService.acceptFollow(followAlarm.getSender(), followAlarm.getReceiver(), isAccept);
        followAlarm.readAlarm();
        followAlarmRepository.save(followAlarm);

        return msg;
    }

    @Override
    @Transactional
    public FindParentReplyResDto readReplyAlarm(String loginId, Long replyAlarmId) {
        ReplyAlarm replyAlarm = replyAlarmRepository.findById(replyAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        isMyAlarm(loginId, replyAlarm.getReceiver().getLoginId());

        replyAlarm.readAlarm();
        replyAlarmRepository.save(replyAlarm);

        return replyService.readParentReply(replyAlarm.getSnap().getId(), 1L);
    }

    @Override
    public Object getAlarmList(Long loginId) {
        return null;
    }

    @Override
    public Long findNotReadAlarmCnt(Long loginId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteAlarm(String loginId, Long alarmId, AlarmType alarmType) {

        // 팔로우 알림일 경우 거절처리 후 삭제
        if(alarmType == AlarmType.FOLLOW){
            FollowAlarm followAlarm = followAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(loginId, followAlarm.getReceiver().getLoginId());
            friendService.acceptFollow(followAlarm.getSender(), followAlarm.getReceiver(), false);
        }
        // 댓글알림일 경우 바로삭제
        else if(alarmType == AlarmType.REPLY){
            ReplyAlarm replyAlarm = replyAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(loginId, replyAlarm.getReceiver().getLoginId());
            replyAlarmRepository.delete(replyAlarm);
        }
        // 스냅(스냅태그, 좋아요)에 대한 알림일 경우 바로 삭제
        else{
            SnapAlarm snapAlarm = snapAlarmRepository.findById(alarmId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

            isMyAlarm(loginId, snapAlarm.getReceiver().getLoginId());
            snapAlarmRepository.delete(snapAlarm);
        }
    }

    // 자신한테 온 알림인지 여부체크
    private void isMyAlarm(String loginId, String alarmReceiverLoginId){

        if(!loginId.equals(alarmReceiverLoginId))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_ALARM);

    }
}
