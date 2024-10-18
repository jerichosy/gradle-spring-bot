package com.novamaday.d4j.gradle.springbot.commands;

import com.novamaday.d4j.gradle.springbot.facade.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class IllegalInjection implements Command {
    @Override
    public String getName() {
        return "illegalinjection";
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
            .title("Wai:")
            .image("https://cdn.discordapp.com/attachments/1178014261243551929/1296147657580085279/AP1GczOMTM_u6LdYfpw3oTOrTV63w4C7Z9uiEa5TxYbnaTSpNsB_mL2Qb4A6HAw1270-h1270-s-no-gm.png?ex=67113b12&is=670fe992&hm=a8644082b595d3d492a26f7afdcc81a88bcc7b9e93d94a36ca1b3edd9cb39d18&")
            .build();

        MessageCreateSpec message = MessageCreateSpec.builder()
            .content("WTF IS ILLEGAL INJECTION???")
            .addEmbed(embed)
            .build();

        return event.getMessage().getChannel().flatMap(messageChannel -> messageChannel.createMessage(message)).then();
    }
}
