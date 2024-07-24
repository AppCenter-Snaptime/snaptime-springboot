package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.FollowAlarm;
import me.snaptime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowAlarmRepository extends JpaRepository<FollowAlarm,Long> {

    List<FollowAlarm> findByReceiverAndIsRead(User user, boolean isRead);

    Long countByReceiverAndIsRead(User user, boolean isRead);
}
