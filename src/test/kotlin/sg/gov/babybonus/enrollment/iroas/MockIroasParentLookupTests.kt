package sg.gov.babybonus.enrollment.iroas

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import java.nio.file.Path

class MockIroasParentLookupTests {

    @Test
    fun findsParentByNric(@TempDir tempDir: Path) {
        val lookup = createLookup(tempDir)

        val parent = lookup.findParentByNric("S8001234A")

        assertEquals(
            IroasParent(
                nric = "S8001234A",
                name = "Tan Ah Kow",
                relationship = Relationship.FATHER,
            ),
            parent,
        )
    }

    @Test
    fun returnsNullWhenParentIsUnknown(@TempDir tempDir: Path) {
        val lookup = createLookup(tempDir)

        assertNull(lookup.findParentByNric("S9999999Z"))
    }

    private fun createLookup(tempDir: Path): MockIroasParentLookup {
        val parentsFile = tempDir.resolve("iroas_parents.json")
        Files.writeString(
            parentsFile,
            """
            [
              {
                "nric": "S8001234A",
                "name": "Tan Ah Kow",
                "relationship": "FATHER"
              }
            ]
            """.trimIndent(),
        )

        return MockIroasParentLookup(
            parentsFile = parentsFile.toString(),
            objectMapper = jacksonObjectMapper(),
        )
    }
}
