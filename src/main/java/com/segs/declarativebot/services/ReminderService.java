package com.segs.declarativebot.services;

import com.segs.declarativebot.entities.Reminder;
import com.segs.declarativebot.repositories.ReminderRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ReminderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderService.class);

    private final GatewayDiscordClient client;

    private final ReminderRepository reminderRepository;

    public ReminderService(GatewayDiscordClient client, ReminderRepository reminderRepository) {
        this.client = client;
        this.reminderRepository = reminderRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void remindUsers() {
        long now = System.currentTimeMillis();

        Collection<Reminder> dueReminders = reminderRepository.findByTriggerTimeLessThan(now);

        LOGGER.info("Due Reminders: {}", dueReminders);

        dueReminders.forEach(reminder -> {
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
            reminderRepository.delete(reminder);
        });
    }
}
