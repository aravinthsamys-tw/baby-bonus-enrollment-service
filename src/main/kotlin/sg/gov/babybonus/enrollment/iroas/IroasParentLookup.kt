package sg.gov.babybonus.enrollment.iroas

interface IroasParentLookup {
    fun findParentByNric(nric: String): IroasParent?
}
