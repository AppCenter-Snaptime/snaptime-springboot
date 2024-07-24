package me.snaptime.alarm.dto.res;

import lombok.Builder;
import lombok.Getter;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;

import java.time.LocalDateTime;

@Builder
public record AlarmInfo(
        Long alarmId,
        String snapPhotoURL,
        String senderName,
        String senderProfilePhotoURL,
        String timeAgo,
        String previewText,
        AlarmType alarmType,
        @Getter
        LocalDateTime createdDate

) {
    public static AlarmInfo toDtoByFollowAlarm(String senderProfilePhotoURL, String timeAgo, FollowAlarm followAlarm){

        return AlarmInfo.builder()
                .alarmId(followAlarm.getFollowAlarmId())
                .snapPhotoURL(null)
                .senderName(followAlarm.getSender().getName())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(null)
                .alarmType(followAlarm.getAlarmType())
                .createdDate(followAlarm.getCreatedDate())
                .build();
    }

    public static AlarmInfo toDtoBySnapAlarm(String senderProfilePhotoURL, String snapPhotoURL,
                                             String timeAgo, SnapAlarm snapAlarm){

        return AlarmInfo.builder()
                .alarmId(snapAlarm.getSnapAlarmId())
                .snapPhotoURL(snapPhotoURL)
                .senderName(snapAlarm.getSender().getName())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(null)
                .alarmType(snapAlarm.getAlarmType())
                .createdDate(snapAlarm.getCreatedDate())
                .build();
    }

    public static AlarmInfo toDtoByReplyAlarm(String senderProfilePhotoURL, String snapPhotoURL,
                                              String timeAgo, ReplyAlarm replyAlarm){

        return AlarmInfo.builder()
                .alarmId(replyAlarm.getReplyAlarmId())
                .snapPhotoURL(snapPhotoURL)
                .senderName(replyAlarm.getSender().getName())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(replyAlarm.getReplyMessage())
                .alarmType(replyAlarm.getAlarmType())
                .createdDate(replyAlarm.getCreatedDate())
                .build();
    }
}
