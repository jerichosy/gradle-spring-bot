package com.segs.declarativebot.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Service
public class WebhookProcessorService {

    private final ObjectMapper objectMapper;

    public WebhookProcessorService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<EmbedCreateSpec> processWebhook(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String hookType = jsonNode.has("hookType") ? jsonNode.get("hookType").asText() : "Unknown Event";

            EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder()
                .title("Webhook Event: " + hookType);

            // Dynamically add fields based on the JSON structure
            addFieldsRecursively(embedBuilder, jsonNode, "");

            return Mono.just(embedBuilder.build());
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

    private void addFieldsRecursively(EmbedCreateSpec.Builder embedBuilder, JsonNode jsonNode, String prefix) {
        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                if (value.isObject() || value.isArray()) {
                    addFieldsRecursively(embedBuilder, value, prefix + key + ".");
                } else {
                    String fieldName = prefix.isEmpty() ? key : prefix + key;
                    embedBuilder.addField(fieldName, value.asText(), true);
                }
            }
        } else if (jsonNode.isArray()) {
            for (int i = 0; i < jsonNode.size(); i++) {
                addFieldsRecursively(embedBuilder, jsonNode.get(i), prefix + "[" + i + "].");
            }
        }
    }
}