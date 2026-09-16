package sg.gov.babybonus.enrollment.ica

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate

class MockIcaChildLookupTests {

    @Test
    fun findsChildByNric(@TempDir tempDir: Path) {
        val lookup = createLookup(tempDir)

        val child = lookup.findChildByNric("T2400001A")

        assertEquals(
            IcaChild(
                nric = "T2400001A",
                name = "Tan Wei Xuan",
                dateOfBirth = LocalDate.parse("2024-01-15"),
                citizenship = Citizenship.SINGAPORE_CITIZEN,
            ),
            child,
        )
    }

    @Test
    fun returnsNullWhenChildIsUnknown(@TempDir tempDir: Path) {
        val lookup = createLookup(tempDir)

        assertNull(lookup.findChildByNric("T2499999Z"))
    }

    private fun createLookup(tempDir: Path): MockIcaChildLookup {
        val childrenFile = tempDir.resolve("ica_children.json")
        Files.writeString(
            childrenFile,
            """
            [
              {
                "nric": "T2400001A",
                "name": "Tan Wei Xuan",
                "dateOfBirth": "2024-01-15",
                "citizenship": "SINGAPORE_CITIZEN"
              }
            ]
            """.trimIndent(),
        )

        return MockIcaChildLookup(
            childrenFile = childrenFile.toString(),
            objectMapper = jacksonObjectMapper(),
        )
    }
}
