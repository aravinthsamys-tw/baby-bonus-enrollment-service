package sg.gov.babybonus.enrollment.eligibility

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import sg.gov.babybonus.enrollment.audit.AuditLogger
import sg.gov.babybonus.enrollment.enrollment.EnrollmentRepository
import sg.gov.babybonus.enrollment.ica.Citizenship
import sg.gov.babybonus.enrollment.ica.IcaChild
import sg.gov.babybonus.enrollment.ica.IcaChildLookup
import sg.gov.babybonus.enrollment.iroas.IroasParent
import sg.gov.babybonus.enrollment.iroas.IroasParentLookup
import sg.gov.babybonus.enrollment.iroas.Relationship
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate

class EnrollmentEligibilityServiceTests {

    @Test
    fun returnsEligibleWhenAllRulesPass() {
        val service = createService()

        val result = service.evaluate(defaultRequest())

        assertTrue(result.eligible)
        assertNull(result.reason)
    }

    @Test
    fun returnsIneligibleWhenChildIsMissing() {
        val service = createService(children = emptyMap())

        val result = service.evaluate(defaultRequest())

        assertEquals(EligibilityResult.ineligible(IneligibilityReason.CHILD_NOT_FOUND), result)
    }

    @Test
    fun returnsIneligibleWhenChildIsNotSingaporeCitizen() {
        val service = createService(
            children = mapOf(
                "T2400002B" to defaultChild(
                    nric = "T2400002B",
                    citizenship = Citizenship.PERMANENT_RESIDENT,
                ),
            ),
        )

        val result = service.evaluate(defaultRequest(childNric = "T2400002B"))

        assertEquals(EligibilityResult.ineligible(IneligibilityReason.CHILD_NOT_SINGAPORE_CITIZEN), result)
    }

    @Test
    fun returnsIneligibleWhenParentIsMissing() {
        val service = createService(parents = emptyMap())

        val result = service.evaluate(defaultRequest())

        assertEquals(EligibilityResult.ineligible(IneligibilityReason.PARENT_NOT_FOUND), result)
    }

    @Test
    fun returnsIneligibleWhenChildAlreadyEnrolled() {
        val service = createService(enrolledChildNrics = setOf("T2400001A"))

        val result = service.evaluate(defaultRequest())

        assertEquals(EligibilityResult.ineligible(IneligibilityReason.CHILD_ALREADY_ENROLLED), result)
    }

    private fun createService(
        children: Map<String, IcaChild> = mapOf("T2400001A" to defaultChild()),
        parents: Map<String, IroasParent> = mapOf("S8001234A" to defaultParent()),
        enrolledChildNrics: Set<String> = emptySet(),
    ): EnrollmentEligibilityService {
        val enrollmentRepository = mock(EnrollmentRepository::class.java)
        enrolledChildNrics.forEach { childNric ->
            `when`(enrollmentRepository.existsByChildNric(childNric)).thenReturn(true)
        }

        return EnrollmentEligibilityService(
            icaChildLookup = MapIcaChildLookup(children),
            iroasParentLookup = MapIroasParentLookup(parents),
            enrollmentRepository = enrollmentRepository,
            auditLogger = AuditLogger(ObjectMapper()),
        )
    }

    private fun defaultRequest(
        childNric: String = "T2400001A",
        parentNric: String = "S8001234A",
    ): EligibilityRequest = EligibilityRequest(
        childNric = childNric,
        parentNric = parentNric,
    )

    private fun defaultChild(
        nric: String = "T2400001A",
        citizenship: Citizenship = Citizenship.SINGAPORE_CITIZEN,
    ): IcaChild = IcaChild(
        nric = nric,
        name = "Tan Wei Xuan",
        dateOfBirth = LocalDate.parse("2024-01-15"),
        citizenship = citizenship,
    )

    private fun defaultParent(): IroasParent = IroasParent(
        nric = "S8001234A",
        name = "Tan Ah Kow",
        relationship = Relationship.FATHER,
    )

    private class MapIcaChildLookup(
        private val children: Map<String, IcaChild>,
    ) : IcaChildLookup {
        override fun findChildByNric(nric: String): IcaChild? = children[nric]
    }

    private class MapIroasParentLookup(
        private val parents: Map<String, IroasParent>,
    ) : IroasParentLookup {
        override fun findParentByNric(nric: String): IroasParent? = parents[nric]
    }
}
