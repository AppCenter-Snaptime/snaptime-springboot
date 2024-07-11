package me.snaptime.alarm.repository;

import me.snaptime.alarm.domain.SnapAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapAlarmRepository extends JpaRepository<SnapAlarm,Long> {

}
