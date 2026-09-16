# AGENTS.md

## Working Agreement

This repository is a Lead Software Engineer assessment. Work in small, reviewable units and keep every implementation choice defensible.

Follow the written scope and specs before adding code. Do not expand the assessment beyond the agreed scope unless the scope is explicitly updated.

## Engineering Principles

- Prefer conventional Kotlin, Spring Boot, JVM, and Gradle patterns.
- Keep solutions simple and explicit. Apply KISS, YAGNI, DRY, and SOLID pragmatically.
- Do not add abstractions, extension points, or layers before a current use case needs them.
- Keep domain, application, infrastructure, and HTTP concerns separated where useful, without over-engineering.
- Add or update tests for behavior introduced by each change.
- Prefer behavior-oriented tests over tests for trivial framework wiring, getters, or constructors.
- Do not weaken existing tests to make new code pass.

## Security and Sensitive Data

- Treat NRIC and financial data as sensitive.
- Never log raw NRIC values.
- Do not commit credentials, API keys, passwords, tokens, real PII, or AWS secrets.
- Use safe public error messages that do not leak source-system lookup details.
- Distinguish assessment simplifications from production recommendations.

## Assessment Discipline

- One prompt or task should normally map to one coherent unit of work.
- Keep commits small and reviewable.
- Do not implement deferred use cases unless `SCOPE.md` changes.
- Document important assumptions, trade-offs, and production gaps honestly.
- Run relevant tests or validation before declaring a unit complete.

## AI-Assisted Development

- Do not assume generated code is correct.
- Review AI-assisted changes for correctness, security, transaction behavior, test coverage, and maintainability.
- Pay particular attention to duplicate enrollment, financial disbursement, NRIC handling, authentication, audit logging, Terraform/IAM, and concurrency.
- Record meaningful AI assistance later in `AI_USAGE.md`, including how outputs were reviewed or changed.
