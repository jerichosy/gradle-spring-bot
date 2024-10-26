package com.segs.declarativebot.repositories;

import com.segs.declarativebot.entities.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByTriggerTimeLessThan(long currentTimeMillis);
}
