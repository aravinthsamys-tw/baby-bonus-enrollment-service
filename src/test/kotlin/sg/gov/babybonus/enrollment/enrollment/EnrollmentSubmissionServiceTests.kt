package sg.gov.babybonus.enrollment.enrollment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sg.gov.babybonus.enrollment.disbursement.CashGiftDisbursementService
import sg.gov.babybonus.enrollment.disbursement.DisbursementRepository
import sg.gov.babybonus.enrollment.disbursement.DisbursementStatus
import sg.gov.babybonus.enrollment.disbursement.DisbursementType
import sg.gov.babybonus.enrollment.TestProperties
import sg.gov.babybonus.enrollment.eligibility.IneligibilityReason

@SpringBootTest(properties = [TestProperties.API_KEY, TestProperties.UNIQUE_H2_DATABASE])
@Transactional
class EnrollmentSubmissionServiceTests(
    @Autowired private val service: EnrollmentSubmissionService,
    @Autowired private val enrollmentRepository: EnrollmentRepository,
    @Autowired private val disbursementRepository: DisbursementRepository,
) {

    @Test
    fun enrollsEligibleChild() {
        val result = service.submit(
            EnrollmentSubmissionRequest(
                childNric = "T2400001A",
                parentNric = "S8001234A",
            ),
        )

        assertEquals(EnrollmentSubmissionStatus.ENROLLED, result.status)
        assertNotNull(result.enrollmentId)
        assertNotNull(result.enrolledAt)
        assertNull(result.reason)

        val enrollment = enrollmentRepository.findById(result.enrollmentId!!).orElseThrow()
        assertEquals("T2400001A", enrollment.childNric)
        assertEquals("S8001234A", enrollment.parentNric)
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.status)
        assertNotNull(enrollment.enrolledAt)

        val disbursements = disbursementRepository.findAll()
        assertEquals(1, disbursements.size)
        val disbursement = disbursements.single()
        assertEquals(enrollment.id, disbursement.enrollment.id)
        assertEquals(DisbursementType.CASH_GIFT, disbursement.type)
        assertEquals(CashGiftDisbursementService.CASH_GIFT_AMOUNT, disbursement.amount)
        assertEquals(DisbursementStatus.PENDING, disbursement.status)
        assertNull(disbursement.processedAt)
    }

    @Test
    fun returnsIneligibleResultWithoutCreatingEnrollment() {
        val result = service.submit(
            EnrollmentSubmissionRequest(
                childNric = "T2499999Z",
                parentNric = "S8001234A",
            ),
        )

        assertEquals(EnrollmentSubmissionStatus.INELIGIBLE, result.status)
        assertEquals(IneligibilityReason.CHILD_NOT_FOUND, result.reason)
        assertNull(result.enrollmentId)
        assertNull(result.enrolledAt)
        assertEquals(0, enrollmentRepository.count())
        assertEquals(0, disbursementRepository.count())
    }
}
