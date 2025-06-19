CREATE TABLE author_favorite
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT                                                NOT NULL,
    ai_author_id BIGINT                                                NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_author_favorite_user FOREIGN KEY (user_id)
        REFERENCES users (id),
    CONSTRAINT fk_author_favorite_ai_author FOREIGN KEY (ai_author_id)
        REFERENCES ai_author (id)
);
