package sg.gov.babybonus.enrollment.api

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sg.gov.babybonus.enrollment.enrollment.EnrollmentIneligibleException
import sg.gov.babybonus.enrollment.enrollment.EnrollmentQueryService
import sg.gov.babybonus.enrollment.enrollment.EnrollmentSubmissionRequest
import sg.gov.babybonus.enrollment.enrollment.EnrollmentSubmissionService
import sg.gov.babybonus.enrollment.enrollment.EnrollmentSubmissionStatus
import java.util.UUID

@RestController
@RequestMapping("/api/v1/enrollments")
class EnrollmentController(
    private val enrollmentSubmissionService: EnrollmentSubmissionService,
    private val enrollmentQueryService: EnrollmentQueryService,
) {

    @GetMapping("/{id}")
    fun getEnrollment(@PathVariable id: UUID): EnrollmentStatusResponse =
        EnrollmentStatusResponse.from(enrollmentQueryService.getEnrollmentDetails(id))

    @PostMapping
    fun submitEnrollment(
        @Valid @RequestBody request: SubmitEnrollmentRequest,
    ): ResponseEntity<SubmitEnrollmentResponse> {
        val result = enrollmentSubmissionService.submit(
            EnrollmentSubmissionRequest(
                childNric = request.childNric,
                parentNric = request.parentNric,
            ),
        )

        return when (result.status) {
            EnrollmentSubmissionStatus.ENROLLED ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(SubmitEnrollmentResponse.from(result))

            EnrollmentSubmissionStatus.INELIGIBLE ->
                throw EnrollmentIneligibleException(
                    checkNotNull(result.reason) {
                        "Ineligible enrollment submission result must include a reason"
                    },
                )
        }
    }
}
