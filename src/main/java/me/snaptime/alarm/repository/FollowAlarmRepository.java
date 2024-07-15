package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.FollowAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowAlarmRepository extends JpaRepository<FollowAlarm,Long> {
}
