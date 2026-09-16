package sg.gov.babybonus.enrollment.ica

import java.time.LocalDate

data class IcaChild(
    val nric: String,
    val name: String,
    val dateOfBirth: LocalDate,
    val citizenship: Citizenship,
)
