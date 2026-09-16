package sg.gov.babybonus.enrollment.ica

interface IcaChildLookup {
    fun findChildByNric(nric: String): IcaChild?
}
