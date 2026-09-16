package sg.gov.babybonus.enrollment.enrollment

import sg.gov.babybonus.enrollment.eligibility.IneligibilityReason
import java.time.Instant
import java.util.UUID

data class EnrollmentSubmissionResult(
    val status: EnrollmentSubmissionStatus,
    val enrollmentId: UUID?,
    val enrolledAt: Instant?,
    val reason: IneligibilityReason?,
) {
    companion object {
        fun enrolled(enrollment: Enrollment): EnrollmentSubmissionResult =
            EnrollmentSubmissionResult(
                status = EnrollmentSubmissionStatus.ENROLLED,
                enrollmentId = checkNotNull(enrollment.id),
                enrolledAt = checkNotNull(enrollment.enrolledAt),
                reason = null,
            )

        fun ineligible(reason: IneligibilityReason): EnrollmentSubmissionResult =
            EnrollmentSubmissionResult(
                status = EnrollmentSubmissionStatus.INELIGIBLE,
                enrollmentId = null,
                enrolledAt = null,
                reason = reason,
            )
    }
}
