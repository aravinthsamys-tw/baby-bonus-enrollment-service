package sg.gov.babybonus.enrollment.enrollment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sg.gov.babybonus.enrollment.eligibility.IneligibilityReason

@SpringBootTest
@Transactional
class EnrollmentSubmissionServiceTests(
    @Autowired private val service: EnrollmentSubmissionService,
    @Autowired private val enrollmentRepository: EnrollmentRepository,
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
    }
}
