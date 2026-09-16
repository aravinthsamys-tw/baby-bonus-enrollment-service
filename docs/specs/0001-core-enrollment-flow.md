# Spec 0001: Core Enrollment Flow

## Objective

Allow a caller to submit a Baby Bonus enrollment application for a newborn child, evaluate eligibility using mocked government sources of truth, persist the enrollment, and create the cash gift disbursement required by the brief.

## API Behaviour

### Submit Enrollment

`POST /api/v1/enrollments`

Request body:

```json
{
  "childNric": "T2400001A",
  "parentNric": "S8001234A"
}
```

Expected successful response:

- HTTP `201 Created`
- enrollment ID
- status `ENROLLED`
- non-null `enrolledAt`

Expected ineligible response:

- appropriate 4xx status
- stable error code
- safe public message
- no raw source-system implementation detail

### Retrieve Enrollment

`GET /api/v1/enrollments/{id}`

Expected successful response:

- enrollment ID
- masked child NRIC
- enrollment status
- `enrolledAt`
- associated cash gift disbursement details
- no parent NRIC in the response

Unknown enrollment IDs return `404` with a stable error response.

## Eligibility Rules

A child is eligible only when all rules pass:

1. Child exists in ICA mock data.
2. Child citizenship is `SINGAPORE_CITIZEN`.
3. Parent or guardian exists in IROAS mock data.
4. Child has not previously been enrolled.

## Domain Invariants

- A child may have at most one enrollment.
- Duplicate enrollment is guarded by an application check and a database uniqueness constraint on child NRIC.
- A successful enrollment creates exactly one `CASH_GIFT` disbursement for SGD 3000.
- Ineligible enrollment creates no enrollment and no disbursement.
- Raw NRIC values are not returned from read APIs except where explicitly required by request input.

## Tests Required

- Eligibility service tests for each eligibility rule.
- Enrollment submission service tests for successful and ineligible submissions.
- Persistence test proving child NRIC uniqueness.
- HTTP tests for successful submit, duplicate submit, invalid request, unknown enrollment, and retrieval response masking.

## Notes

Concurrent duplicate requests are protected by the database uniqueness constraint. Mapping database uniqueness violations from true concurrent races to the same public duplicate-enrollment response is a production hardening concern and may be documented rather than implemented unless time permits.
