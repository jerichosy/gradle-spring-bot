package com.novamaday.d4j.gradle.springbot.schedulers;

import com.novamaday.d4j.gradle.springbot.entities.Reminder;
import com.novamaday.d4j.gradle.springbot.services.ReminderService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderScheduler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ReminderService reminderService;
    private final GatewayDiscordClient client;

    public ReminderScheduler(ReminderService reminderService, GatewayDiscordClient client) {
        this.reminderService = reminderService;
        this.client = client;
    }

    @Scheduled(fixedRate = 5000)
    public void remindUsers() {
        long now = System.currentTimeMillis();
        List<Reminder> dueReminders = reminderService.getDueReminders(now);

        if (dueReminders.isEmpty()) { return; }

        LOGGER.info("Due Reminders: {}", dueReminders);
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
