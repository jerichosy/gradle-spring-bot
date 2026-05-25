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
public class ListRemindersCommand implements SlashCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListRemindersCommand.class);

    private ReminderService reminderService;

    @Override
    public String getName() {
        return "listeminders";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        LOGGER.info("ListRemindersCommand invoked");

        final String content = reminderService.listReminders(event);

        return event.reply()
            .withEphemeral(false)
            .withContent(content);
    }
}
