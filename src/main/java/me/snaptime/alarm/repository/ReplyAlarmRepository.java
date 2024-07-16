package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.ReplyAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyAlarmRepository extends JpaRepository<ReplyAlarm,Long> {
}
