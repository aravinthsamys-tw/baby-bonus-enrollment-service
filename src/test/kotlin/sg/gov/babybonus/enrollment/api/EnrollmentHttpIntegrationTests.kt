package sg.gov.babybonus.enrollment.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import sg.gov.babybonus.enrollment.TestProperties

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        TestProperties.API_KEY,
        TestProperties.CALLER_IDENTITY,
        TestProperties.UNIQUE_H2_DATABASE,
    ],
)
class EnrollmentHttpIntegrationTests(
    @LocalServerPort private val port: Int,
) {

    private val restTemplate = RestTemplate()

    @Test
    fun retrievesEnrollmentCreatedByPreviousHttpRequest() {
        val submitResponse = restTemplate.exchange(
            url("/api/v1/enrollments"),
            HttpMethod.POST,
            HttpEntity(
                mapOf(
                    "childNric" to "T2400001A",
                    "parentNric" to "S8001234A",
                ),
                headers(),
            ),
            SubmitEnrollmentResponse::class.java,
        )

        assertEquals(HttpStatus.CREATED, submitResponse.statusCode)
        val enrollmentId = checkNotNull(submitResponse.body?.id)

        val getResponse = restTemplate.exchange(
            url("/api/v1/enrollments/$enrollmentId"),
            HttpMethod.GET,
            HttpEntity<Unit>(headers()),
            EnrollmentStatusResponse::class.java,
        )

        assertEquals(HttpStatus.OK, getResponse.statusCode)
        assertEquals(enrollmentId, getResponse.body?.id)
        assertEquals("T240****A", getResponse.body?.childNric)
        assertEquals("ENROLLED", getResponse.body?.status?.name)
        assertNotNull(getResponse.body?.disbursement)
    }

    private fun url(path: String): String = "http://127.0.0.1:$port$path"

    private fun headers(): HttpHeaders =
        HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("X-API-Key", "test-api-key")
        }
}
