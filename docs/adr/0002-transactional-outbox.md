# ADR 0002: Transactional outbox instead of direct Kafka from use cases

## Status

Accepted

## Context

Publishing to Kafka inside a DB transaction risks dual-write inconsistency (DB commit succeeds, Kafka fails, or vice versa).

## Decision

Use cases call `EventPublisher`. Infrastructure writes to `outbox_event` in the same transaction. A relay worker publishes with `FOR UPDATE SKIP LOCKED` (at-least-once). Consumers must be idempotent.

## Consequences

Slight relay lag; topic mapping lives in config; never call Kafka producers from use cases.
