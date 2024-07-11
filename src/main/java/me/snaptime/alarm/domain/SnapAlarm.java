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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnapAlarm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapAlarmId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="snap_id",nullable = false)
    private Snap snap;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "to_user_id")
    // 알림을 받는 유저
    private User toUser;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "from_user_id")
    // 행위(좋아요 누르기, 댓글달기 등)를 통해서 toUser에게 알림이 가도록 하는 유저
    private User fromUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "alarm_type")
    private AlarmType alarmType;

    @Column(name = "is_read",nullable = false)
    private boolean isRead = false;

    @Builder
    protected SnapAlarm(Snap snap, User toUser, User fromUser, AlarmType alarmType){
        this.toUser=toUser;
        this.fromUser=fromUser;
        this.snap = snap;
        this.alarmType=alarmType;
    }

    public void readAlarm(){
        isRead = true;
    }
}
