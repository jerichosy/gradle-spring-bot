package com.novamaday.d4j.gradle.springbot.entities;

import javax.persistence.*;

@Entity
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long channelId;
    private String message;
    private long triggerTime;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

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

//    public long getTriggerTime() {
//        return triggerTime;
//    }

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
}
