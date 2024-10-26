package com.segs.declarativebot.commands;

import com.segs.declarativebot.facade.ReminderService;
import com.segs.declarativebot.facade.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RemindMeCommand implements SlashCommand {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReminderService reminderService;

    @Override
    public String getName() {
        return "remindme";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        LOGGER.info("RemindMeCommand invoked");

        final String content = reminderService.addReminder(event);

        return event.reply()
            .withEphemeral(false)
            .withContent(content);
    }

}
