package sg.gov.babybonus.enrollment.disbursement

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import sg.gov.babybonus.enrollment.enrollment.Enrollment
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "disbursements")
class Disbursement(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    var enrollment: Enrollment,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: DisbursementType,

    @Column(nullable = false, precision = 12, scale = 2)
    var amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: DisbursementStatus = DisbursementStatus.PENDING,

    @Column(nullable = true)
    var processedAt: Instant? = null,
)
