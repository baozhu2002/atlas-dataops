# Atlas DataOps

> A resume-ready data workflow control plane with DAG scheduling, retries, data quality, lineage, SQL guardrails and operational metrics.

Atlas DataOps is deliberately designed as a small but real platform rather than a CRUD demo. It models the control-plane problems that appear in Airflow/DolphinScheduler/DataWorks-style systems: pipeline validation, dependency planning, idempotent runs, task state transitions, retry semantics, auditability and lineage.

## Architecture

```text
                     +-------------------------+
                     |   REST Control Plane     |
                     | pipelines / runs / QA    |
                     +------------+------------+
                                  |
       +--------------------------+--------------------------+
       |                          |                          |
+------v------+            +------v------+            +------v------+
| Domain Core |            | Persistence |            | Policy Layer |
| DAG / state |            | PostgreSQL  |            | SQL guard    |
| quality     |            | H2 tests    |            | validation   |
+------+------+            +------+------+            +------+------+ 
       |                          |                          |
       +--------------------------+--------------------------+
                                  |
                     +------------v------------+
                     |   Run Orchestrator      |
                     | plan -> execute -> retry|
                     +------------+------------+
                                  |
                     +------------v------------+
                     | Task Executor Registry   |
                     | SQL / HTTP / PYTHON / QA |
                     +--------------------------+
```

## Current capabilities

- Multi-module Maven architecture: `atlas-domain` and `atlas-api`
- DAG validation with missing-dependency and cycle detection
- Deterministic topological execution plan
- Run state machine: `PENDING -> RUNNING -> SUCCESS/FAILED/CANCELLED`
- Idempotent pipeline runs through `Idempotency-Key`
- Task retry policy with per-task `maxRetries`
- Executor SPI for SQL, HTTP, Python and quality tasks
- Task-level run records with attempts and row metrics
- Data quality evaluator: `NOT_NULL`, `UNIQUE`, `NON_NEGATIVE`, `REGEX`, `ROW_COUNT_MIN`
- Dataset/task lineage graph generation
- SQL read-only guardrails with allow-listed tables
- Audit-ready pipeline/run/task persistence
- Actuator health and metrics endpoint
- PostgreSQL production profile and H2 test profile
- Docker Compose and GitHub Actions

## Quick start

Requires JDK 21 and Maven 3.9+:

```bash
mvn clean verify
mvn -pl atlas-api spring-boot:run
```

Docker:

```bash
docker compose up --build
```

The API starts on `http://localhost:8080`.

## Demo flow

### 1. Register a DAG pipeline

```bash
curl -X POST http://localhost:8080/api/v1/pipelines \
  -H "Content-Type: application/json" \
  -d @docs/demo-pipeline.json
```

The demo contains this graph:

```text
ingest_orders -> normalize_orders -> quality_gate -> publish_metrics
```

### 2. Start an idempotent run

```bash
curl -X POST http://localhost:8080/api/v1/pipelines/1/runs \
  -H "Idempotency-Key: demo-run-001"
```

Submitting the same key again returns the existing run instead of duplicating work.

### 3. Inspect lineage

```bash
curl http://localhost:8080/api/v1/pipelines/1/lineage
```

### 4. Evaluate data quality

```json
POST /api/v1/quality/evaluate
{
  "rows": [{"id": 1, "amount": 20}, {"id": 1, "amount": -3}],
  "rules": [
    {"field":"id","type":"UNIQUE","severity":"ERROR"},
    {"field":"amount","type":"NON_NEGATIVE","severity":"WARN"}
  ]
}
```

## Configuration

```text
DB_URL              JDBC connection string
DB_USERNAME         database user
DB_PASSWORD         database password
DB_DRIVER           JDBC driver, defaults to H2
ATLAS_MAX_WORKERS   reserved worker-pool size for execution expansion
```

## Design decisions

1. **Domain first**: DAG validation and quality evaluation have no Spring dependency, so they are easy to unit test and reuse in a worker process.
2. **Idempotency at the persistence boundary**: the run key is unique in the database, not only in application memory.
3. **Executor SPI**: adding Kafka/Flink/OSS executors should not change orchestration logic.
4. **Guardrails before execution**: SQL policy checks happen before a future connector executes a query.
5. **Structured records**: task attempts, row counts and errors are persisted as operational evidence, not only logs.

## Interview discussion points

- How would you distribute leases across multiple workers?
- How would you recover a worker that dies while holding a task lease?
- How would you make retries safe for non-idempotent sinks?
- How would you add event sourcing or an outbox without changing API contracts?
- How would you partition task logs and metrics at scale?
- How would you evolve the DAG schema while keeping old pipeline versions runnable?

## Roadmap

- [x] DAG control plane and local execution engine
- [x] Quality and lineage primitives
- [x] Idempotency, retries, persistence and guardrails
- [ ] PostgreSQL advisory locks and worker leases
- [ ] Kafka event bus and outbox publisher
- [ ] Real JDBC/OSS/Kafka connectors
- [ ] Flink streaming executor
- [ ] OpenTelemetry traces and Prometheus dashboards
- [ ] RBAC, secrets references and tenant isolation
- [ ] Vue control-plane console

## License

MIT
