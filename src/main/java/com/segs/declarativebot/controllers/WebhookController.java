package com.segs.declarativebot.controllers;

import com.segs.declarativebot.services.DiscordWebhookService;
import com.segs.declarativebot.services.WebhookProcessorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping
    public Mono<Void> handleWebhook(@RequestBody String payload) {
        return processorService.processWebhook(payload)
            .flatMap(discordWebhookService::sendWebhookMessage);
    }
}
