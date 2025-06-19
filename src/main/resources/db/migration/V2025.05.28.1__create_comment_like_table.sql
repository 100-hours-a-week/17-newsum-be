CREATE TABLE IF NOT EXISTS comment_like
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    comment_id
    BIGINT
    NOT
    NULL,
    user_id
    BIGINT
    NOT
    NULL,

    CONSTRAINT
    uq_comment_like_user_comment
    UNIQUE
(
    user_id,
    comment_id
)
    );
