package sg.gov.babybonus.enrollment.api

import sg.gov.babybonus.enrollment.disbursement.DisbursementStatus
import sg.gov.babybonus.enrollment.disbursement.DisbursementType
import sg.gov.babybonus.enrollment.enrollment.EnrollmentDetails
import sg.gov.babybonus.enrollment.enrollment.EnrollmentStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class EnrollmentStatusResponse(
    val id: UUID,
    val childNric: String,
    val status: EnrollmentStatus,
    val enrolledAt: Instant?,
    val disbursement: DisbursementResponse?,
) {
    companion object {
        fun from(details: EnrollmentDetails): EnrollmentStatusResponse =
            EnrollmentStatusResponse(
                id = details.id,
                childNric = maskNric(details.childNric),
                status = details.status,
                enrolledAt = details.enrolledAt,
                disbursement = details.disbursement?.let(DisbursementResponse::from),
            )

        private fun maskNric(nric: String): String =
            if (nric.length <= 5) {
                "*".repeat(nric.length)
            } else {
                "${nric.take(4)}${"*".repeat(nric.length - 5)}${nric.last()}"
            }
    }
}

data class DisbursementResponse(
    val type: DisbursementType,
    val amount: BigDecimal,
    val status: DisbursementStatus,
) {
    companion object {
        fun from(disbursement: EnrollmentDetails.DisbursementDetails): DisbursementResponse =
            DisbursementResponse(
                type = disbursement.type,
                amount = disbursement.amount,
                status = disbursement.status,
            )
    }
}
