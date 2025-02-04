package com.segs.declarativebot.commands;

import com.segs.declarativebot.facade.Command;
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
            .image("https://cdn.discordapp.com/attachments/1178014261243551929/1296147657580085279/AP1GczOMTM_u6LdYfpw3oTOrTV63w4C7Z9uiEa5TxYbnaTSpNsB_mL2Qb4A6HAw1270-h1270-s-no-gm.png")
            .build();

        MessageCreateSpec message = MessageCreateSpec.builder()
            .content("WTF IS ILLEGAL INJECTION???")
            .addEmbed(embed)
            .build();

        return event.getMessage().getChannel().flatMap(messageChannel -> messageChannel.createMessage(message)).then();
    }
}
