package me.snaptime.alarm.service;

import me.snaptime.alarm.common.AlarmType;

public interface AlarmService {

    // snapAlarm을 읽음처리합니다.
    void readSnapAlarm(Long snapAlarmId);

    /*
        FollowAlarm을 읽음처리합니다.
        FollowAlarm은 친구요청을 수락 or거절을 한 경우에만 읽힙니다.
    */
    String readFollowAlarm(String loginId, Long followAlarmId, boolean isAccept);

    //ReplyAlarm을 읽음처리합니다.
    void readReplyAlarm(Long replyAlarmId);

    //유저의 모든 알림을 불러옵니다.
    Object getAlarmList(Long loginId);

    // 읽지않은 알림이 몇개인지 조회합니다.
    Long findNotReadAlarmCnt(Long loginId);

    /*
        알림을 삭제합니다.
        읽지않은 팔로우요청에 대한 알림의 경우 자동으로 요청거절이 됩니다.
    */
    void deleteAlarm(Long alarmId, AlarmType alarmType);
}
