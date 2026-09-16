package sg.gov.babybonus.enrollment.disbursement

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import sg.gov.babybonus.enrollment.enrollment.Enrollment
import sg.gov.babybonus.enrollment.enrollment.EnrollmentRepository
import sg.gov.babybonus.enrollment.enrollment.EnrollmentStatus
import java.math.BigDecimal

@DataJpaTest
class DisbursementRepositoryTests(
    @Autowired private val disbursementRepository: DisbursementRepository,
    @Autowired private val enrollmentRepository: EnrollmentRepository,
) {

    @Test
    fun savesDisbursement() {
        val enrollment = enrollmentRepository.saveAndFlush(
            Enrollment(
                childNric = "T2400001A",
                parentNric = "S8001234A",
                status = EnrollmentStatus.ENROLLED,
            ),
        )

        val saved = disbursementRepository.saveAndFlush(
            Disbursement(
                enrollment = enrollment,
                type = DisbursementType.CASH_GIFT,
                amount = BigDecimal("3000.00"),
                status = DisbursementStatus.PENDING,
            ),
        )

        val found = disbursementRepository.findById(saved.id!!).orElseThrow()

        assertNotNull(saved.id)
        assertEquals(enrollment.id, found.enrollment.id)
        assertEquals(DisbursementType.CASH_GIFT, found.type)
        assertEquals(BigDecimal("3000.00"), found.amount)
        assertEquals(DisbursementStatus.PENDING, found.status)
        assertNull(found.processedAt)
    }
}
