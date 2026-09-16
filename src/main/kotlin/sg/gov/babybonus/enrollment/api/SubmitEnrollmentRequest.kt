package sg.gov.babybonus.enrollment.api

import jakarta.validation.constraints.NotBlank

data class SubmitEnrollmentRequest(
    @field:NotBlank
    val childNric: String,

    @field:NotBlank
    val parentNric: String,
)
