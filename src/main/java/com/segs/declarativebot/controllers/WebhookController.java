package com.segs.declarativebot.controllers;

import com.segs.declarativebot.services.DiscordWebhookService;
import com.segs.declarativebot.services.WebhookProcessorService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookProcessorService processorService;
    private final DiscordWebhookService discordWebhookService;

    public WebhookController(WebhookProcessorService processorService, DiscordWebhookService discordWebhookService) {
        this.processorService = processorService;
        this.discordWebhookService = discordWebhookService;
    }


    @PostMapping("/{webhookId}/{webhookToken}")
    public Mono<Void> handleWebhook(
        @PathVariable long webhookId,
        @PathVariable String webhookToken,
        @RequestBody String payload) {
        return processorService.processWebhook(payload)
            .flatMap(embedSpec -> discordWebhookService.sendWebhookMessage(webhookId, webhookToken, embedSpec));
    }
}
