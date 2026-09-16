package sg.gov.babybonus.enrollment.api

import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EnrollmentControllerTests(
    @Autowired private val mockMvc: MockMvc,
) {

    @Test
    fun submitEnrollmentReturnsCreatedForEligibleChild() {
        mockMvc.post("/api/v1/enrollments") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "childNric": "T2400001A",
                  "parentNric": "S8001234A"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.id", notNullValue())
                jsonPath("$.status") { value("ENROLLED") }
                jsonPath("$.enrolledAt", notNullValue())
            }
    }

    @Test
    fun submitEnrollmentReturnsUnprocessableEntityForIneligibleChild() {
        mockMvc.post("/api/v1/enrollments") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "childNric": "T2499999Z",
                  "parentNric": "S8001234A"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isUnprocessableContent() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.code") { value("CHILD_NOT_FOUND") }
                jsonPath("$.message") { value("Child is not eligible for enrollment.") }
            }
    }

    @Test
    fun submitEnrollmentReturnsConflictForDuplicateChild() {
        val requestBody = """
            {
              "childNric": "T2400001A",
              "parentNric": "S8001234A"
            }
        """.trimIndent()

        mockMvc.post("/api/v1/enrollments") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }
            .andExpect {
                status { isCreated() }
            }

        mockMvc.post("/api/v1/enrollments") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }
            .andExpect {
                status { isConflict() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.code") { value("CHILD_ALREADY_ENROLLED") }
                jsonPath("$.message") { value("Child has already been enrolled.") }
            }
    }

    @Test
    fun submitEnrollmentReturnsBadRequestForBlankNric() {
        mockMvc.post("/api/v1/enrollments") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "childNric": "",
                  "parentNric": "S8001234A"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isBadRequest() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.code") { value("INVALID_REQUEST") }
                jsonPath("$.message") { value("Request contains invalid enrollment details.") }
            }
    }

    @Test
    fun submitEnrollmentReturnsBadRequestForInvalidJson() {
        mockMvc.post("/api/v1/enrollments") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "childNric": "T2400001A",
                  "parentNric":
                }
            """.trimIndent()
        }
            .andExpect {
                status { isBadRequest() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.code") { value("INVALID_REQUEST") }
                jsonPath("$.message") { value("Request body is invalid.") }
            }
    }
}
