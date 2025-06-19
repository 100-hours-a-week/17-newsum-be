CREATE TABLE IF NOT EXISTS keyword
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    content
    VARCHAR
(
    100
) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS keyword_favorite
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL,
    keyword_id
    BIGINT
    NOT
    NULL,
    created_at
    TIMESTAMP
    NOT
    NULL
    DEFAULT
    CURRENT_TIMESTAMP,

    CONSTRAINT
    fk_keyword_favorite_user
    FOREIGN
    KEY
(
    user_id
) REFERENCES users
(
    id
) ON DELETE CASCADE,
    CONSTRAINT fk_keyword_favorite_keyword
    FOREIGN KEY
(
    keyword_id
) REFERENCES keyword
(
    id
)
  ON DELETE CASCADE,
    CONSTRAINT unique_user_keyword UNIQUE
(
    user_id,
    keyword_id
)
    );
