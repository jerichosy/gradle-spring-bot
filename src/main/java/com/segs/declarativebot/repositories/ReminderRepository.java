package com.segs.declarativebot.repositories;

import com.segs.declarativebot.entities.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByTriggerTimeLessThan(long currentTimeMillis);

    List<Reminder> findByUserIdOrderByTriggerTimeAsc(Long userId);

    Optional<Reminder> findByIdAndUserId(Long id, Long userId);
}
