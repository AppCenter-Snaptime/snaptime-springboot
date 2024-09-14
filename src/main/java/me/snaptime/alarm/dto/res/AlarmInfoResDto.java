package me.snaptime.alarm.dto.res;

import lombok.Builder;
import lombok.Getter;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.alarm.domain.SnapAlarm;

import java.time.LocalDateTime;

@Builder
public record AlarmInfoResDto(
        Long alarmId,
        String snapPhotoURL,
        String senderName,
        String senderProfilePhotoURL,
        String timeAgo,
        String previewText,
        AlarmType alarmType,
        Long snapId,
        String senderEmail,
        @Getter
        LocalDateTime createdDate

) {
    public static AlarmInfoResDto toDtoByFollowAlarm(String senderProfilePhotoURL, String timeAgo, FollowAlarm followAlarm){

        return AlarmInfoResDto.builder()
                .alarmId(followAlarm.getFollowAlarmId())
                .snapPhotoURL(null)
                .senderName(followAlarm.getSender().getName())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(null)
                .alarmType(followAlarm.getAlarmType())
                .createdDate(followAlarm.getCreatedDate())
                .snapId(null)
                .senderEmail(followAlarm.getSender().getEmail())
                .build();
    }

    public static AlarmInfoResDto toDtoBySnapAlarm(String senderProfilePhotoURL, String snapPhotoURL,
                                                   String timeAgo, SnapAlarm snapAlarm){

        return AlarmInfoResDto.builder()
                .alarmId(snapAlarm.getSnapAlarmId())
                .snapPhotoURL(snapPhotoURL)
                .senderName(snapAlarm.getSender().getName())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(null)
                .alarmType(snapAlarm.getAlarmType())
                .createdDate(snapAlarm.getCreatedDate())
                .snapId(snapAlarm.getSnap().getId())
                .senderEmail(snapAlarm.getSender().getEmail())
                .build();
    }

    public static AlarmInfoResDto toDtoByReplyAlarm(String senderProfilePhotoURL, String snapPhotoURL,
                                                    String timeAgo, ReplyAlarm replyAlarm){

        return AlarmInfoResDto.builder()
                .alarmId(replyAlarm.getReplyAlarmId())
                .snapPhotoURL(snapPhotoURL)
                .senderName(replyAlarm.getSender().getName())
                .senderProfilePhotoURL(senderProfilePhotoURL)
                .timeAgo(timeAgo)
                .previewText(replyAlarm.getReplyMessage())
                .alarmType(replyAlarm.getAlarmType())
                .createdDate(replyAlarm.getCreatedDate())
                .snapId(replyAlarm.getSnap().getId())
                .senderEmail(replyAlarm.getSender().getEmail())
                .build();
    }
}
