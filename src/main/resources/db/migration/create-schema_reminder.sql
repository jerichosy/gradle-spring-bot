CREATE TABLE reminder
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id      BIGINT,
    channel_id   BIGINT,
    message      VARCHAR(255),
    trigger_time BIGINT                                  NOT NULL,
    CONSTRAINT pk_reminder PRIMARY KEY (id)
);
