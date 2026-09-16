package sg.gov.babybonus.enrollment.eligibility

data class EligibilityResult(
    val eligible: Boolean,
    val reason: IneligibilityReason?,
) {
    companion object {
        fun eligible(): EligibilityResult = EligibilityResult(eligible = true, reason = null)

        fun ineligible(reason: IneligibilityReason): EligibilityResult =
            EligibilityResult(eligible = false, reason = reason)
    }
}
