# baby-bonus-enrollment-service

Spec-driven Kotlin/Spring Boot service for Baby Bonus enrollment, eligibility, disbursement, and infrastructure design.

## Running Locally

Configure an API key before starting the service:

```bash
export BABY_BONUS_API_KEY=local-dev-api-key
export BABY_BONUS_CALLER_ID=local-dev-client
./gradlew bootRun
```

All `/api/**` requests must include the key:

```bash
X-API-Key: local-dev-api-key
```

For this assessment, the caller receives the API key through environment-specific configuration. In production, credential issuance, rotation, and revocation would be handled by the platform or an identity provider rather than committed to source control.

Successful API authentication also associates the request with the configured caller identity for audit logging.

## Running With Docker

Build and run the service image:

```bash
docker build -t baby-bonus-enrollment-service .
docker run --rm -p 8080:8080 \
  -e BABY_BONUS_API_KEY=local-dev-api-key \
  -e BABY_BONUS_CALLER_ID=local-dev-client \
  baby-bonus-enrollment-service
```

## Testing

Run the test suite from a clean build:

```bash
./gradlew --no-daemon clean test
```

## CI/CD

GitHub Actions runs tests and Docker image build on pushes and pull requests.

Kotlin static analysis is not currently part of CI because the available Detekt and ktlint versions did not cleanly support Kotlin `2.4.20` during implementation.

For ECR publishing from `main`, configure:

- repository variable `AWS_ACCOUNT_ID`
- repository secret `AWS_ROLE_TO_ASSUME`

The ECR repository is expected to exist as `baby-bonus-enrollment-service` in `ap-southeast-1`; creating it is handled by infrastructure rather than the CI workflow.

## Infrastructure

Terraform for the AWS production sketch is under `infra/`:

```bash
cd infra
terraform init
terraform validate
```

## Assumptions

- A child may have at most one enrollment.
- A successful enrollment creates a pending `CASH_GIFT` disbursement for SGD 3000.
- ICA and IROAS are represented by local mock data files for this assessment.
- H2 is used for local and test persistence; production would use a managed database such as RDS PostgreSQL.
- API key authentication is used for this assessment; production should use managed identity, OAuth2/JWT, mTLS, or an equivalent centrally governed mechanism.
- Raw NRIC values may be stored for lookup and uniqueness, but are not returned unmasked or written to application logs.

## What I Would Do Next

- Replace mock ICA and IROAS lookups with real adapters, including timeouts, retries, authentication, and contract tests.
- Map concurrent duplicate-enrollment database races to the same duplicate-enrollment response.
- Move disbursement processing to an asynchronous, reconcilable payment workflow with idempotency and reversal support.
- Add CDA account lifecycle, refunds, and manual correction workflows after authorization and audit rules are defined.
- Add production-grade identity, field-level NRIC protection, durable audit retention, monitoring, and alerting.
- Revisit Kotlin static analysis once ktlint or Detekt support cleanly matches the selected Kotlin version.
