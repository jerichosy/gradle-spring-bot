package com.segs.declarativebot.services;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.WebhookExecuteSpec;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.WebhookExecuteRequest;
import discord4j.rest.RestClient;
import discord4j.rest.util.Color;
import discord4j.rest.util.MultipartRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscordWebhookService {

    private final RestClient restClient;

    public DiscordWebhookService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Mono<Void> sendWebhookMessage(long webhookId, String webhookToken, EmbedCreateSpec embedSpec) {
        WebhookExecuteSpec spec = WebhookExecuteSpec.builder()
            .addEmbed(embedSpec.withColor(Color.BLUE))
            .build();

        List<EmbedData> embedDataList = spec.embeds().stream()
            .map(EmbedCreateSpec::asRequest)
            .collect(Collectors.toList());

        WebhookExecuteRequest request = WebhookExecuteRequest.builder()
            .embeds(embedDataList)
            .build();

        MultipartRequest<WebhookExecuteRequest> multipartRequest =
            MultipartRequest.ofRequest(request);

        return restClient.getWebhookService()
            .executeWebhook(webhookId, webhookToken, true, multipartRequest)
            .then();
    }
}
