package com.novamaday.d4j.gradle.springbot.services;

import com.novamaday.d4j.gradle.springbot.entities.Reminder;
import com.novamaday.d4j.gradle.springbot.repositories.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {

    private final ReminderRepository repository;

    @Autowired
    public ReminderService(ReminderRepository repository) {
        this.repository = repository;
    }

    public Reminder saveReminder(Long userId, Long channelId, String message, long triggerTime) {
        Reminder reminder = new Reminder();
        reminder.setUserId(userId);
        reminder.setChannelId(channelId);
        reminder.setMessage(message);
        reminder.setTriggerTime(triggerTime);
        return repository.save(reminder);
    }

    public List<Reminder> getDueReminders(long currentTimeMillis) {
        return repository.findByTriggerTimeLessThan(currentTimeMillis);
    }

    public void deleteReminder(Reminder reminder) {
        repository.delete(reminder);
    }
}
