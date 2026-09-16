# baby-bonus-enrollment-service

Spec-driven Kotlin/Spring Boot service for Baby Bonus enrollment, eligibility, disbursement, and infrastructure design.

## Running Locally

Configure an API key before starting the service:

```bash
export BABY_BONUS_API_KEY=local-dev-api-key
./gradlew bootRun
```

All `/api/**` requests must include the key:

```bash
X-API-Key: local-dev-api-key
```

For this assessment, the caller receives the API key through environment-specific configuration. In production, credential issuance, rotation, and revocation would be handled by the platform or an identity provider rather than committed to source control.
