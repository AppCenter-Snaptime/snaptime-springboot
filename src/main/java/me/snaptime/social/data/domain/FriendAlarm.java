package me.snaptime.social.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.social.common.AlarmType;
import me.snaptime.user.data.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapAlarmId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "to_user_id")
    // 알림을 받는 유저
    private User toUser;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "from_user_id")
    // 행위(팔로잉 요청)를 통해서 toUser에게 알림이 가도록 하는 유저
    private User fromUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "alarm_type")
    private AlarmType alarmType;

    @Column(name = "is_read",nullable = false)
    private boolean isRead = false;

    @Builder
    protected FriendAlarm(User toUser, User fromUser, AlarmType alarmType){
        this.toUser=toUser;
        this.fromUser=fromUser;
        this.alarmType=alarmType;
    }

    public void readAlarm(){
        isRead = true;
    }
}
