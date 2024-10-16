package com.novamaday.d4j.gradle.springbot.repositories;

import com.novamaday.d4j.gradle.springbot.entities.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByTriggerTimeLessThan(long currentTimeMillis);
//    List<Reminder> findByUserId(Long userId);
}
