# Baby Bonus Enrollment Service — Lead Software Engineer Assessment

## Background

The Baby Bonus Scheme supports parents of newborn Singapore Citizens with two financial benefits:

- A **cash gift** paid directly to the parent's bank account
- A **Child Development Account (CDA)** — a ring-fenced savings account seeded by government top-ups, usable only at approved merchants

Parents apply to enrol their newborn child. The system checks eligibility against government sources of truth, creates an enrollment record, and initiates disbursement. The scheme currently runs on a legacy system — this service is part of a modernisation effort to rebuild it.

---

## Scenario

You are building a **Baby Bonus Enrollment Service** — a Kotlin/Spring Boot backend service that handles enrollment applications — and designing the infrastructure to run it.

You are provided with mock data representing two external sources of truth:

- **ICA (Immigration & Checkpoints Authority)** — citizenship and identity records for children
- **IROAS** — identity records for parents and guardians

You do **not** need to integrate with real external APIs. Load the provided mock data files as in-memory stubs.

---

## Domain Model

```
Child
  nric:         String        // Singapore NRIC (e.g. T2400001A)
  name:         String
  dateOfBirth:  LocalDate
  citizenship:  SINGAPORE_CITIZEN | PERMANENT_RESIDENT | FOREIGNER

Parent / Guardian
  nric:         String
  name:         String
  relationship: FATHER | MOTHER | LEGAL_GUARDIAN

Enrollment
  id:           UUID
  childNric:    String
  parentNric:   String
  status:       PENDING | ENROLLED | INELIGIBLE
  enrolledAt:   Instant?
  createdAt:    Instant

Disbursement
  id:           UUID
  enrollmentId: UUID
  type:         CASH_GIFT | CDA_DEPOSIT
  amount:       BigDecimal
  status:       PENDING | PROCESSED | FAILED
  processedAt:  Instant?
```

---

## Eligibility Rules

A child is eligible for enrolment if **all** of the following are true:

1. The child exists in ICA records
2. The child is a **Singapore Citizen**
3. The parent/guardian exists in IROAS records
4. The child has **not** been previously enrolled

Upon successful enrolment, initiate a **cash gift disbursement of $3,000**.

---

## Mock Data

Mock data is provided in `mock-data/`:

- `mock-data/ica_children.json` — child identity and citizenship records
- `mock-data/iroas_parents.json` — parent/guardian identity records

Your service should load these files at startup and use them to simulate calls to ICA and IROAS.

---

## Requirements

### Core API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/enrollments` | Submit an enrollment application |
| `GET` | `/api/v1/enrollments/{id}` | Retrieve enrollment status and disbursement details |

**POST /api/v1/enrollments** — request body:
```json
{
  "childNric": "T2400001A",
  "parentNric": "S8001234A"
}
```

**GET /api/v1/enrollments/{id}** — example response:
```json
{
  "id": "a1b2c3d4-...",
  "childNric": "T240****A",
  "status": "ENROLLED",
  "enrolledAt": "2024-08-01T10:00:00Z",
  "disbursement": {
    "type": "CASH_GIFT",
    "amount": 3000.00,
    "status": "PROCESSED"
  }
}
```

### Additional Use Cases

The core endpoints above are the baseline. The following use cases are also in scope — use `SCOPE.md` to decide which to build, mock, or defer:

| Use Case | Description |
|----------|-------------|
| List enrollments | `GET /api/v1/enrollments?childNric=...` — retrieve all enrollments for a given child |
| Error correction | `PATCH /api/v1/enrollments/{id}/ineligible` — ops function to override an enrollment to INELIGIBLE with a reason, for cases where source data was incorrect |
| CDA disbursement | Extend disbursement to support `CDA_DEPOSIT` type in addition to `CASH_GIFT`, with a separate CDA account entity |
| Refund | `POST /api/v1/enrollments/{id}/refund` — ops function to reverse an erroneous disbursement |

### Technical Requirements

