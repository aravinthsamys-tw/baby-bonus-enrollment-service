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
