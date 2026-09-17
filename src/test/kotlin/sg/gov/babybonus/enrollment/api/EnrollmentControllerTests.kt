package sg.gov.babybonus.enrollment.api

import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import sg.gov.babybonus.enrollment.TestProperties
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@SpringBootTest(
    properties = [
        TestProperties.API_KEY,
        TestProperties.CALLER_IDENTITY,
        TestProperties.UNIQUE_H2_DATABASE,
    ],
)
@AutoConfigureMockMvc
@Transactional
@ExtendWith(OutputCaptureExtension::class)
class EnrollmentControllerTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {

    @Test
    fun getEnrollmentReturnsEnrollmentStatus() {
        val postResponse = mockMvc.post("/api/v1/enrollments") {
            header(API_KEY_HEADER, API_KEY)
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "childNric": "T2400001A",
                  "parentNric": "S8001234A"
                }
            """.trimIndent()
        }
            .andReturn()
            .response
            .contentAsString
        val enrollmentId = objectMapper.readTree(postResponse).get("id").stringValue()

        mockMvc.get("/api/v1/enrollments/$enrollmentId") {
            header(API_KEY_HEADER, API_KEY)
        }
            .andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") { value(enrollmentId) }
                jsonPath("$.childNric") { value("T240****A") }
                jsonPath("$.status") { value("ENROLLED") }
                jsonPath("$.enrolledAt", notNullValue())
                jsonPath("$.disbursement.type") { value("CASH_GIFT") }
                jsonPath("$.disbursement.amount") { value(3000.00) }
                jsonPath("$.disbursement.status") { value("PENDING") }
                jsonPath("$.parentNric") { doesNotExist() }
            }
    }

    @Test
    fun getEnrollmentReturnsNotFoundForUnknownId() {
        mockMvc.get("/api/v1/enrollments/${UUID.randomUUID()}") {
            header(API_KEY_HEADER, API_KEY)
        }
            .andExpect {
                status { isNotFound() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.code") { value("ENROLLMENT_NOT_FOUND") }
                jsonPath("$.message") { value("Enrollment not found.") }
            }
    }

    @Test
    fun submitEnrollmentReturnsCreatedForEligibleChild(output: CapturedOutput) {
        mockMvc.post("/api/v1/enrollments") {
            header(API_KEY_HEADER, API_KEY)
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

        assertTrue(output.out.contains(""""operation":"ELIGIBILITY_CHECK_RESULT""""))
        assertTrue(output.out.contains(""""operation":"DISBURSEMENT_INITIATED""""))
        assertTrue(output.out.contains(""""operation":"ENROLLMENT_SUBMITTED""""))
        assertTrue(output.out.contains(""""callerIdentity":"test-client""""))
        assertTrue(output.out.contains(""""subject":"T240****A""""))
        assertTrue(output.out.contains(""""outcome":"ENROLLED""""))
        assertFalse(output.out.contains("T2400001A"))
    }

    @Test
    fun submitEnrollmentReturnsUnprocessableEntityForIneligibleChild() {
        mockMvc.post("/api/v1/enrollments") {
            header(API_KEY_HEADER, API_KEY)
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
            header(API_KEY_HEADER, API_KEY)
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }
            .andExpect {
                status { isCreated() }
            }

        mockMvc.post("/api/v1/enrollments") {
            header(API_KEY_HEADER, API_KEY)
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
            header(API_KEY_HEADER, API_KEY)
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
            header(API_KEY_HEADER, API_KEY)
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

    @Test
    fun submitEnrollmentReturnsUnauthorizedWhenApiKeyIsMissing() {
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
                status { isUnauthorized() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.code") { value("UNAUTHORIZED") }
                jsonPath("$.message") { value("Authentication is required.") }
            }
    }

    @Test
    fun getEnrollmentReturnsUnauthorizedWhenApiKeyIsInvalid() {
        mockMvc.get("/api/v1/enrollments/${UUID.randomUUID()}") {
            header(API_KEY_HEADER, "wrong-api-key")
        }
            .andExpect {
                status { isUnauthorized() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.code") { value("UNAUTHORIZED") }
                jsonPath("$.message") { value("Authentication is required.") }
            }
    }

    private companion object {
        const val API_KEY_HEADER = "X-API-Key"
        const val API_KEY = "test-api-key"
    }
}
