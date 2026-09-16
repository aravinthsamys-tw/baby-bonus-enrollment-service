# Decision 0001: Scope and Trade-offs

## Context

The brief asks for a working Kotlin/Spring Boot service, security-conscious handling of NRIC and financial data, Terraform, CI/CD, architecture documentation, and explicit scoping. It also lists optional use cases that can significantly expand the domain.

## Decision

Implement the core enrollment flow first and defer optional workflows that require deeper operational, financial, or authorization design.

Build:

- submit enrollment
- retrieve enrollment by ID
- mocked ICA and IROAS lookup
- eligibility evaluation
- duplicate enrollment protection
- cash gift disbursement record
- authentication
- audit logging
- focused tests
- Docker, CI, Terraform, and concise production documentation

Defer:

- enrollment listing by child NRIC
- manual ineligibility override
- CDA account and CDA disbursement processing
- refund and reversal processing

## Rationale

The core path contains the highest-value business and security risks: eligibility correctness, duplicate enrollment, NRIC handling, and financial disbursement initiation. The deferred use cases are important, but they introduce additional authorization, audit, reversal, and lifecycle rules that should not be rushed into incomplete implementation.

## Duplicate Enrollment Note

The implemented assessment path should use both an application-level eligibility check and a persistence-level uniqueness constraint. The uniqueness constraint is the true final guard against duplicate records. Under real production concurrency, a raced duplicate insert should be mapped to the same public duplicate-enrollment outcome instead of leaking as a generic failure; this can be documented as a hardening item if not implemented in the time box.

## Consequences

- The repository history can progress in small, reviewable commits.
- Each implementation unit can point back to an explicit spec.
- Production gaps are acknowledged rather than hidden.
- The assessment remains explainable in a debrief.
