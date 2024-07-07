package me.snaptime.social.data.repository.alarm;

import me.snaptime.social.data.domain.SnapAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapAlarmRepository extends JpaRepository<SnapAlarm,Long> {

}
