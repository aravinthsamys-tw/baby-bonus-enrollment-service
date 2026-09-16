package sg.gov.babybonus.enrollment.enrollment

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "enrollments")
class Enrollment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    var childNric: String,

    @Column(nullable = false)
    var parentNric: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EnrollmentStatus = EnrollmentStatus.PENDING,

    @Column(nullable = true)
    var enrolledAt: Instant? = null,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),
)
