package sg.gov.babybonus.enrollment.eligibility

import org.springframework.stereotype.Service
import sg.gov.babybonus.enrollment.enrollment.EnrollmentRepository
import sg.gov.babybonus.enrollment.ica.Citizenship
import sg.gov.babybonus.enrollment.ica.IcaChildLookup
import sg.gov.babybonus.enrollment.iroas.IroasParentLookup

@Service
class EnrollmentEligibilityService(
    private val icaChildLookup: IcaChildLookup,
    private val iroasParentLookup: IroasParentLookup,
    private val enrollmentRepository: EnrollmentRepository,
) {

    fun evaluate(request: EligibilityRequest): EligibilityResult {
        val child = icaChildLookup.findChildByNric(request.childNric)
            ?: return EligibilityResult.ineligible(IneligibilityReason.CHILD_NOT_FOUND)

        if (child.citizenship != Citizenship.SINGAPORE_CITIZEN) {
            return EligibilityResult.ineligible(IneligibilityReason.CHILD_NOT_SINGAPORE_CITIZEN)
        }

        val parent = iroasParentLookup.findParentByNric(request.parentNric)
        if (parent == null) {
            return EligibilityResult.ineligible(IneligibilityReason.PARENT_NOT_FOUND)
        }

        if (enrollmentRepository.existsByChildNric(request.childNric)) {
            return EligibilityResult.ineligible(IneligibilityReason.CHILD_ALREADY_ENROLLED)
        }

        return EligibilityResult.eligible()
    }
}
