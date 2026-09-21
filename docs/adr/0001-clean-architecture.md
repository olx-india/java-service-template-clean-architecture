# ADR 0001: Clean Architecture layering

## Status

Accepted

## Context

Services need a maintainable structure that keeps business rules independent of frameworks (Spring, JPA, Kafka).

## Decision

Use Clean Architecture with packages `controller`, `usecase`, `domain`, and `infrastructure`. Dependencies point inward. ArchUnit enforces boundaries.

## Consequences

New features follow domain port → use case → controller → adapter. Framework types stay out of `domain` ports.
