package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.FriendAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendAlarmRepository extends JpaRepository<FriendAlarm,Long> {
}
