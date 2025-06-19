ALTER TABLE image_generation_queue
    DROP COLUMN reference_url;

ALTER TABLE image_generation_queue
    ADD COLUMN work_id VARCHAR;

ALTER TABLE image_generation_queue
    ADD COLUMN keyword JSONB;

ALTER TABLE image_generation_queue
    ADD COLUMN category VARCHAR;
