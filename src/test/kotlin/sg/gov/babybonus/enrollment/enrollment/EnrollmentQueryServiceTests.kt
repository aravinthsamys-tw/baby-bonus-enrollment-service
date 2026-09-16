package sg.gov.babybonus.enrollment.enrollment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sg.gov.babybonus.enrollment.disbursement.DisbursementType
import java.util.UUID

@SpringBootTest(properties = ["baby-bonus.auth.api-key=test-api-key"])
@Transactional
class EnrollmentQueryServiceTests(
    @Autowired private val enrollmentSubmissionService: EnrollmentSubmissionService,
    @Autowired private val enrollmentQueryService: EnrollmentQueryService,
) {

    @Test
    fun getsEnrollmentDetails() {
        val submission = enrollmentSubmissionService.submit(
            EnrollmentSubmissionRequest(
                childNric = "T2400001A",
                parentNric = "S8001234A",
            ),
        )

        val details = enrollmentQueryService.getEnrollmentDetails(submission.enrollmentId!!)

        assertEquals(submission.enrollmentId, details.id)
        assertEquals("T2400001A", details.childNric)
        assertEquals(EnrollmentStatus.ENROLLED, details.status)
        assertNotNull(details.enrolledAt)
        assertEquals(DisbursementType.CASH_GIFT, details.disbursement?.type)
    }

    @Test
    fun throwsWhenEnrollmentIsUnknown() {
        assertThrows<EnrollmentNotFoundException> {
            enrollmentQueryService.getEnrollmentDetails(UUID.randomUUID())
        }
    }
}
