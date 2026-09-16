package sg.gov.babybonus.enrollment.enrollment

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sg.gov.babybonus.enrollment.disbursement.DisbursementRepository
import java.util.UUID

@Service
class EnrollmentQueryService(
    private val enrollmentRepository: EnrollmentRepository,
    private val disbursementRepository: DisbursementRepository,
) {

    @Transactional(readOnly = true)
    fun getEnrollmentDetails(id: UUID): EnrollmentDetails {
        val enrollment = enrollmentRepository.findById(id)
            .orElseThrow { EnrollmentNotFoundException(id) }
        val disbursement = disbursementRepository.findByEnrollmentId(id).singleOrNull()

        return EnrollmentDetails(
            id = checkNotNull(enrollment.id),
            childNric = enrollment.childNric,
            status = enrollment.status,
            enrolledAt = enrollment.enrolledAt,
            disbursement = disbursement?.let {
                EnrollmentDetails.DisbursementDetails(
                    type = it.type,
                    amount = it.amount,
                    status = it.status,
                )
            },
        )
    }
}
