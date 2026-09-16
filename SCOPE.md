# Scope

This submission prioritises a small, defensible Baby Bonus enrollment service. The goal is to implement the core path well, keep sensitive data handling explicit, and document production trade-offs instead of partially building every optional use case.

## Build

- `POST /api/v1/enrollments` to submit an enrollment application.
- `GET /api/v1/enrollments/{id}` to retrieve enrollment status and disbursement details.
- ICA child lookup using `mock-data/ica_children.json` loaded as an application-level mock.
- IROAS parent or guardian lookup using `mock-data/iroas_parents.json` loaded as an application-level mock.
- Eligibility checks for:
  - child exists in ICA records
  - child is a Singapore Citizen
  - parent or guardian exists in IROAS records
  - child has not previously been enrolled
- Duplicate enrollment protection using an application-level eligibility check and a persistence-level uniqueness constraint on child NRIC.
- Creation of a `CASH_GIFT` disbursement for SGD 3000 after successful enrollment.
- H2 persistence for local and test execution.
- Centralized API error handling with meaningful messages that do not leak implementation details.
- API authentication using an assessment-appropriate mechanism documented in the README.
- Structured audit logging for enrollment submission, eligibility decisions, and disbursement initiation, with masked child NRIC values.
- Service-layer tests for the happy path and key error cases.
- At least one HTTP integration test exercising the full enrollment flow.
- Dockerfile and GitHub Actions CI covering lint, test, Docker image build, and ECR push steps.
- Terraform under `infra/` for the components requested in the brief.
- Concise documentation for architecture, data sensitivity, AI usage, and known production gaps.

## Defer

- `GET /api/v1/enrollments?childNric=...` list endpoint.
- `PATCH /api/v1/enrollments/{id}/ineligible` operator correction endpoint.
- `CDA_DEPOSIT` processing and CDA account modelling.
- `POST /api/v1/enrollments/{id}/refund` refund endpoint.

These use cases introduce additional authorization, audit, financial reversal, and account-lifecycle rules. They are better documented as next steps than partially implemented in a time-boxed assessment.

## Application-Level Mocks

- ICA and IROAS are mocked with provided JSON files.
- Disbursement is represented as an internal record rather than an integration with a payment or government finance system.
- H2 is used for assessment persistence; production would use a durable managed database such as RDS PostgreSQL.
- Authentication will be implemented locally for the assessment rather than integrating with a real identity provider.

## Assumptions

- A child may have at most one enrollment, regardless of parent or guardian.
- A duplicate enrollment attempt should be rejected rather than silently treated as success.
- Parent or guardian existence in IROAS is sufficient for this assessment; the mock data does not define a child-to-parent relationship.
- A successful enrollment creates one cash gift disbursement in the same transaction as the enrollment record.
- Disbursement processing is simplified locally; production financial processing should be asynchronous, reconcilable, and auditable.
- Raw NRICs may be stored in the assessment database to support lookup and uniqueness, but must not be returned unmasked or written to logs.
- Error responses should be useful to callers without revealing sensitive source-of-truth lookup details.

## Production Simplifications

The assessment implementation intentionally simplifies:

- Real ICA and IROAS integration, including authentication, retries, timeouts, and data freshness.
- Payment execution, settlement, reconciliation, and reversal workflows.
- Strong customer and operator identity management.
- Fine-grained authorization for operations functions.
- Encryption key management and field-level protection for NRIC values.
- Full audit retention, tamper evidence, alerting, and compliance reporting.
- High availability, autoscaling, backup, disaster recovery, and blue/green deployment.
- Legacy migration, coexistence, and phased cutover.
