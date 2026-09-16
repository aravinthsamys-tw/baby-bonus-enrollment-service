package sg.gov.babybonus.enrollment.eligibility

import org.springframework.stereotype.Service
import sg.gov.babybonus.enrollment.audit.AuditLogger
import sg.gov.babybonus.enrollment.audit.AuditOperation
import sg.gov.babybonus.enrollment.enrollment.EnrollmentRepository
import sg.gov.babybonus.enrollment.ica.Citizenship
import sg.gov.babybonus.enrollment.ica.IcaChildLookup
import sg.gov.babybonus.enrollment.iroas.IroasParentLookup

@Service
class EnrollmentEligibilityService(
    private val icaChildLookup: IcaChildLookup,
    private val iroasParentLookup: IroasParentLookup,
    private val enrollmentRepository: EnrollmentRepository,
    private val auditLogger: AuditLogger,
) {

    fun evaluate(request: EligibilityRequest): EligibilityResult {
        val child = icaChildLookup.findChildByNric(request.childNric)
            ?: return auditedResult(
                request,
                EligibilityResult.ineligible(IneligibilityReason.CHILD_NOT_FOUND),
            )

        if (child.citizenship != Citizenship.SINGAPORE_CITIZEN) {
            return auditedResult(
                request,
                EligibilityResult.ineligible(IneligibilityReason.CHILD_NOT_SINGAPORE_CITIZEN),
            )
        }

        val parent = iroasParentLookup.findParentByNric(request.parentNric)
        if (parent == null) {
            return auditedResult(
                request,
                EligibilityResult.ineligible(IneligibilityReason.PARENT_NOT_FOUND),
            )
        }

        if (enrollmentRepository.existsByChildNric(request.childNric)) {
            return auditedResult(
                request,
                EligibilityResult.ineligible(IneligibilityReason.CHILD_ALREADY_ENROLLED),
            )
        }

        return auditedResult(request, EligibilityResult.eligible())
    }

    private fun auditedResult(request: EligibilityRequest, result: EligibilityResult): EligibilityResult {
        auditLogger.log(
            operation = AuditOperation.ELIGIBILITY_CHECK_RESULT,
            childNric = request.childNric,
            outcome = result.reason?.name ?: "ELIGIBLE",
        )
        return result
    }
}
