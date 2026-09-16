package sg.gov.babybonus.enrollment.disbursement

import org.springframework.stereotype.Service
import sg.gov.babybonus.enrollment.enrollment.Enrollment
import java.math.BigDecimal

@Service
class CashGiftDisbursementService(
    private val disbursementRepository: DisbursementRepository,
) {

    fun initiateCashGift(enrollment: Enrollment): Disbursement =
        disbursementRepository.save(
            Disbursement(
                enrollment = enrollment,
                type = DisbursementType.CASH_GIFT,
                amount = CASH_GIFT_AMOUNT,
                status = DisbursementStatus.PENDING,
            ),
        )

    companion object {
        val CASH_GIFT_AMOUNT: BigDecimal = BigDecimal("3000.00")
    }
}
