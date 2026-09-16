package sg.gov.babybonus.enrollment.enrollment

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sg.gov.babybonus.enrollment.eligibility.EligibilityRequest
import sg.gov.babybonus.enrollment.eligibility.EnrollmentEligibilityService
import java.time.Instant

@Service
class EnrollmentSubmissionService(
    private val eligibilityService: EnrollmentEligibilityService,
    private val enrollmentRepository: EnrollmentRepository,
) {

    @Transactional
    fun submit(request: EnrollmentSubmissionRequest): EnrollmentSubmissionResult {
        val eligibility = eligibilityService.evaluate(
            EligibilityRequest(
                childNric = request.childNric,
                parentNric = request.parentNric,
            ),
        )

        if (!eligibility.eligible) {
            val reason = checkNotNull(eligibility.reason) {
                "Ineligible eligibility result must include a reason"
            }
            return EnrollmentSubmissionResult.ineligible(reason)
        }

        val enrollment = enrollmentRepository.save(
            Enrollment(
                childNric = request.childNric,
                parentNric = request.parentNric,
                status = EnrollmentStatus.ENROLLED,
                enrolledAt = Instant.now(),
            ),
        )

        return EnrollmentSubmissionResult.enrolled(enrollment)
    }
}
