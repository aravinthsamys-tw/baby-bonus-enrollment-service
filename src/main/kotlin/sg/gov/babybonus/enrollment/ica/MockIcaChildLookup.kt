package sg.gov.babybonus.enrollment.ica

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import java.nio.file.Files
import java.nio.file.Path

@Component
class MockIcaChildLookup(
    @Value("\${baby-bonus.mock-data.ica-children-file:mock-data/ica_children.json}")
    childrenFile: String,
    objectMapper: ObjectMapper,
) : IcaChildLookup {

    private val childrenByNric: Map<String, IcaChild>

    init {
        val file = Path.of(childrenFile)
        require(Files.exists(file)) { "ICA mock data file not found: $childrenFile" }

        childrenByNric = Files.newBufferedReader(file).use { reader ->
            objectMapper.readValue<List<IcaChild>>(reader)
        }.associateBy { it.nric }
    }

    override fun findChildByNric(nric: String): IcaChild? = childrenByNric[nric]
}
