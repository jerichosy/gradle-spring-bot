package com.segs.declarativebot;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.rest.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBot {
    public static void main(String[] args) {
        //Start spring application
        new SpringApplicationBuilder(SpringBot.class)
            .build()
            .run(args);
    }

    @Value("${discord.bot.token}")
    private String botToken;

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        return DiscordClientBuilder.create(botToken).build()
            .gateway()
            .setInitialPresence(ignore -> ClientPresence.online(ClientActivity.custom("\uD83D\uDE80 has better internet than Wai")))
            .login()
            .block();
    }

    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }
}
