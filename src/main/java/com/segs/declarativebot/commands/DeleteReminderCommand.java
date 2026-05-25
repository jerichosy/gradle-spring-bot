package com.segs.declarativebot.commands;

import com.segs.declarativebot.facade.ReminderService;
import com.segs.declarativebot.facade.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class DeleteReminderCommand implements SlashCommand {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private ReminderService reminderService;

    @Override
    public String getName() {
        return "delreminder";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        LOGGER.info("DeleteReminderCommand invoked");

        final String content = reminderService.deleteReminder(event);

        return event.reply()
            .withEphemeral(false)
            .withContent(content);
    }
}
