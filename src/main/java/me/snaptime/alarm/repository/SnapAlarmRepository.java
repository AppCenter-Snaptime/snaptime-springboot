package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.SnapAlarm;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapAlarmRepository extends JpaRepository<SnapAlarm,Long> {

    List<SnapAlarm> findByReceiverAndIsRead(User user, boolean isRead);

    Long countByReceiverAndIsRead(User user, boolean isRead);
}
