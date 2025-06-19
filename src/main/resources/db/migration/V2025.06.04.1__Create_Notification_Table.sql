CREATE TABLE IF NOT EXISTS notification
(
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    title             VARCHAR(100) NOT NULL,
    content           VARCHAR(100) NOT NULL,
    reference_id      BIGINT       NOT NULL,
    notification_type varchar(20)  NOT NULL,
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (id)
);
