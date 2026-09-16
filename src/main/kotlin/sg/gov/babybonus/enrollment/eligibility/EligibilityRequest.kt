package sg.gov.babybonus.enrollment.eligibility

data class EligibilityRequest(
    val childNric: String,
    val parentNric: String,
)
