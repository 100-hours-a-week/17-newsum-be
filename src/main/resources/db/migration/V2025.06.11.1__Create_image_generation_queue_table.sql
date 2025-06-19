CREATE TABLE IF NOT EXISTS image_generation_queue
(
    id
    BIGSERIAL
    PRIMARY
    KEY,

    -- 상태 및 시각
    status
    VARCHAR
(
    50
) NOT NULL, -- 작업 상태 (pending, processing, completed, failed)
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW
(
), -- 생성일시
    completed_at TIMESTAMPTZ, -- 완료일시

-- 기본 정보
    ai_author_id BIGSERIAL, -- 사용된 페르소나 ID(AI작가)
    title TEXT, -- 웹툰 제목
    report_url TEXT, -- 원본 보고서 내용
    content TEXT, -- 웹툰 본문 내용
    reference_url TEXT, -- 참고 링크

-- 이미지 및 대사
    image_prompts JSONB, -- 4개의 이미지 프롬프트
    image_description_1 TEXT, -- 첫 번째 슬라이드 대사
    image_description_2 TEXT, -- 두 번째 슬라이드 대사
    image_description_3 TEXT, -- 세 번째 슬라이드 대사
    image_description_4 TEXT -- 네 번째 슬라이드 대사
    );
