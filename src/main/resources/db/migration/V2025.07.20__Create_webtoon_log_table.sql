CREATE TABLE webtoon_view_log
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    VARCHAR(100) NOT NULL,
    webtoon_id BIGINT       NOT NULL,
    view_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_webtoon_view_log_user ON webtoon_view_log (user_id);
CREATE INDEX idx_webtoon_view_log_webtoon ON webtoon_view_log (webtoon_id);
CREATE INDEX idx_webtoon_view_log_view_at ON webtoon_view_log (view_at);
