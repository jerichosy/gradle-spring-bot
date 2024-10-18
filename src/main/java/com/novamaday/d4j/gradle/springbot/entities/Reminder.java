package com.novamaday.d4j.gradle.springbot.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long channelId;
    private String message;
    private long triggerTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    @Override
    public String toString() {
        return "Reminder{" +
            "id=" + id +
            ", userId=" + userId +
            ", message='" + message + '\'' +
            ", triggerTime=" + triggerTime +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reminder reminder = (Reminder) o;
        return triggerTime == reminder.triggerTime && Objects.equals(id, reminder.id) && Objects.equals(userId, reminder.userId) && Objects.equals(channelId, reminder.channelId) && Objects.equals(message, reminder.message);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(channelId);
        result = 31 * result + Objects.hashCode(message);
        result = 31 * result + Long.hashCode(triggerTime);
        return result;
    }
}
