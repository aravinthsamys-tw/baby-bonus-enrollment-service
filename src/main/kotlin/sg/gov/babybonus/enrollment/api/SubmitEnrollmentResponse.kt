package sg.gov.babybonus.enrollment.api

import sg.gov.babybonus.enrollment.enrollment.EnrollmentSubmissionResult
import sg.gov.babybonus.enrollment.enrollment.EnrollmentSubmissionStatus
import java.time.Instant
import java.util.UUID

data class SubmitEnrollmentResponse(
    val id: UUID,
    val status: EnrollmentSubmissionStatus,
    val enrolledAt: Instant,
) {
    companion object {
        fun from(result: EnrollmentSubmissionResult): SubmitEnrollmentResponse =
            SubmitEnrollmentResponse(
                id = checkNotNull(result.enrollmentId),
                status = result.status,
                enrolledAt = checkNotNull(result.enrolledAt),
            )
    }
}
