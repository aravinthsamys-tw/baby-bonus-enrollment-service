package sg.gov.babybonus.enrollment.iroas

data class IroasParent(
    val nric: String,
    val name: String,
    val relationship: Relationship,
)
