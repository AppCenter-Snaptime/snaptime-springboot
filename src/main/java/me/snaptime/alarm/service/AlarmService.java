package me.snaptime.alarm.service;

import me.snaptime.alarm.common.AlarmType;


public interface AlarmService {

    /*
        snapAlarm을 읽음처리합니다.
    */
    void readSnapAlarm(String reqEmail, Long snapAlarmId);

    //팔로우알림을 읽음처리합니다.
    void readFollowAlarm(String reqEmail, Long followAlarmId);

    /*
        ReplyAlarm을 읽음처리합니다.
    */
    void readReplyAlarm(String reqEmail, Long replyAlarmId);

    //유저의 모든 알림을 불러옵니다.
    Object findAlarms(String reqEmail);

    // 읽지않은 알림이 몇개인지 조회합니다.
    Long findNotReadAlarmCnt(String reqEmail);

    /*
        알림을 삭제합니다.
        읽지않은 팔로우요청에 대한 알림의 경우 자동으로 요청거절이 됩니다.
    */
    void deleteAlarm(String reqEmail, Long alarmId, AlarmType alarmType);
}
