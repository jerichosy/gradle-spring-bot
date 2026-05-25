package com.segs.declarativebot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.Objects;

@Entity
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private Long userId;

    @Getter
    private Long channelId;

    @Getter
    private String message;

    @Getter
    private long triggerTime;

    public Reminder() { /* For JPA */ }

    public Reminder(Long userId, Long channelId, String message, long triggerTime) {
        this.userId = userId;
        this.channelId = channelId;
        this.message = message;
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
