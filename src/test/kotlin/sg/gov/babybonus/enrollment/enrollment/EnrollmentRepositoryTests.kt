package sg.gov.babybonus.enrollment.enrollment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import sg.gov.babybonus.enrollment.TestProperties

@DataJpaTest(properties = [TestProperties.UNIQUE_H2_DATABASE])
class EnrollmentRepositoryTests(
    @Autowired private val repository: EnrollmentRepository,
) {

    @Test
    fun savesEnrollment() {
        val saved = repository.saveAndFlush(
            Enrollment(
                childNric = "T2400001A",
                parentNric = "S8001234A",
                status = EnrollmentStatus.PENDING,
            ),
        )

        val found = repository.findById(saved.id!!).orElseThrow()

        assertNotNull(saved.id)
        assertEquals(saved.id, found.id)
        assertEquals("T2400001A", found.childNric)
        assertEquals("S8001234A", found.parentNric)
        assertEquals(EnrollmentStatus.PENDING, found.status)
    }

    @Test
    fun rejectsDuplicateEnrollmentForSameChildNric() {
        repository.saveAndFlush(
            Enrollment(
                childNric = "T2400001A",
                parentNric = "S8001234A",
            ),
        )

        assertThrows<DataIntegrityViolationException> {
            repository.saveAndFlush(
                Enrollment(
                    childNric = "T2400001A",
                    parentNric = "S8205678B",
                ),
            )
        }
    }
}
