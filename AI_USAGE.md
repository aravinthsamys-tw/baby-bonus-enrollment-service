# AI Usage

This repository was developed using a spec-driven, AI-assisted workflow. OpenAI Codex was used as a pair-programming aid; final implementation choices, trade-offs, and validation remained the developer's responsibility.

## How AI Was Used

- Codebase navigation and implementation support while working through small units of work.
- Drafting and revising documentation after scoping and implementation choices were made.
- Suggesting relevant tests/checks and helping diagnose failures.
- Investigating tooling compatibility, including ktlint and Detekt.

## Review and Validation

AI-generated output was reviewed before inclusion. Diffs were checked against the assessment scope, existing code, and the repository engineering guidelines in `AGENTS.md`.

Validation included clean Gradle test runs, Docker image builds, Terraform formatting/validation, Git diff review, and GitHub Actions feedback.

Extra attention was given to NRIC handling, duplicate enrollment, disbursement initiation, authentication, audit logging, and Terraform IAM/networking choices.

## Rejected or Changed

- Detekt/ktlint workarounds and Kotlin downgrades were rejected in favour of keeping the selected Kotlin version and documenting the tooling gap.
- Future-facing repository methods and behaviours were removed where they did not support a current use case.
- Concurrent duplicate-enrollment race handling was deferred: the scoped implementation uses an application-level eligibility check plus a database uniqueness constraint, while mapping raced unique-constraint failures to the same friendly duplicate response is documented as production hardening.
- Optional workflows such as CDA lifecycle, refunds, and manual ineligibility override were documented as deferred rather than partially implemented.
- A CI failure exposed shared H2 test database coupling; the fix was reproduced with `clean test` and corrected by isolating Spring test databases.
