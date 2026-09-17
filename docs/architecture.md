# Architecture

Proposed production architecture for the Baby Bonus Enrollment Service.

## Production Architecture

### Runtime

Run the service as a stateless Spring Boot workload on EKS in private subnets. Route public traffic through managed ingress with TLS, authentication, request limits, access logs, and WAF-style controls.

### Components

- EKS service with at least two replicas across Availability Zones
- RDS PostgreSQL in private subnets
- Secrets Manager for credentials and service secrets
- Central logging, metrics, and audit storage
- ICA, IROAS, and payment integrations behind adapters with timeouts, retries, and circuit breaking

### Data Store

Use RDS Multi-AZ with backups, encryption, and monitoring.

### Service Boundary

Keep this service focused on enrollment decisions and disbursement initiation; CDA lifecycle, refunds, settlement, and reversal should be separate capabilities.

### Integration Style

Use event-driven integration for downstream side effects such as payment initiation, audit publication, reporting, and notifications. Keep enrollment submission synchronous and use a transactional outbox for reliable asynchronous publication.

## Compliance and Audit Controls

- Use managed identity, OAuth2/JWT, or mTLS rather than static shared secrets.
- Authorize by operation and caller role.
- Encrypt data at rest and in transit.
- Avoid raw NRICs in logs, metrics, URLs, errors, and dashboards.
- Consider deterministic encryption or keyed hashing for NRIC lookup.
- Store audit logs centrally with retention, access controls, tamper evidence, and append-only behaviour where possible.
- Alert on suspicious access, lookup spikes, duplicate attempts, failed disbursements, and manual correction spikes.

## Modernisation Approach

Use a strangler-style modernisation: introduce the new service around clear capabilities, route selected traffic gradually, and retire legacy paths only after behaviour, auditability, and support readiness are proven.

Recommended phases:

1. Establish domain boundaries.
2. Build the core enrollment path behind ICA, IROAS, and financial adapters.
3. Use approved test datasets and controlled parallel runs until integration contracts are ready.
4. Run in shadow mode before taking writes.
5. Move low-risk workflows first, then progressively enable writes with feature flags and rollback plans.
6. Migrate historical data with reconciliation reports, not just row counts.
7. Retire legacy capabilities only after the new service has proven equivalent business behaviour, reliable audit trails, and support-team readiness.

Key risks:

- Mismatched eligibility decisions
- Duplicate or missed disbursements
- Incomplete audit trails
- NRIC exposure through logs, exports, or support tooling
- Optional workflows added before authorization and operational rules are clear

## Key Decisions

### 1. Runtime Platform

Recommendation: EKS + RDS + Secrets Manager.

Trade-off: stronger platform controls and scaling, but more operational complexity than a simpler managed container runtime.

### 2. Service Boundary

Recommendation: keep enrollment separate from payment, CDA, refund, and settlement capabilities.

Trade-off: more integration points, but clearer ownership and safer financial controls.

### 3. Event-Driven Integration

Recommendation: use events for downstream side effects, with transactional outbox for reliable publication.

Trade-off: eventual consistency, but safer financial and audit processing than direct synchronous downstream calls.
