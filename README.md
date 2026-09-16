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

## CI/CD

GitHub Actions runs tests and Docker image build on pushes and pull requests.

Kotlin static analysis is intentionally deferred while Detekt and ktlint support catches up with Kotlin `2.4.20`. The assessment keeps the latest stable Kotlin patch version instead of downgrading the language toolchain only to satisfy a lint plugin.

For ECR publishing from `main`, configure:

- repository variable `AWS_ACCOUNT_ID`
- repository secret `AWS_ROLE_TO_ASSUME`

The ECR repository is expected to exist as `baby-bonus-enrollment-service` in `ap-southeast-1`; creating it is handled by infrastructure rather than the CI workflow.
