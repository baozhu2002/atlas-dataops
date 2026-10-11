# Architecture Notes

## Control plane versus execution plane

The current repository implements the control-plane core and a deterministic local executor. In production, `RunOrchestrator` becomes the control plane and dispatches leased task envelopes to worker processes through Kafka or a queue.

```text
API -> RunOrchestrator -> task envelope -> broker -> worker
                         |                         |
                         +-> state store <---------+
```

The `TaskExecutor` interface is the seam for this migration. A worker can depend on `atlas-domain` and register connector implementations without importing the web layer.

## Run consistency

- `run_key` is unique, so retrying an HTTP request is safe.
- Each task writes a run record with attempts and row counters.
- The state machine rejects terminal-state rewrites.
- A future worker lease should include `lease_owner`, `lease_until` and a fencing token.

## Quality execution

Quality rules are pure domain objects. They can run against a bounded sample locally or be translated into SQL predicates for large tables. The result keeps the rule, checked row count, failed row count, severity and message so the platform can distinguish a warning from a blocking error.

## Lineage

Lineage is derived from task dependencies plus explicit `input` and `output` dataset references. The current graph is intentionally small and deterministic; a production version would persist dataset versions, column-level mappings and schema snapshots.
