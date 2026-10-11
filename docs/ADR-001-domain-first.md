# ADR-001: Keep the workflow domain independent from Spring

## Context

A scheduler, API process and worker process need to share DAG validation, state transitions and quality semantics. Coupling these rules to a web framework would make worker reuse and unit testing expensive.

## Decision

The `atlas-domain` module contains records, enums and pure algorithms only. The `atlas-api` module supplies persistence, HTTP and orchestration adapters.

## Consequences

- Domain behavior can be tested without a database.
- A future Kafka worker can reuse the same task and quality contracts.
- Serialization and persistence concerns remain at the boundary.
- Some mapping code is explicit instead of hidden in an ORM.
