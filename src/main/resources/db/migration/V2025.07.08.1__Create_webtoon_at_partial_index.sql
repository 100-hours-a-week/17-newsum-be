-- 생성일 기준 최신순 정렬 + deleted_at IS NULL 조건에 맞춘 Partial Index
CREATE INDEX IF NOT EXISTS idx_webtoon_created_at_partial
    ON webtoon (created_at DESC)
    WHERE deleted_at IS NULL;
