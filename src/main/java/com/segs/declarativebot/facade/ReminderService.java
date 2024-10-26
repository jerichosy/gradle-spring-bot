package com.segs.declarativebot.facade;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

public interface ReminderService {

    void remindUsers();

    String addReminder(ChatInputInteractionEvent event);
}
