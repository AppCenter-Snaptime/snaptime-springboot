package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.ReplyAlarm;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyAlarmRepository extends JpaRepository<ReplyAlarm,Long> {

    List<ReplyAlarm> findByReceiverAndIsRead(User user, boolean isRead);

    Long countByReceiverAndIsRead(User user, boolean isRead);
}
