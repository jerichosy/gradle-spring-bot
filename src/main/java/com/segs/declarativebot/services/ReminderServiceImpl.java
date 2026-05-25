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

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReminderServiceImpl implements ReminderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderServiceImpl.class);
    private static final long DEFAULT_REMINDER_MILLIS = 10_000L;
    private static final Pattern TIME_PART_PATTERN = Pattern.compile("(\\d+)([smhdw])");
    private static final DateTimeFormatter REMINDER_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Autowired
    private GatewayDiscordClient client;

    @Autowired
    private ReminderRepository reminderRepository;

    @Scheduled(fixedRate = 10000)
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

        Reminder savedReminder = reminderRepository.save(new Reminder(
            event.getInteraction().getUser().getId().asLong(),
            event.getInteraction().getChannelId().asLong(),
            message,
            triggerTime));

        LOGGER.info("Saved reminder for user id {}", event.getInteraction().getUser().getId().asString());

        return "Reminder set for " + timeString + ". ID: " + savedReminder.getId() + ".";
    }

    @Override
    public String listReminders(ChatInputInteractionEvent event) {
        long userId = event.getInteraction().getUser().getId().asLong();
        List<Reminder> reminders = reminderRepository.findByUserIdOrderByTriggerTimeAsc(userId);

        if (reminders.isEmpty()) {
            return "You do not have any reminders.";
        }

        StringBuilder content = new StringBuilder("Your reminders:\n");
        for (Reminder reminder : reminders) {
            String formattedTime = REMINDER_TIME_FORMATTER.format(Instant.ofEpochMilli(reminder.getTriggerTime()));
            content.append("ID ").append(reminder.getId())
                .append(" | ").append(formattedTime)
                .append(" | ").append(reminder.getMessage())
                .append('\n');
        }

        return content.toString().trim();
    }

    @Override
    public String deleteReminder(ChatInputInteractionEvent event) {
        Optional<Long> reminderId = event.getOption("id")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asLong);

        if (reminderId.isEmpty()) {
            return "Reminder ID is required.";
        }

        long userId = event.getInteraction().getUser().getId().asLong();
        Optional<Reminder> reminder = reminderRepository.findByIdAndUserId(reminderId.get(), userId);

        if (reminder.isEmpty()) {
            return "No reminder found with ID " + reminderId.get() + " for your account.";
        }

        reminderRepository.delete(reminder.get());
        return "Deleted reminder " + reminderId.get() + ".";
    }

    // TODO: Swap this out for a library.
    private long parseTimeStringToMillis(String timeString) {
        if (timeString == null) {
            return DEFAULT_REMINDER_MILLIS;
        }

        String normalized = timeString.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return DEFAULT_REMINDER_MILLIS;
        }

        if (normalized.matches("\\d+")) {
            try {
                return Math.multiplyExact(Long.parseLong(normalized), 1000L);
            } catch (NumberFormatException | ArithmeticException e) {
                return DEFAULT_REMINDER_MILLIS;
            }
        }

        String condensed = normalized.replaceAll("\\s+", "");
        Matcher matcher = TIME_PART_PATTERN.matcher(condensed);
        long totalMillis = 0L;
        int lastMatchEnd = 0;
        boolean matched = false;

        while (matcher.find()) {
            if (matcher.start() != lastMatchEnd) {
                return DEFAULT_REMINDER_MILLIS;
            }

            matched = true;
            long value;
            try {
                value = Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                return DEFAULT_REMINDER_MILLIS;
            }

            long multiplier = switch (matcher.group(2)) {
                case "s" -> 1000L;
                case "m" -> 60_000L;
                case "h" -> 3_600_000L;
                case "d" -> 86_400_000L;
                case "w" -> 604_800_000L;
                default -> 0L;
            };

            if (multiplier == 0L) {
                return DEFAULT_REMINDER_MILLIS;
            }

            try {
                totalMillis = Math.addExact(totalMillis, Math.multiplyExact(value, multiplier));
            } catch (ArithmeticException e) {
                return DEFAULT_REMINDER_MILLIS;
            }

            lastMatchEnd = matcher.end();
        }

        if (!matched || lastMatchEnd != condensed.length() || totalMillis <= 0L) {
            return DEFAULT_REMINDER_MILLIS;
        }

        return totalMillis;
    }
}
