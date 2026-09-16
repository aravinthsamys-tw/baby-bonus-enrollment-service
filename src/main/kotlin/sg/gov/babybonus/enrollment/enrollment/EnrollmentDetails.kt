package sg.gov.babybonus.enrollment.enrollment

import sg.gov.babybonus.enrollment.disbursement.DisbursementStatus
import sg.gov.babybonus.enrollment.disbursement.DisbursementType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class EnrollmentDetails(
    val id: UUID,
    val childNric: String,
    val status: EnrollmentStatus,
    val enrolledAt: Instant?,
    val disbursement: DisbursementDetails?,
) {
    data class DisbursementDetails(
        val type: DisbursementType,
        val amount: BigDecimal,
        val status: DisbursementStatus,
    )
}
