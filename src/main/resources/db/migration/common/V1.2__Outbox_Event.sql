CREATE TABLE IF NOT EXISTS outbox_event
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    event_type  VARCHAR(128) NOT NULL,
    payload     TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published   TINYINT(1)   NOT NULL DEFAULT 0
);

CREATE INDEX idx_outbox_event_published_created_at ON outbox_event (published, created_at);
