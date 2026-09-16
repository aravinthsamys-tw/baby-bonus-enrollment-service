package sg.gov.babybonus.enrollment.sensitive

object NricMasker {

    fun mask(nric: String): String =
        if (nric.length <= VISIBLE_PREFIX_LENGTH + VISIBLE_SUFFIX_LENGTH) {
            "*".repeat(nric.length)
        } else {
            "${nric.take(VISIBLE_PREFIX_LENGTH)}${"*".repeat(nric.length - VISIBLE_PREFIX_LENGTH - VISIBLE_SUFFIX_LENGTH)}${nric.last()}"
        }

    private const val VISIBLE_PREFIX_LENGTH = 4
    private const val VISIBLE_SUFFIX_LENGTH = 1
}
