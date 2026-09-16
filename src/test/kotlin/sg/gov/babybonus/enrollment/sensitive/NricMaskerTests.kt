package sg.gov.babybonus.enrollment.sensitive

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NricMaskerTests {

    @Test
    fun masksNricKeepingPrefixAndSuffix() {
        assertEquals("T240****A", NricMasker.mask("T2400001A"))
    }

    @Test
    fun masksShortValuesCompletely() {
        assertEquals("*****", NricMasker.mask("S1234"))
    }
}
