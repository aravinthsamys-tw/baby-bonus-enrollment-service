package sg.gov.babybonus.enrollment.disbursement

import org.springframework.stereotype.Service
import sg.gov.babybonus.enrollment.audit.AuditLogger
import sg.gov.babybonus.enrollment.audit.AuditOperation
import sg.gov.babybonus.enrollment.enrollment.Enrollment
import java.math.BigDecimal

@Service
class CashGiftDisbursementService(
    private val disbursementRepository: DisbursementRepository,
    private val auditLogger: AuditLogger,
) {

    fun initiateCashGift(enrollment: Enrollment): Disbursement {
        val disbursement = disbursementRepository.save(
            Disbursement(
                enrollment = enrollment,
                type = DisbursementType.CASH_GIFT,
                amount = CASH_GIFT_AMOUNT,
                status = DisbursementStatus.PENDING,
            ),
        )
        auditLogger.log(AuditOperation.DISBURSEMENT_INITIATED, enrollment.childNric, disbursement.status.name)
        return disbursement
    }

    companion object {
        val CASH_GIFT_AMOUNT: BigDecimal = BigDecimal("3000.00")
    }
}
