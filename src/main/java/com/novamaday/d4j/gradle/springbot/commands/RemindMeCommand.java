package com.novamaday.d4j.gradle.springbot.commands;

import com.novamaday.d4j.gradle.springbot.services.ReminderService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RemindMeCommand implements SlashCommand {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ReminderService reminderService;

    public RemindMeCommand(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Override
    public String getName() {
        return "remindme";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        LOGGER.info("RemindMeCommand");

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

        reminderService.saveReminder(
            event.getInteraction().getUser().getId().asLong(),
            event.getInteraction().getChannelId().asLong(),
            message,
            triggerTime
        );

        LOGGER.info("Saved reminder for user id {}", event.getInteraction().getUser().getId().asString());

        return event.reply()
            .withEphemeral(false)
            .withContent("Reminder set for " + timeString + ".");
    }

    private long parseTimeStringToMillis(String timeString) {
        try {
            if (timeString.endsWith("s")) {
                return Integer.parseInt(timeString.substring(0, timeString.length() - 1)) * 1000L;
            } else if (timeString.endsWith("m")) {
                return Integer.parseInt(timeString.substring(0, timeString.length() - 1)) * 60_000L;
            } else if (timeString.endsWith("h")) {
                return Integer.parseInt(timeString.substring(0, timeString.length() - 1)) * 3_600_000L;
            } else {
                return Long.parseLong(timeString) * 1000L;
            }
        } catch (NumberFormatException e) {
            return 10_000L;  // Defaults to 10 seconds if parsing fails
        }
    }
}
