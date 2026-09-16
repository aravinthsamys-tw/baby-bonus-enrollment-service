package sg.gov.babybonus.enrollment.enrollment

import java.util.UUID

class EnrollmentNotFoundException(
    id: UUID,
) : RuntimeException("Enrollment not found: $id")
