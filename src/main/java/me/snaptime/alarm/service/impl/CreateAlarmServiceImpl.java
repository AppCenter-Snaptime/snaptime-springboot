package me.snaptime.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.alarm.repository.FollowAlarmRepository;
import me.snaptime.alarm.repository.ReplyAlarmRepository;
import me.snaptime.alarm.repository.SnapAlarmRepository;
import me.snaptime.alarm.service.CreateAlarmService;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAlarmServiceImpl implements CreateAlarmService {

    private final SnapAlarmRepository snapAlarmRepository;
    private final FollowAlarmRepository followAlarmRepository;
    private final ReplyAlarmRepository replyAlarmRepository;

    @Override
    @Transactional
    public void createSnapAlarm(User sender, User receiver, Snap snap, AlarmType alarmType) {
        SnapAlarm snapAlarm = SnapAlarm.builder()
                .sender(sender)
                .receiver(receiver)
                .snap(snap)
                .alarmType(alarmType)
                .build();

        snapAlarmRepository.save(snapAlarm);
    }

    @Override
    @Transactional
    public void createFollowAlarm(User sender, User receiver) {
        FollowAlarm followAlarm = FollowAlarm.builder()
                .sender(sender)
                .receiver(receiver)
                .alarmType(AlarmType.FOLLOW)
                .build();

        followAlarmRepository.save(followAlarm);
    }

    @Override
    @Transactional
    public void createReplyAlarm(User sender, User receiver, Snap snap, AlarmType alarmType, String message) {
        ReplyAlarm replyAlarm = ReplyAlarm.builder()
                .sender(sender)
                .receiver(receiver)
                .snap(snap)
                .messgae(message)
                .alarmType(alarmType)
                .build();

        replyAlarmRepository.save(replyAlarm);
    }
}
