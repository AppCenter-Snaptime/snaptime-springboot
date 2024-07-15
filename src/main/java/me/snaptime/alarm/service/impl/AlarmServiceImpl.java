package me.snaptime.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.repository.FollowAlarmRepository;
import me.snaptime.alarm.repository.ReplyAlarmRepository;
import me.snaptime.alarm.repository.SnapAlarmRepository;
import me.snaptime.alarm.service.AlarmService;
import me.snaptime.component.url.UrlComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.friend.service.FriendService;
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
    private final UrlComponent urlComponent;


    @Override
    @Transactional
    public void readSnapAlarm(Long snapAlarmId) {

    }

    @Override
    @Transactional
    public String readFollowAlarm(String loginId, Long followAlarmId, boolean isAccept) {
        FollowAlarm followAlarm = followAlarmRepository.findById(followAlarmId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_EXIST));

        // 자신한테 온 알림인지 여부체크
        if(!loginId.equals(followAlarm.getReceiver().getLoginId()))
            throw new CustomException(ExceptionCode.ACCESS_FAIL_ALARM);

        String msg = friendService.acceptFollow(followAlarm.getSender(), followAlarm.getReceiver(), isAccept);
        followAlarm.readAlarm();
        followAlarmRepository.save(followAlarm);

        return msg;
    }

    @Override
    @Transactional
    public void readReplyAlarm(Long replyAlarmId) {

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
    public void deleteAlarm(Long alarmId, AlarmType alarmType) {

    }
}
