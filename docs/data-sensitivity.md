# Data Sensitivity

This service handles NRIC values, eligibility decisions, enrollment records, and financial disbursement records. Treat these as sensitive by default, even where the assessment uses mock data and H2.

## Data at Rest

- Use a managed database with encryption at rest enabled, such as RDS with KMS-managed keys.
- Store secrets in AWS Secrets Manager or an equivalent managed secret store, not in source code, container images, Terraform state, or environment files committed to Git.
- Restrict database access to the service workload and operational break-glass roles only.
- Consider field-level protection for NRIC values if lookup requirements allow it:
  - deterministic encryption or keyed hashing for exact-match lookup
  - separate encrypted display value where operational display is required
- Keep financial disbursement records immutable enough for reconciliation: append status transitions instead of overwriting important financial history in production.
- Define retention and deletion policies for enrollment, audit, and disbursement data with legal and operational stakeholders.

## Data in Transit

- Require TLS for all public and internal service traffic.
- Terminate external TLS at a managed ingress or load balancer, and use TLS again for service-to-database and service-to-service communication where supported.
- Prefer short-lived workload identity, mTLS, or signed tokens for service-to-service authentication in production.
- Do not send NRIC or financial details in URLs because URLs are commonly logged by clients, gateways, proxies, and monitoring tools.
- Apply least-privilege network access: private subnets for the service and database, restricted ingress, and controlled egress to approved dependencies only.

## Logs and Audit

- Never log raw NRIC values, API keys, database credentials, or financial account details.
- Mask NRIC consistently in application logs and audit events.
- Include enough audit context to investigate decisions without exposing unnecessary personal data:
  - timestamp
  - operation
  - trusted caller identity
  - masked subject
  - outcome
- Send logs to a central platform with access controls, retention policies, and tamper-resistant storage for audit logs.
- Alert on suspicious patterns such as repeated authentication failures, unusual lookup volume, duplicate enrollment attempts, and failed disbursement processing.

## Assessment Implementation

Implemented in this repository:

- API key authentication for `/api/**`.
- Structured audit logging for eligibility checks, enrollment submission, and disbursement initiation.
- Masked child NRIC in read responses and audit logs.
- No raw NRIC logging in application code.
- H2 and local mock data for assessment execution.

Production gaps intentionally documented rather than fully built:

- Managed identity or JWT-based caller authentication.
- Key rotation and credential revocation.
- Field-level encryption or tokenisation for NRIC.
- Durable audit retention and tamper evidence.
- Payment settlement, reconciliation, retry, and reversal controls.
- Centralised monitoring and security alerting.
