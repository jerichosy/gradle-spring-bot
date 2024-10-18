package com.novamaday.d4j.gradle.springbot.commands;

import com.novamaday.d4j.gradle.springbot.facade.commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PingCommand implements SlashCommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        //We reply to the command with "Pong!" and make sure it is ephemeral (only the command user can see it)
        return event.reply()
            .withEphemeral(false)
            .withContent("Pong!");
    }
}
