package me.snaptime.alarm.service;

import me.snaptime.alarm.common.AlarmType;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;

public interface CreateAlarmService {

    /*
        알림을 생성합니다.
        sender : 행위(좋아요,스냅에 태그)를 하여 알림을 보내는 유저
        receiver : 알림을 받는 유저
        snap : 행위가 이루어진 스냅
        alarmType : 알림타입
    */
    void createSnapAlarm(User sender, User receiver, Snap snap, AlarmType alarmType);

    /*
       알림을 생성합니다.
       sender : 행위(팔로우 요청)를 하여 알림을 보내는 유저
       receiver : 알림을 받는 유저
       alarmType : 알림타입
   */
    void createFollowAlarm(User sender, User receiver);

    /*
       알림을 생성합니다.
       sender : 행위(댓글태그, 댓글달기)를 하여 알림을 보내는 유저
       receiver : 알림을 받는 유저
       alarmType : 알림타입
   */
    void createReplyAlarm(User sender, User receiver, Snap snap, String replyMessage);
}
