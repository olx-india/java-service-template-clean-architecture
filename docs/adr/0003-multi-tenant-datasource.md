# ADR 0003: Database-per-tenant with read/write routing

## Status

Accepted

## Context

Multi-brand deployments need tenant isolation and optional read replicas.

## Decision

Resolve tenant from `X-Default-Tenant` (or host map). Route master vs replica via `@ReadWriteTransaction` / `@ReadOnlyTransaction` and `CustomRoutingDataSource`.

## Consequences

Controllers must annotate read vs write. Tenant is infrastructure context, not a domain entity field by default.
