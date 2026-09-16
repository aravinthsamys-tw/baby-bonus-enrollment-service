package sg.gov.babybonus.enrollment.enrollment

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sg.gov.babybonus.enrollment.audit.AuditLogger
import sg.gov.babybonus.enrollment.audit.AuditOperation
import sg.gov.babybonus.enrollment.disbursement.CashGiftDisbursementService
import sg.gov.babybonus.enrollment.eligibility.EligibilityRequest
import sg.gov.babybonus.enrollment.eligibility.EnrollmentEligibilityService
import java.time.Instant

@Service
class EnrollmentSubmissionService(
    private val eligibilityService: EnrollmentEligibilityService,
    private val enrollmentRepository: EnrollmentRepository,
    private val cashGiftDisbursementService: CashGiftDisbursementService,
    private val auditLogger: AuditLogger,
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
            auditLogger.log(AuditOperation.ENROLLMENT_SUBMITTED, request.childNric, reason.name)
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

        cashGiftDisbursementService.initiateCashGift(enrollment)
        auditLogger.log(AuditOperation.ENROLLMENT_SUBMITTED, request.childNric, enrollment.status.name)

        return EnrollmentSubmissionResult.enrolled(enrollment)
    }
}
