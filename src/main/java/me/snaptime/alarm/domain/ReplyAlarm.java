package me.snaptime.alarm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.alarm.common.AlarmType;
import me.snaptime.common.BaseTimeEntity;
import me.snaptime.snap.domain.Snap;
import me.snaptime.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "reply_alarm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyAlarm extends BaseTimeEntity {

    @Id
    @Column(name = "reply_alarm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyAlarmId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sender_id",nullable = false)
    // 행위(댓글 등록)을 통해 receiver에게 알림을 보내는 유저
    private User sender;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "receiver_id",nullable = false)
    // 알림을 받는 유저
    private User receiver;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="snap_id",nullable = false)
    private Snap snap;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "alarm_type")
    private AlarmType alarmType;
    
    // 댓글이 변경되더라도 기존 댓글내용을 유지하기 위해 Reply와 연관관계를 맺지않음
    @Column(nullable = false, name = "reply_message")
    private String replyMessage;

    @Column(name = "is_read",nullable = false)
    private boolean isRead = false;

    @Builder
    protected ReplyAlarm(User sender, User receiver, Snap snap, String replyMessage, AlarmType alarmType){
        this.sender=sender;
        this.receiver=receiver;
        this.alarmType=alarmType;
        this.snap=snap;
        this.replyMessage=replyMessage;
    }

    public void readAlarm(){
        isRead = true;
    }
}
