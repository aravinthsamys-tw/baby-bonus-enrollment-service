package sg.gov.babybonus.enrollment.enrollment

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EnrollmentRepository : JpaRepository<Enrollment, UUID> {
    fun existsByChildNric(childNric: String): Boolean
}
