package me.snaptime.social.data.repository.alarm;

import me.snaptime.social.data.domain.FriendAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendAlarmRepository extends JpaRepository<FriendAlarm,Long> {
}
