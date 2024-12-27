CREATE TABLE IF NOT EXISTS events (
    `id` UUID,
    `short_url` String,
    `created_at` DateTime,
    `user_agent` Nullable(String),
    `referred` Nullable(String),
    `country` Nullable(String),
    `ip_address` Nullable(String),
) ENGINE = MergeTree()
PRIMARY KEY (short_url, created_at)
ORDER BY (short_url, created_at);
