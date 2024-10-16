package com.novamaday.d4j.gradle.springbot.listeners;

import com.novamaday.d4j.gradle.springbot.commands.Command;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Component
public class CommandListener {

    private final Collection<Command> commands;

    public CommandListener(List<Command> commands, GatewayDiscordClient client) {
        this.commands = commands;

        client.on(MessageCreateEvent.class, this::execute).subscribe();
    }


    public Mono<Void> execute(MessageCreateEvent event) {
        // 3.1 Message.getContent() is a String
        return Mono.just(event.getMessage().getContent())
            .flatMap(content ->
                Flux.fromIterable(commands)
                    // We will be using ! as our "prefix" to any command in the system.
                    .filter(command -> content.startsWith("!" + command.getName())) // FIXME: will match even with trailing characters, unsure if that's desired
                    .next() // Get the first matching command
                    .flatMap(command -> command.execute(event))
            ).then(); // return a Mono<Void> to complete the operation
    }
}
