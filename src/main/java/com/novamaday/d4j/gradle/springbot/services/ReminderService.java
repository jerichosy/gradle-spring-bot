package com.novamaday.d4j.gradle.springbot.services;

import com.novamaday.d4j.gradle.springbot.entities.Reminder;
import com.novamaday.d4j.gradle.springbot.repositories.ReminderRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private final ReminderService reminderService;

    private final GatewayDiscordClient client;

    private final ReminderRepository repository;

    public ReminderService(ReminderService reminderService, GatewayDiscordClient client, ReminderRepository repository) {
        this.reminderService = reminderService;
        this.client = client;
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

    @Scheduled(fixedRate = 5000)
    public void remindUsers() {
        long now = System.currentTimeMillis();
        List<Reminder> dueReminders = reminderService.getDueReminders(now);

        if (dueReminders.isEmpty()) {
            return;
        }

        log.info("Due Reminders: {}", dueReminders);
        for (Reminder reminder : dueReminders) {
            // Send to the same guild channel where reminder was created
            // Retain this to make it clear to others that it works as intended
            client.getChannelById(Snowflake.of(reminder.getChannelId()))
                .flatMap(channel -> channel.getRestChannel()
                    .createMessage("Reminder for <@" + reminder.getUserId() + ">: " + reminder.getMessage()))
                .subscribe();

            // DM the user
            // FIXME: This may be disabled.
            client.getUserById(Snowflake.of(reminder.getUserId()))
                .flatMap(user -> user.getPrivateChannel().flatMap(channel ->
                    channel.createMessage("Reminder: " + reminder.getMessage())))
                .subscribe();

            // Delete the reminder
            reminderService.deleteReminder(reminder);
        }
    }
}