- **Framework**: Kotlin with Spring Boot
- **Persistence**: In-memory H2 database (no external database setup required)
- **Error handling**: Return appropriate HTTP status codes with meaningful, non-implementation-leaking error messages
- **Tests**: Service layer unit tests (happy path + key error cases) and at least one integration test exercising the full enrollment flow through the HTTP layer
- **README.md**: Setup instructions, how to run, assumptions made, and a "What I would do next" section noting known gaps or next priorities
- **AI_USAGE.md**: Document which AI tools you used, what you used them for, how you reviewed and validated the output, and any cases where you discarded AI-generated code. You will be asked about this in the debrief — uncritical over-reliance is a red flag; thoughtful, verified use is a strength

### Authentication

Secure all API endpoints. The mechanism is your choice (API key, JWT, etc.). Document how a caller obtains and uses credentials in the README.

### Audit Logging

Log key operations as structured log entries. Each entry must capture: timestamp, operation, caller identity, subject (child NRIC — masked), and outcome. Example operations: enrollment submitted, eligibility check result, disbursement initiated.

### Containerisation

Provide a `Dockerfile` that builds and runs the service.

### Scoping

Before writing code, commit a `SCOPE.md` to your repository covering:
- Which additional use cases (if any) you chose to build, and why
- What infrastructure components you are building in Terraform vs. sketching vs. deferring, and why
- What you are explicitly mocking at the application level, and why
- Any assumptions where requirements are ambiguous

There are no wrong answers — scoping well under constraints is a core lead engineering skill.

### Data Sensitivity Document (`docs/data-sensitivity.md`)

A concise 1-pager: how would you protect sensitive data (NRIC, financial records) in a production system? Address data at rest, in transit, and in logs.

### Infrastructure (Terraform)

Provide Terraform in `infra/` covering:
- VPC with public and private subnets
- EKS cluster with a node group
- RDS instance (engine of your choice)
- IAM roles with least-privilege policies for the service workload
- AWS Secrets Manager entry for database credentials
- Ingress/egress separation reflecting a security-conscious network design

Requirements:
- `terraform validate` must pass
- Use `ap-southeast-1` as the target region
- You do **not** need to apply this against a real AWS account

### CI/CD Pipeline (`.github/workflows/ci.yml`)

A GitHub Actions workflow covering: lint → test → build Docker image → push to ECR.

### Architecture Document (`docs/architecture.md`)

Address the following, keeping it concise and opinionated:

1. **Production architecture**: How would you design this service to handle production-scale load and availability requirements?
2. **Compliance and audit controls**: What controls would you put in place given that this service handles NRIC data and financial transactions?
3. **Modernisation approach**: The Baby Bonus scheme currently runs on a legacy system. How would you approach rebuilding it? What phases, principles, and risks would guide your approach?
4. **Key decisions**: Document 2–3 significant architectural decisions and their trade-offs (ADR format is welcome but not required)

---

## Submission

- Submit via a **GitHub repository** (public, or shared with your reviewers)
- Include a `README.md` at the root with clear setup and run instructions
- **Commit incrementally** — your commit history is part of the assessment; a single large commit is a red flag
- Do not include: AWS credentials, real PII, or NRIC data beyond the provided mock set

**Before you submit, confirm you have:**
- [ ] Service runs with the steps in your README
- [ ] `SCOPE.md` committed to the repository
- [ ] `AI_USAGE.md` completed honestly
- [ ] "What I would do next" section in your README
- [ ] Terraform passes `terraform validate`
- [ ] No secrets or credentials committed

**Note on honesty:** You will be asked about your choices in a debrief. We value candidates who understand the trade-offs they made — including where they cut corners — over candidates who submit more code but cannot explain it. If something is mocked, say so. If AI wrote something you are not fully confident in, say so. Clarity and self-awareness are strengths.

---

## What We Value

- **Clarity over cleverness**: Simple, readable code beats impressive abstractions
- **Security by default**: Treat NRIC and financial data as sensitive throughout — not as an afterthought
- **Judgment under constraints**: You will not be able to complete everything perfectly. Show us how you prioritise and what trade-offs you consciously make
- **Communication**: We should be able to run and understand your service without asking you questions
