CREATE TABLE IF NOT EXISTS instagram_media_cache (
    id BIGSERIAL PRIMARY KEY,
    cache_key VARCHAR(100) NOT NULL UNIQUE,
    payload_json TEXT NOT NULL,
    updated_at TIMESTAMP NOT NULL
);