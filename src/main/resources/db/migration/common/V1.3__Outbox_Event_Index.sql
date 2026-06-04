CREATE INDEX idx_outbox_event_published_created_at ON outbox_event (published, created_at);
