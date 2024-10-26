package com.segs.declarativebot.services;

import com.segs.declarativebot.entities.Reminder;
import com.segs.declarativebot.facade.ReminderService;
import com.segs.declarativebot.repositories.ReminderRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ReminderServiceImpl implements ReminderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderServiceImpl.class);

    @Autowired
    private GatewayDiscordClient client;

    @Autowired
    private ReminderRepository reminderRepository;

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

    @Override
    public String addReminder(ChatInputInteractionEvent event) {

        LOGGER.info(event.toString());

        String timeString = event.getOption("time")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .get();

        String message = event.getOption("message")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .orElse("You have a reminder!");

        LOGGER.info("Got request to send reminder {} from now with message: {}", timeString, message);

        long triggerTime = System.currentTimeMillis() + parseTimeStringToMillis(timeString);

        reminderRepository.save(new Reminder(
            event.getInteraction().getUser().getId().asLong(),
            event.getInteraction().getChannelId().asLong(),
            message,
            triggerTime));

        LOGGER.info("Saved reminder for user id {}", event.getInteraction().getUser().getId().asString());

        return "Reminder set for " + timeString + ".";
    }

    // TODO: Swap this out for a library.
    private long parseTimeStringToMillis(String timeString) {
        try {
            long millis = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
            if (timeString.endsWith("s")) {
                return millis * 1000L;
            } else if (timeString.endsWith("m")) {
                return millis * 60_000L;
            } else if (timeString.endsWith("h")) {
                return millis * 3_600_000L;
            } else {
                return Long.parseLong(timeString) * 1000L;
            }
        } catch (NumberFormatException e) {
            return 10_000L;  // Defaults to 10 seconds if parsing fails
        }
    }
}
