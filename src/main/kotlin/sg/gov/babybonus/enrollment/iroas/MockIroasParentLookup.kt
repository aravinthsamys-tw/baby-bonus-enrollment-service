package sg.gov.babybonus.enrollment.iroas

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import java.nio.file.Files
import java.nio.file.Path

@Component
class MockIroasParentLookup(
    @Value("\${baby-bonus.mock-data.iroas-parents-file:mock-data/iroas_parents.json}")
    parentsFile: String,
    objectMapper: ObjectMapper,
) : IroasParentLookup {

    private val parentsByNric: Map<String, IroasParent>

    init {
        val file = Path.of(parentsFile)
        require(Files.exists(file)) { "IROAS mock data file not found: $parentsFile" }

        parentsByNric = Files.newBufferedReader(file).use { reader ->
            objectMapper.readValue<List<IroasParent>>(reader)
        }.associateBy { it.nric }
    }

    override fun findParentByNric(nric: String): IroasParent? = parentsByNric[nric]
}
