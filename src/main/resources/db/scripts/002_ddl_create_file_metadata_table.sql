create table file_metadata(
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    path VARCHAR(500) NOT NULL,
    name VARCHAR(200) NOT NULL,
    size BIGINT NOT NULL,
    type VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_user_path UNIQUE (user_id, path)
);

CREATE INDEX idx_file_metadata_user_name ON file_metadata(user_id, path)